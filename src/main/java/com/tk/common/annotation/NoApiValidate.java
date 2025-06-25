package com.tk.common.annotation;

import java.lang.annotation.*;

/**
 * 接口不校验
 * 开启校验必须验证用户请求
 *
 * @author weizuxiao
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface NoApiValidate {

}
