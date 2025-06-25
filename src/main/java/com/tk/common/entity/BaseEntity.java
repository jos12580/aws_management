package com.tk.common.entity;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class BaseEntity implements Serializable {

    protected static final long serialVersionUID = 1L;

    protected String username;

    @TableId(value = "id", type = IdType.AUTO)
    protected Long id;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    protected Long createTime;

    public String getCreateTimeStr() {
        if (createTime == null) {
            return "";
        }
        return DateUtil.formatDateTime(new Date(createTime));
    }

}
