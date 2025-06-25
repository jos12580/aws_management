package com.tk.server.infrastructure.enums;


import com.tk.common.exception.GlobalException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 角色枚举
 */
@Getter
public enum TkRoleEnum {

    /**
     * 系统管理员
     */
    ADMIN("系统管理员","admin"),
    /**
     * 子系统管理员
     */
    SUB_ADMIN("子系统管理员","subAdmin"),
    /**
     * 员工
     */
    EMPLOYEE("员工","employee"),

    ;
    private final String title;
    private final String desc;

    TkRoleEnum(String desc,String title) {
        this.title = title;
        this.desc = desc;
    }

    public static TkRoleEnum find(String title){
        Optional<TkRoleEnum> first = Arrays.stream(TkRoleEnum.values()).filter(i -> i.title.equals(title)).findFirst();
        if(first.isPresent()){
            return first.get();
        }
        throw new GlobalException("角色不存在");
    }
}
