package com.tk.server.application.dto;


import com.tk.server.domain.model.User;
import lombok.Data;

import java.io.Serializable;


@Data
public class UserLoginRespDTO implements Serializable {


    private String userId;

    private String username;

    private String account;

    /**
     * 0可用 1禁用
     */
    private Integer status;

    private String role;

    private String token;

    /**
     * 登录类型  0 全部， 1 只能登录后台， 2 只能登录脚本
     */
    private Integer loginType;



    public static UserLoginRespDTO convert(User user) {
        UserLoginRespDTO it = new UserLoginRespDTO();
        it.setUserId(user.getUserId());
        it.setAccount(user.getAccount());
        it.setUsername(user.getUsername());
        it.setToken(user.genToken());
        it.setRole(user.getRole());
        return it;
    }
}
