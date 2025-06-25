package com.tk.server.application.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tk.common.exception.GlobalException;
import com.tk.common.vo.R;
import com.tk.server.application.dto.UserListReqDTO;
import com.tk.server.application.dto.UserLoginRespDTO;
import com.tk.server.domain.model.User;
import com.tk.server.domain.repository.IUserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;

@Service
public class UserApplication {

    @Resource
    private IUserService userService;


    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public UserLoginRespDTO login(String account, String password) {
        User user = userService.get(account, password);
        if (ObjectUtils.isEmpty(user)) {
            throw new GlobalException("账号或密码错误");
        }
        if (!user.canUse()) {
            throw new GlobalException("账号已被禁用");
        }
        return UserLoginRespDTO.convert(user);
    }


    public R<Page<User>> list(UserListReqDTO params) {
        Page<User> page = userService.paged(params);
        page.getRecords().forEach(i -> i.setPassword("*"));
        return R.ok(page);
    }

    /**
     * 5分钟同步一次账号信息
     */
    @Scheduled(cron = "0 0/5 * * * *")
    public void syncAccount() {
        userService.syncAccount();
    }


    public void status(String account, Integer status) {
        User user = userService.getByAccount(account);
        if (ObjectUtils.isEmpty(user)) {
            return;
        }
        user.setStatus(Integer.valueOf(0).equals(status) ? 0 : 1);
        userService.saveOrUpdate(user);
        userService.syncAccount();
    }



}
