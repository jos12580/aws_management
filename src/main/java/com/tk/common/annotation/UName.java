package com.tk.common.annotation;

import java.lang.annotation.*;

/**
 * 用户ID注解
 * 通过注解获取用户信息
 *
 * @author weizuxiao
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface UName {

}
