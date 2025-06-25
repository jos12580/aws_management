package com.tk.server.domain.model;

import com.baomidou.mybatisplus.annotation.*;
import com.tk.common.entity.BaseEntity;
import com.tk.common.util.JwtUtil;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

/**
 * <p>
 *
 * </p>
 *
 * @author author
 * @since 2023-03-03
 */
@Data
@TableName("user")
public class User  extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String userId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;

    private String account;

    private String password;

    /**
     * 0可用 1禁用
     */
    private Integer status;


    /**
     * 是否可见 0：可见， 1：不可见
     */
    private Integer showed;

    private String remark;

    /**
     * 最大设备数
     */
    private Integer maxDvNum;

    private String role;

    public static User newInstance(String account, String password, String username, String role) {
        User it = new User();
        it.setUserId(account);
        it.setUsername(username);
        it.setAccount(account);
        it.setPassword(password);
        it.setStatus(0);
        it.setMaxDvNum(100);
        return it;
    }


    public boolean canUse() {
        return Integer.valueOf(0).equals(status);
    }

    public String genToken() {
        HashMap<String, String> map = new HashMap<>();
        map.put("username",username);
        map.put("userId",userId);
        return JwtUtil.gen(map);
    }


}
