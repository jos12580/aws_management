package com.tk.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

//在后端启动文件同级目录，创建一个目录Corsconfig
@Configuration
public class CorsConfig {

    @Value("${enable_cors:false}")
    private boolean enableCors;

    private CorsConfiguration buildConfig(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        if (enableCors){
            //设置属性
            corsConfiguration.addAllowedOriginPattern("*");
            //跨域的请求头
            corsConfiguration.addAllowedHeader("*");
            //跨域的请求方法
            corsConfiguration.addAllowedMethod("*");
            //在跨域请求的时候使用同一个Session
            corsConfiguration.setAllowCredentials(true);
        }
        return corsConfiguration;
    }

    @Bean
    @Conditional(EnableCorsCondition.class)
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",buildConfig());
        return new CorsFilter(source);
    }

}