package com.tk.aws.domain.model;


import com.baomidou.mybatisplus.annotation.*;
import com.tk.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 亚马逊账号配置
 */
@EqualsAndHashCode(callSuper = true)
@TableName("aws_key_config")
@Data
public class AwsKeyConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;



    private  String accessKey;

    private  String secretKey;

    private  String loginName;



}
