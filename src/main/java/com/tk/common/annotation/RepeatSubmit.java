package com.tk.common.annotation;


import java.lang.annotation.*;

/**
 * 防止重复提交注解，过期时间,默认1s
 */
@Target(ElementType.METHOD) // 注解只能用于方法
@Retention(RetentionPolicy.RUNTIME) // 修饰注解的生命周期
@Documented
public @interface RepeatSubmit {

    /**
     * 防重复操作过期时间,默认2s
     */
    long expireTime() default 2;

    String key() default "";
}
