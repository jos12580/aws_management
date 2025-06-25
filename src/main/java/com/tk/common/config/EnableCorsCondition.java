package com.tk.common.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class EnableCorsCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // 从配置中获取 enableCors 的值
        return context.getEnvironment()
                .getProperty("enable_cors", Boolean.class, false);
    }
}