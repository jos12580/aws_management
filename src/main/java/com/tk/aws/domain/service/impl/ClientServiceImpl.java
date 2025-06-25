package com.tk.aws.domain.service.impl;

import com.alibaba.fastjson.JSON;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lightsail.model.Instance;
import com.amazonaws.services.lightsail.model.Region;
import com.tk.aws.domain.model.AwsKeyConfig;
import com.tk.aws.domain.model.LightInstance;
import com.tk.aws.domain.repository.AwsKeyConfigRepository;
import com.tk.aws.domain.repository.LightInstanceRepository;
import com.tk.aws.domain.service.ClientService;
import com.tk.aws.infrastructure.config.AmazonClient;
import com.tk.aws.infrastructure.config.AwsLightsailConfig;
import com.tk.aws.infrastructure.enums.LightInstanceStaEnum;
import com.tk.aws.service.LightService;
import com.tk.common.util.IpUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ClientServiceImpl implements ClientService  {

    @Resource
    private LightInstanceRepository lightInstanceRepository;
    @Resource
    private RedisTemplate<String, String> redisTemplate;



    @Resource
    @Lazy
    private LightService lightService;

    @Resource
    private AwsKeyConfigRepository awsKeyConfigRepository;

    public static final String LIGHT_NAME_KEY = "Light-Name-Key:create:%s";

    public static final String LIGHT_DEL_KEY = "Light-Name-Key:del:%s";

    private static final String IP_SINGLE_COUNT = "IP_SINGLE_COUNT";

    @Value("${aws.callback.awsBaseUrl}")
    private String awsBaseUrl;


    public static AmazonClient getClient(AwsKeyConfig awsKeyConfig, String region) {
        if (Objects.isNull(awsKeyConfig)) {
            throw new RuntimeException("awsKeyConfig 密钥不存在");
        }
        AwsLightsailConfig config = new AwsLightsailConfig(awsKeyConfig.getAccessKey(), awsKeyConfig.getSecretKey());
        return AmazonClient.build(config, region);
    }

    @Async
    @SneakyThrows
    @Override
    public void dispose(LightInstance instance) {
        //  处理前把任务id存到缓存。处理完成后删除
        redisTemplate.opsForValue().set(String.format(LIGHT_NAME_KEY, instance.getInstanceName()), instance.getInstanceName(), 2400, TimeUnit.HOURS);
        AwsKeyConfig awsKeyConfig = awsKeyConfigRepository.getById(instance.getAwsKeyConfigId());
        AmazonClient client = getClient(awsKeyConfig, instance.getRegion());
        try {
//            创建实例
            log.info("创建实例 {}", instance.getInstanceName());
            client.createInstance(instance.getInstanceName(), instance.getRegion(), awsBaseUrl);
            instance.setSta(LightInstanceStaEnum.CREATING);
        } catch (Exception e) {
            e.printStackTrace();
            instance.setError(e.getMessage());
        }
        lightInstanceRepository.updateById(instance);
    }

    @Override
    public void openNetwork(LightInstance instance) {
        AwsKeyConfig awsKeyConfig = awsKeyConfigRepository.getById(instance.getAwsKeyConfigId());
        AmazonClient client = getClient(awsKeyConfig, instance.getRegion());
        log.info("开放实例端口 {}", instance.getInstanceName());
        client.openNetwork(instance.getInstanceName());
        Instance detail = client.detail(instance.getInstanceName());
        instance.setIp(detail.getPublicIpAddress());
        redisTemplate.delete(String.format(LIGHT_NAME_KEY, instance.getInstanceName()));
        try {
            IpUtils.sk5Test(instance.getIp(), 11688);
            instance.readyUse();
            lightInstanceRepository.updateById(instance);
        } catch (Exception e) {
            log.error("", e);
            lightInstanceRepository.updateById(instance);
            log.error("测试ip {} 不通，重新创建实例 {}",instance.getIp(),instance.getInstanceName());
            lightService.del(instance.getInstanceName());
            lightService.create(1,instance.getRegion(), instance.getInstanceType(), instance.getAwsKeyConfigId());
        }
    }

    @SneakyThrows
    @Async
    @Override
    public void del(LightInstance instance) {
        redisTemplate.opsForValue().set(String.format(LIGHT_DEL_KEY, instance.getInstanceName()), JSON.toJSONString(instance), 2400, TimeUnit.HOURS);
        AwsKeyConfig awsKeyConfig = awsKeyConfigRepository.getById(instance.getAwsKeyConfigId());
        AmazonClient client = getClient(awsKeyConfig, instance.getRegion());
        log.info("删除实例 {}", instance.getInstanceName());
        client.deleteInstance(instance.getInstanceName());
        redisTemplate.delete(String.format(LIGHT_DEL_KEY, instance.getInstanceName()));
    }


    @Override
    public List<Region> regions() {
        AwsKeyConfig awsKeyConfig = awsKeyConfigRepository.getByLoginName(null);
        AmazonClient client = getClient(awsKeyConfig, Regions.DEFAULT_REGION.getName());
        return client.getRegions();
    }



}
