package com.tk.common.interceptor;

import com.tk.common.annotation.NoApiValidate;
import com.tk.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.webmvc.api.OpenApiWebMvcResource;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.naming.AuthenticationNotSupportedException;
import java.util.Objects;

/**
 * 接口校验拦截
 *
 * @author weizuxiao
 */
@Slf4j
@Component
public class ApiValidateInterceptor implements WebHandlerInterceptor, Ordered {

    private Boolean validate=true;

    @Override
    public int getOrder() {
        return 0;
    }

    @SneakyThrows
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        /* 关闭校验不拦截诶 */
        if (!validate) {
            return true;
        }
        /* 不拦截资源请求 */
        if (handler instanceof ResourceHttpRequestHandler) {
            return true;
        }
        /* 非接口不拦截 */
        HandlerMethod method = handler instanceof HandlerMethod ? (HandlerMethod) handler : null;
        if (Objects.isNull(method)) {
            return true;
        }
        /* 不拦截 swagger api接口 / 错误信息接口 */
        if (method.getBeanType().isAssignableFrom(OpenApiWebMvcResource.class)
                || method.getBeanType().isAssignableFrom(BasicErrorController.class)
        ) {
            return true;
        }
        /* 指定不拦截 */
        if (method.getBean().getClass().isAnnotationPresent(NoApiValidate.class)
                || method.hasMethodAnnotation(NoApiValidate.class)) {
            return true;
        }
        /* 获取用户身份 */
        String authorization = request.getHeader("Authorization");
        try {
            if (!StringUtils.hasText(JwtUtil.getTokenInfo(authorization).getClaim("userId").asString())) {
                throw new AuthenticationNotSupportedException("无效访问身份");
            }
        } catch (Exception e) {
            throw new AuthenticationNotSupportedException("无效访问身份");
        }
        return true;
    }

}
