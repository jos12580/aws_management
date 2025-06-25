package com.tk.aws.infrastructure.schedule;


import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.tk.aws.domain.model.AwsKeyConfig;
import com.tk.aws.domain.model.LightInstance;
import com.tk.aws.domain.repository.AwsKeyConfigRepository;
import com.tk.aws.domain.repository.LightInstanceRepository;
import com.tk.aws.domain.service.impl.ClientServiceImpl;
import com.tk.aws.infrastructure.config.AmazonClient;
import com.tk.aws.infrastructure.enums.LightInstanceStaEnum;
import com.tk.aws.service.LightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * @author Administrator
 */
@Component
@Slf4j
public class IpRedisJob {

    @Resource
    private LightInstanceRepository lightInstanceRepository;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private LightService lightService;
    @Resource
    private AwsKeyConfigRepository awsKeyConfigRepository;

    int ipRedisExpireTime = 2398;


    @Scheduled(cron = "0 1/10 * * * ?")
    public void findIpNoCallback() {
        String lockKey = "IpRedisJob.findIpNoCallback";
        Boolean absent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 3, TimeUnit.MINUTES);
        if (Boolean.FALSE.equals(absent)) {
            return;
        }
        List<LightInstance> list = lightInstanceRepository.findBySta(LightInstanceStaEnum.CREATING);
        long time = DateUtil.offsetMinute(new Date(), -10).getTime();
        List<LightInstance> collect = list.stream().filter(i -> i.getCreateTime() < time).toList();
        for (LightInstance instance : collect) {
            try {
                //  删除实例，新增新实例
                log.warn("删除实例，新增新实例");
                lightService.del(instance.getInstanceName());
                Thread.sleep(5000);
                lightService.create(1, instance.getRegion(), instance.getInstanceType(), instance.getAwsKeyConfigId());
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }


    /**
     * 重置许久不用ip
     * 实例类型 0 抢包； 1 上号; 2 采集
     */
    @Scheduled(cron = "0 6/3 * * * ?")
    public void reSetSleepIp() {
        String lockKey = "IpRedisJob.reSetSleepIp";
        Boolean absent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 3, TimeUnit.MINUTES);
        if (Boolean.FALSE.equals(absent)) {
            return;
        }
        Date date = new Date();
//        抢包4小时唤醒
        long time = DateUtil.offsetHour(date, -2).getTime();
        lightInstanceRepository.weekUpIpBeforeTime(time, 0);
//        上号一小时唤醒
        time = DateUtil.offsetHour(date, -1).getTime();
        lightInstanceRepository.weekUpIpBeforeTime(time, 1);
    }


    /**
     * 删除删除失败数据
     */
    @Scheduled(cron = "0 15/30 * * * ?")
    public void delDelFailIp() {
        String lockKey = "IpRedisJob.delDelFailIp";
        Boolean absent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 3, TimeUnit.MINUTES);
        if (Boolean.FALSE.equals(absent)) {
            return;
        }
        Set<String> keys = redisTemplate.keys(String.format(ClientServiceImpl.LIGHT_DEL_KEY, "*"));
        if (ObjectUtils.isEmpty(keys)) {
            return;
        }
        keys.forEach(k -> {
//            默认2400小时，小于2398就删除
            Long expire = redisTemplate.getExpire(k, TimeUnit.HOURS);
            if (!ObjectUtils.isEmpty(expire) && expire < ipRedisExpireTime) {
                String str = redisTemplate.opsForValue().get(k);
                AmazonClient client;
                try {
                    LightInstance instance = JSON.parseObject(str, LightInstance.class);
                    AwsKeyConfig awsKeyConfig = awsKeyConfigRepository.getById(instance.getAwsKeyConfigId());
                    client = ClientServiceImpl.getClient(awsKeyConfig, instance.getRegion());
                } catch (Exception e) {
                    AwsKeyConfig awsKeyConfig = awsKeyConfigRepository.getByLoginName(null);
                    client = ClientServiceImpl.getClient(awsKeyConfig, str);
                }
                String[] split = k.split(":");
                try {
                    log.info("删除实例 {}", split[2]);
                    client.deleteInstance(split[2]);
                } catch (Exception e) {
                    log.error("", e);
                }
                redisTemplate.delete(k);
            }
        });
    }


    /**
     * 删除创建失败数据
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void delFailIp() {
        String lockKey = "IpRedisJob.delFailIp";
        Boolean absent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 3, TimeUnit.MINUTES);
        if (Boolean.FALSE.equals(absent)) {
            return;
        }
        Set<String> keys = redisTemplate.keys(String.format(ClientServiceImpl.LIGHT_NAME_KEY, "*"));
        if (ObjectUtils.isEmpty(keys)) {
            return;
        }
        keys.forEach(k -> {
            try {
                //            默认2400小时，小于2398就删除
                Long expire = redisTemplate.getExpire(k, TimeUnit.HOURS);
                if (!ObjectUtils.isEmpty(expire) && expire < ipRedisExpireTime) {
                    String instanceName = redisTemplate.opsForValue().get(k);
                    redisTemplate.delete(k);
                    lightService.del(instanceName);
                }
            } catch (Exception e) {
                log.error("k=>{}", k);
                log.error("", e);
            }

        });
    }


}
