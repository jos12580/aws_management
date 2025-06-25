package com.tk.aws.domain.service.impl;

import com.tk.aws.domain.model.LightInstance;
import com.tk.aws.domain.repository.LightInstanceRepository;
import com.tk.aws.domain.service.AwsUseDomainService;
import com.tk.aws.service.LightService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;


/**
 * 注册 aws ip
 */
@Service
public class RegeditAwsUseDomainServiceImpl implements AwsUseDomainService {

    @Resource
    private LightInstanceRepository lightInstanceRepository;
    @Resource
    @Lazy
    private LightService lightService;

    @Override
    public boolean filter(Integer instanceType) {
        return Integer.valueOf(3).equals(instanceType);
    }

    @Override
    public LightInstance used(String devicename, Integer instanceType, String region, String uname) {
        LightInstance instance = lightInstanceRepository.findByDeviceName(devicename, instanceType, region);
        if (Objects.isNull(instance)||!instance.getNetCanUsed()) {
            instance = lightInstanceRepository.findByDeviceName(null, instanceType, region);
        }
        if(Objects.nonNull(instance)&&instance.getNetCanUsed()&&instance.getUseCount()>=1){
            lightService.del(instance.getInstanceName());
            lightService.create(1, instance.getRegion(), instance.getInstanceType(), instance.getAwsKeyConfigId());
            instance=null;
        }
        return instance;
    }


}
