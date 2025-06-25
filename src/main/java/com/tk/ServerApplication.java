package com.tk;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Date;

@EnableWebMvc
@EnableAsync
@EnableScheduling
@SpringBootApplication
@Slf4j
//@EnableDiscoveryClient
public class ServerApplication {


    public static void main(String[] args) {

        SpringApplication.run(ServerApplication.class, args);

        log.info("当前时间：{}", DateUtil.formatDateTime(new Date()));

    }


}
