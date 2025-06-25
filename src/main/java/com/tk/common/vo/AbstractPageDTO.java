package com.tk.common.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AbstractPageDTO implements Serializable {

    protected static final long serialVersionUID = 1L;

    /**
     *
     */
    protected Integer size = 10;

    /**
     * 当前页
     */
    protected Integer current = 1;



}
