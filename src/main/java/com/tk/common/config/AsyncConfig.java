package com.tk.common.config;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {
    private static ThreadPoolTaskExecutor executor;

    @PostConstruct
    public void init() {
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix(String.format("async-%s-%s", RandomUtil.randomString(6), getLocalIp()));
        executor.initialize();
    }

    public String getLocalIp() {
        String localIp = RandomUtil.randomString(6);
        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return localIp;
    }

    @Override
    public Executor getAsyncExecutor() {
        return executor;
    }

    @PreDestroy
    public void destroy() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    public static ThreadPoolTaskExecutor getExecutor() {
        return executor;
    }


}