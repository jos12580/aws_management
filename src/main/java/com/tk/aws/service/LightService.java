package com.tk.aws.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tk.aws.controller.dto.LightInstanceListReqDTO;
import com.tk.aws.domain.model.AwsKeyConfig;
import com.tk.aws.domain.model.LightInstance;
import com.tk.common.vo.R;

import java.util.List;
import java.util.Map;

public interface LightService {


    /**
     * 创建实例
     * @param num
     * @param region
     * @param instanceType
     * @param awsKeyConfigId
     * @return
     */
     R<Object> create(int num, String region, Integer instanceType, Long awsKeyConfigId);

    /**
     * 删除ip
     * @param instanceName
     * @return
     */
    String del(String instanceName);

    /**
     * 获取可用区
     * @return
     */
    List<Map<Object, Object>> regions();

    /**
     * 使用ip
     * @param devicename
     * @param instanceType
     * @param region
     * @param uname
     * @return
     */
    LightInstance used(String devicename, Integer instanceType, String region, String uname);



    /**
     * 分页查询
     * @param params
     * @return
     */
    IPage<LightInstance> lightInstanceList(LightInstanceListReqDTO params);

    /**
     * 空闲IP
     * @param instanceType
     * @return
     */
    R<Map<Object, Object>> freeNum(Integer instanceType);

    /**
     * 根据区域ip分组
     * @param instanceType
     * @return
     */
    R<List<Map<String, Object>>> groupByRegion(Integer instanceType);

    /**
     * 随机使用
     * @param instanceType
     * @return
     */
    R<LightInstance> randomUse(Integer instanceType);

    /**
     * 根据类型获取所有ip
     * @param instanceType
     * @return
     */
    R<List<String>> all(Integer instanceType);



    /**
     * 实例创建回调
     * @param instanceName
     * @param s
     */
    void callback(String instanceName, String s);

    /**
     * ip可用测试
     * @param host
     * @return
     */
    Object ipTest(String host) throws InterruptedException;

    /**
     * 更换ip，通过消息队列
     * @param instanceName
     * @return
     */
    String changeIpByMq(String instanceName);

    /**
     *  获取aws账号列表
     * @return
     */
    List<AwsKeyConfig> lightsailKey();

    /**
     *  获取ip列表
     * @param id
     * @return
     */
    List<LightInstance> getByKeyConfigId(Long id);

    /**
     * 根据ID获取实例
     * @param id
     * @return
     */
    LightInstance getById(Long id);

    List<LightInstance> free(Integer instanceType);
}
