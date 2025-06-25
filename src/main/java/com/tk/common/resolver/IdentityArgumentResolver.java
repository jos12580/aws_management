package com.tk.common.resolver;


import com.tk.common.annotation.UName;
import com.tk.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 集成MethodArgumentResolver
 *
 * @author weizuxiao
 */
@Slf4j
public class IdentityArgumentResolver implements MethodArgumentResolver, Ordered {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UName.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer container,
                                  NativeWebRequest request,
                                  WebDataBinderFactory factory) throws Exception {
        String authorization = request.getHeader("Authorization");
        if (!StringUtils.hasText(authorization)) {
            return null;
        }
        try {
            return JwtUtil.getTokenInfo(authorization).getClaim("username").asString();
        }catch (Exception e){
            return null;
        }
    }

}
