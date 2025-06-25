package com.tk.aws.domain.service.impl;

import com.tk.aws.domain.model.LightInstance;
import com.tk.aws.domain.repository.LightInstanceRepository;
import com.tk.aws.domain.service.AwsUseDomainService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;


/**
 * 抢包aws ip
 */
@Service
public class PickAwsUseDomainServiceImpl implements AwsUseDomainService {
    @Resource
    private LightInstanceRepository lightInstanceRepository;

    @Override
    public boolean filter(Integer instanceType) {
        return Integer.valueOf(0).equals(instanceType);
    }

    @Override
    public LightInstance used(String devicename, Integer instanceType, String region, String uname) {
        LightInstance instance=lightInstanceRepository.findByDeviceNameOrNull(devicename, instanceType,region);
        if (Objects.isNull(instance)){
            instance=lightInstanceRepository.findByDeviceNameOrNull(devicename, instanceType,null);
        }
        return instance;
    }


}
