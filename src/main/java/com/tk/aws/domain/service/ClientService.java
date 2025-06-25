package com.tk.aws.domain.service;

import com.amazonaws.services.lightsail.model.Region;
import com.tk.aws.domain.model.LightInstance;

import java.util.List;

public interface ClientService {

    /**
     * 开始执行创建任务
     * @param instance
     */
    void dispose(LightInstance instance);

    /**
     * 删除aws实例
     * @param instance
     */
    void del(LightInstance instance) ;

    /**
     * 获取可用区
     * @return
     */
    List<Region> regions();


    /**
     * 开放端口
     * @param instance
     */
    void openNetwork(LightInstance instance);


}
