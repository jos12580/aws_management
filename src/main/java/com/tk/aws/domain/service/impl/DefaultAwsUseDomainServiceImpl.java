package com.tk.aws.domain.service.impl;

import com.tk.aws.domain.model.LightInstance;
import com.tk.aws.domain.service.AwsUseDomainService;
import com.tk.common.exception.GlobalException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
@Primary
public class DefaultAwsUseDomainServiceImpl implements AwsUseDomainService {

    @Resource
    private List<AwsUseDomainService> awsUseDomainServiceList;


    @Override
    public boolean filter(Integer instanceType) {
        return false;
    }

    @Override
    public LightInstance used(String devicename, Integer instanceType, String region, String uname) {
        Optional<AwsUseDomainService> optional = awsUseDomainServiceList.stream().filter(i -> i.filter(instanceType)).findFirst();
        if(optional.isPresent()){
            return optional.get().used(devicename,instanceType, region, uname);
        }
        throw new GlobalException("instanceType 不存在");
    }




}
