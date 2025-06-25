package com.tk.aws.domain.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.tk.aws.domain.model.LightInstance;
import com.tk.aws.domain.repository.LightInstanceRepository;
import com.tk.aws.domain.service.AwsUseDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * 采集aws ip
 */
@Service
@Slf4j
public class CaiJiAwsUseDomainServiceImpl implements AwsUseDomainService {

    @Resource
    private LightInstanceRepository lightInstanceRepository;
    @Override
    public boolean filter(Integer instanceType) {
        return Integer.valueOf(2).equals(instanceType);
    }

    @Override
    public LightInstance used(String devicename, Integer instanceType, String region, String uname) {
        List<LightInstance> list = lightInstanceRepository.canUseList(instanceType);
        if(ObjectUtils.isEmpty(list)){
            return null;
        }
        Optional<LightInstance> optional = list.stream().filter(i -> StrUtil.equals(devicename, i.getDevicename())).findFirst();
        if (optional.isPresent()){
            return optional.get();
        }
        list = list.stream().filter(i -> StrUtil.isBlank(i.getDevicename())).collect(Collectors.toList());
        return list.get(RandomUtil.randomInt(0, list.size()));
    }



}
