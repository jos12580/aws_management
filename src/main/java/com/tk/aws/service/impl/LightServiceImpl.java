package com.tk.aws.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.amazonaws.services.lightsail.model.Region;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tk.aws.controller.dto.LightInstanceListReqDTO;
import com.tk.aws.domain.model.AwsKeyConfig;
import com.tk.aws.domain.model.LightInstance;
import com.tk.aws.domain.repository.AwsKeyConfigRepository;
import com.tk.aws.domain.repository.LightInstanceRepository;
import com.tk.aws.domain.service.AwsUseDomainService;
import com.tk.aws.domain.service.ClientService;
import com.tk.aws.domain.service.MyForestClient;
import com.tk.aws.infrastructure.enums.CanUseRegionEnum;
import com.tk.aws.service.AwsUserKeyService;
import com.tk.aws.service.LightService;
import com.tk.common.exception.GlobalException;
import com.tk.common.util.ConstantConf;
import com.tk.common.vo.R;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LightServiceImpl implements LightService {

    @Resource
    private LightInstanceRepository lightInstanceRepository;

    @Resource
    private ClientService clientService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private AwsUseDomainService awsUseDomainService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private AwsKeyConfigRepository awsKeyConfigRepository;

    @Resource
    @Lazy
    private AwsUserKeyService awsUserKeyService;

    @Resource
    private MyForestClient forestClient;

    int maxCreateOneTime = 50;


    @Override
    public R<Object> create(int num, String region, Integer instanceType, Long awsKeyConfigId) {
        if (num > maxCreateOneTime || num < 1) {
            return R.fail("单次创建实例数量为1~50");
        }
        AwsKeyConfig awsKeyConfig = awsKeyConfigRepository.getById(awsKeyConfigId);
        if (ObjectUtils.isEmpty(awsKeyConfig)) {
            return R.fail("密钥不存在");
        }
        List<LightInstance> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            list.add(LightInstance.newInstance(region, instanceType, awsKeyConfig));
        }
        lightInstanceRepository.saveBatch(list);
        list.forEach(i -> clientService.dispose(i));
        return R.ok();
    }


    @Override
    public String del(String instanceName) {
        LightInstance instance = lightInstanceRepository.getByInstanceName(instanceName);
        if (Objects.isNull(instance)) {
            throw new GlobalException("实例不存在");
        }
        clientService.del(instance);
        lightInstanceRepository.removeById(instance.getId());
        return instance.getInstanceName();
    }

    @Override
    public List<Map<Object, Object>> regions() {
        List<Region> list = clientService.regions();
        List<CanUseRegionEnum> enums = CanUseRegionEnum.find(list.stream().map(Region::getName).collect(Collectors.toList()));
        return enums.stream().map(i -> {
            HashMap<Object, Object> map = new HashMap<>(16);
            map.put("name", i.getName());
            map.put("chineseName", i.getChineseName());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public LightInstance used(String devicename, Integer instanceType, String region, String uname) {
        RLock lock = redissonClient.getLock("ip:use:" + uname + instanceType);
        try {
            lock.lock();
            if (!StringUtils.hasText(uname)) {
                throw new GlobalException("登录已过期");
            }
            long count = lightInstanceRepository.countByUnameAndType(uname, instanceType);
            String intStr = redisTemplate.opsForValue().get(String.format(ConstantConf.AWS_MAX_USE_NUM, uname));
            if (!StringUtils.hasText(intStr) || count >= Integer.parseInt(intStr)) {
                throw new GlobalException("超出最大限制");
            }
            LightInstance instance = awsUseDomainService.used(devicename, instanceType, region, uname);
            if (Objects.isNull(instance) || !instance.getNetCanUsed()) {
                throw new GlobalException("无可用ip");
            }
            instance.use(devicename);
            instance.setUsername(uname);
            lightInstanceRepository.updateById(instance);
            return instance;
        } finally {
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
    }


    @Override
    public String changeIpByMq(String instanceName) {
        RLock lock = redissonClient.getLock("ip:change:" + instanceName);
        try {
            lock.lock();
            LightInstance instance = lightInstanceRepository.getByInstanceName(instanceName);
            if (ObjectUtils.isEmpty(instance)) {
                throw new GlobalException("实例不存在");
            }
            if (!instance.getNetCanUsed()) {
                throw new GlobalException("实例不可用");
            }
            //  删除实例，新增新实例
            this.del(instance.getInstanceName());
            this.create(1, instance.getRegion(), instance.getInstanceType(), instance.getAwsKeyConfigId());
            return instance.getInstanceName();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<AwsKeyConfig> lightsailKey() {
        return awsKeyConfigRepository.list();
    }

    @Override
    public List<LightInstance> getByKeyConfigId(Long id) {
        return lightInstanceRepository.getByKeyConfigId(id);
    }

    @Override
    public LightInstance getById(Long id) {
        return lightInstanceRepository.getById(id);
    }

    @Override
    public List<LightInstance> free(Integer instanceType) {
        List<LightInstance> list = lightInstanceRepository.free(instanceType);
        return list;
    }

    @Override
    public IPage<LightInstance> lightInstanceList(LightInstanceListReqDTO params) {
        return lightInstanceRepository.search(params);
    }

    @Override
    public R<Map<Object, Object>> freeNum(Integer instanceType) {
        long num = lightInstanceRepository.countFreeNum(instanceType);
        long count = lightInstanceRepository.count(instanceType);
        Map<Object, Object> map = new HashMap<>(16);
        map.put("freeNum", num);
        map.put("totalNum", count);
        return R.ok(map);
    }

    @Override
    public R<List<Map<String, Object>>> groupByRegion(Integer instanceType) {
        List<Map<String, Object>> list = lightInstanceRepository.countByRegion(instanceType);
        list.forEach(i -> {
            CanUseRegionEnum regionEnum = CanUseRegionEnum.find(i.get("region").toString());
            i.put("regionChineseName", Objects.isNull(regionEnum) ? "未匹配" : regionEnum.getChineseName());
        });
        return R.ok(list);
    }

    @Override
    public R<LightInstance> randomUse(Integer instanceType) {
        List<LightInstance> list = lightInstanceRepository.canUseList(instanceType);
        if (list.isEmpty()) {
            return R.fail("暂无数据");
        }
        LightInstance instance = list.get(RandomUtil.randomInt(0, list.size()));
        return R.ok(instance);
    }

    @Override
    public R<List<String>> all(Integer instanceType) {
        List<LightInstance> list = lightInstanceRepository.canUseList(instanceType);
        if (list.isEmpty()) {
            return R.ok(Collections.emptyList());
        }
        List<String> collect = list.stream().map(LightInstance::getIp).collect(Collectors.toList());
        return R.ok(collect);
    }


    @Override
    @Async
    @SneakyThrows
    public void callback(String instanceName, String str) {
        LightInstance instance = lightInstanceRepository.getByInstanceName(instanceName);
        if (ObjectUtils.isEmpty(instance)) {
            log.error("回调的示例信息不存在：{}", instanceName);
            return;
        }
        if (StringUtils.hasText(str)) {
            String vlessConf = "vless://" + str.split("------------- END -----")[0].split("-reality-")[0].split("vless://")[1];
            log.info("vlessConf = {}", vlessConf);
            instance.setInfo(vlessConf.trim());
            lightInstanceRepository.saveOrUpdate(instance);
        }
        clientService.openNetwork(instance);
    }

    @SneakyThrows
    @Override
    public Object ipTest(String host) {
        String ipInfo;
        try{
             ipInfo = forestClient.showIpInfo(host, "11688", "10010", "10010");
        } catch (Exception e) {
             ipInfo = forestClient.showIpInfo(host, "11688", "10010", "10010");
        }
        if (StringUtils.hasText(ipInfo)) {
            return JSON.parseObject(ipInfo);
        }
        return ipInfo;
    }


}
