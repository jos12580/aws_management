package com.tk.aws.domain.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tk.aws.domain.model.AwsKeyConfig;

public interface AwsKeyConfigRepository extends IService<AwsKeyConfig> {


    /**
     * 根据登录名获取,没有就获取默认值
     * @param loginName
     * @return
     */
    AwsKeyConfig getByLoginName(String loginName);

}
