package com.tk.aws.service;

import com.tk.aws.domain.model.AwsKeyConfig;

import java.util.List;

public interface AwsUserKeyService {

    /**
     * 添加aws账号
     * @param reqDTO
     * @return
     */
    AwsKeyConfig add(AwsKeyConfig reqDTO);

    /**
     * 更新aws账号
     * @param reqDTO
     * @return
     */
    AwsKeyConfig update(AwsKeyConfig reqDTO);

    /**
     * 根据ID获取aws账号
     * @param id
     * @return
     */
    AwsKeyConfig getById(Long id);

    /**
     * 删除aws账号
     * @param id
     * @return
     */

    Long del(Long id);

    /**
     * 获取aws账号列表
     * @return
     */
    List<AwsKeyConfig> list();
}
