package com.tk.aws.controller.dto;

import com.tk.common.vo.AbstractPageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LightInstanceListReqDTO extends AbstractPageDTO {

    private String search;


    /**
     * 实例类型 0 抢包； 1 上号
     */
    private Integer instanceType;

    private Long awsKeyConfigId;

    private String  region;

}
