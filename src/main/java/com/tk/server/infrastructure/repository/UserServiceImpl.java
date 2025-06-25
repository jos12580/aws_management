package com.tk.server.infrastructure.repository;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tk.server.application.dto.UserListReqDTO;
import com.tk.server.domain.model.User;
import com.tk.server.domain.repository.IUserService;
import com.tk.server.infrastructure.mapper.UserMapper;
import com.tk.common.util.ConstantConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author author
 * @since 2023-03-03
 */

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    private static final String USER_KEY = ConstantConf.userKey;


    @Override
    public User get(String account, String password) {
        String str = redisTemplate.opsForValue().get(String.format(USER_KEY, account));
        if(StringUtils.hasText(str)){
            User user = JSON.parseObject(str, User.class);
            if(user.getPassword().equals(password)){
                return user;
            }
        }
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(User::getAccount,account)
                .eq(User::getPassword,password)
                .last("limit 1");
        User user = this.getOne(wrapper);
        if(!ObjectUtils.isEmpty(user)&&user.canUse()) {
            redisTemplate.opsForValue().set(String.format(USER_KEY, user.getAccount().toLowerCase()), JSON.toJSONString(user), 10, TimeUnit.MINUTES);
        }
        return user;
    }

    @Override
    public Page<User> paged(UserListReqDTO params) {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        return this.page( new Page<>(params.getCurrent(), params.getSize()),wrapper);
    }

    @Override
    public void syncAccount() {
        List<User> list = this.list();
        List<String> stringList = list.stream().map(i -> i.getAccount()).collect(Collectors.toList());
        Set<String> keys = redisTemplate.keys(String.format(USER_KEY, "*"));
        for (String key : keys) {
            String[] split = key.split(":");
            if(!ObjectUtils.isEmpty(split)&&split.length>1&&!stringList.contains(split[1])){
                redisTemplate.delete(key);
            }
        }
        List<String> collect = list.stream().filter(i -> !i.canUse()).map(i -> String.format(USER_KEY, i.getAccount())).collect(Collectors.toList());
        if(!ObjectUtils.isEmpty(collect)) {
            redisTemplate.delete(collect);
        }
        list.stream().filter(User::canUse).forEach(i->{
            redisTemplate.opsForValue().set(String.format(USER_KEY, i.getAccount()),JSON.toJSONString(i),10, TimeUnit.MINUTES);

            redisTemplate.opsForValue().set(String.format(ConstantConf.AWS_MAX_USE_NUM, i.getUsername()),i.getMaxDvNum().toString());
        });
    }

    @Override
    public User getByAccount(String account) {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(User::getAccount,account)
                .last("limit 1");
        return this.getOne(wrapper);
    }

    @Override
    public void removeBatchByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(User::getUsername,username);
        this.remove(wrapper);
    }

    @Override
    public Page<User> paged(UserListReqDTO dto, int showed) {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(User::getShowed,showed)
                .eq(User::getStatus,0);
        return this.page( new Page<>(dto.getCurrent(), dto.getSize()),wrapper);
    }

    @Override
    public List<User> getByRole(String role) {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(User::getRole,role);
        return this.list(wrapper);
    }


}
