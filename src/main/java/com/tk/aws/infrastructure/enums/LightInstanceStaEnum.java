package com.tk.aws.infrastructure.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 实例状态
 */
@Getter
public enum LightInstanceStaEnum {

    /**
     * 初始化
     */
    INIT("初始化",0),
    CREATING("创建中",1),
    RUNNING("已运行",2),
    CLOSING("关闭中",3),
    CLOSE("已关闭",4),
    DESTORY("销毁中",5)



    ;

    private final String desc;

    @EnumValue
    private final Integer code;

    LightInstanceStaEnum(String desc, Integer code) {
        this.desc = desc;
        this.code = code;
    }
}
