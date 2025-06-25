package com.tk.common.vo;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通用分页
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommonPageDTO extends AbstractPageDTO {

    protected String search;

    protected String username;


}
