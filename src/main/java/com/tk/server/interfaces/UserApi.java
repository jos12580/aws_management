package com.tk.server.interfaces;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tk.common.annotation.NoApiValidate;
import com.tk.common.annotation.UName;
import com.tk.common.vo.R;
import com.tk.server.application.dto.UserConfigDTO;
import com.tk.server.application.dto.UserListReqDTO;
import com.tk.server.application.dto.UserLoginRespDTO;
import com.tk.server.application.impl.UserApplication;
import com.tk.server.domain.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author author
 * @since 2023-03-03
 */
@Slf4j
@RestController
@RequestMapping("/management/api/user")
@Validated
@Tag(name = "用户")
public class UserApi {

    @Resource
    private UserApplication application;
    @Resource
    private RedisTemplate<String,String> redisTemplate;

    private static final String USER_CONFIG_KEY="user_config_%s";


    @Operation(summary = "save config")
    @GetMapping("/config")
    @NoApiValidate
    public R<Object> getConfig(@Schema(hidden = true)  @UName String username) {
        String str = redisTemplate.opsForValue().get(String.format(USER_CONFIG_KEY, username));
        if(StringUtils.hasText(str)){
            return R.ok(JSON.parseObject(str, UserConfigDTO.class));
        }
        return R.ok(new UserConfigDTO());
    }


    @Operation(summary = "save config")
    @PostMapping("/config")
    @NoApiValidate
    public R<Object> config(@RequestBody UserConfigDTO params,
                                @Schema(hidden = true)  @UName String username) {
        redisTemplate.opsForValue().set(String.format(USER_CONFIG_KEY, username), JSON.toJSONString(params));
        return R.ok();
    }


    @Operation(summary = "用户登录")
    @NoApiValidate
    @Validated
    @PostMapping("/login")
    public R<UserLoginRespDTO> login(@Validated @NotBlank @RequestParam String account,
                                     @Validated @NotBlank @RequestParam String password) {
        account=account.toLowerCase();
        password=password.toLowerCase();
        UserLoginRespDTO login = application.login(account, password);
        return R.ok(login);
    }


    @Operation(summary = "创建用户")
    @PostMapping("/list")
    public R<Page<User>> list(@RequestBody UserListReqDTO params, @Schema(hidden = true)  @UName String username) {
        params.setUsername(username);
        return application.list(params);
    }

    @Operation(summary = "修改用户状态")
    @PostMapping("/status")
    public R<Object> status(@RequestParam String account,
                    @RequestParam Integer status) {
         application.status(account,status);
          return R.ok();
    }


}
