package com.tk.aws.infrastructure.listener;

import com.tk.aws.service.LightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;

@Slf4j
@Component
public class AwsRedisListen extends KeyExpirationEventMessageListener {


    public AwsRedisListen(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Resource
    private LightService lightService;

    private static final String PATTERN = "^aws_socks5_ok:.*";


    /**
     * Redis失效事件 key
     *
     * @param message
     * @param pattern down-num:13067
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 表示失效过期的key值
        String expireKey = message.toString();
        if (!expireKey.matches(PATTERN)) {
            return;
        }
        String[] split = expireKey.split(":");
        if (ObjectUtils.isEmpty(split[1])) {
            return;
        }
        lightService.callback(split[1], "");
    }


}
