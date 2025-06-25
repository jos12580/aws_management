package com.tk.aws.domain.service;

import com.tk.aws.domain.model.LightInstance;

public interface AwsUseDomainService {

    /**
     * 策略配置过滤
     * @param instanceType
     * @return
     */
    boolean filter(Integer instanceType);

    /**
     *  使用aws ip
     * @param devicename
     * @param instanceType
     * @param region
     * @param uname
     * @return
     */
    LightInstance used(String devicename, Integer instanceType, String region, String uname);


}
