package com.tk.aws.domain.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tk.aws.controller.dto.LightInstanceListReqDTO;
import com.tk.aws.domain.model.LightInstance;
import com.tk.aws.infrastructure.enums.LightInstanceStaEnum;

import java.util.List;
import java.util.Map;

public interface LightInstanceRepository  extends IService<LightInstance> {

    /**
     * 通过实例名称获取实例
     * @param instanceName
     * @return
     */
    LightInstance getByInstanceName(String instanceName);


    /**
     * 通过设备名称获取实例
     * @param devicename
     * @param instanceType
     * @param region
     * @return
     */
    LightInstance findByDeviceName(String devicename, Integer instanceType, String region);

    /**
     * 唤醒IP
     *
     * @param time
     * @param instanceType
     */
    void weekUpIpBeforeTime(long time, int instanceType);

    /**
     * 分页搜索
     * @param params
     * @return
     */
    Page<LightInstance> search(LightInstanceListReqDTO params);

    /**
     * 统计空闲实例
     * @param instanceType
     * @return
     */
    long countFreeNum(Integer instanceType);

    /**
     * 统计区域实例
     * @param instanceType
     * @return
     */
    List<Map<String, Object>> countByRegion(Integer instanceType);

    /**
     * 根据类型获取IP
     * @param instanceType
     * @return
     */
    List<LightInstance>  canUseList(Integer instanceType);

    /**
     * 根据类型统计IP
     * @param instanceType
     * @return
     */
    long count(Integer instanceType);


    /**
     * 通过设备获取ip，如不存在，则返回可用ip
     * @param devicename
     * @param instanceType
     * @param region
     * @return
     */
    LightInstance findByDeviceNameOrNull(String devicename, Integer instanceType, String region);

    /**
     * 通过名称跟类型统计ip
     * @param uname
     * @param instanceType
     * @return
     */
    long countByUnameAndType(String uname, Integer instanceType);

    /**
     *      * 根据keyConfigId获取实例
     * @param awsConfigId
     * @return
     */
    List<LightInstance> getByKeyConfigId(Long awsConfigId);

    List<LightInstance> free(Integer instanceType);

    List<LightInstance> findBySta(LightInstanceStaEnum creating);
}
