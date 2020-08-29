package com.example.demo.redis.msg_queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;

/**
 * @author RU
 * @date 2020/8/29
 * @Desc
 */
@Component
public class ListenerContainerBean {

    @Autowired
    RedisListener redisListener;

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory factory) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();

        container.setConnectionFactory(factory);

        container.addMessageListener(redisListener, new PatternTopic("demo-channel"));

        container.setTaskExecutor(Executors.newFixedThreadPool(5));

        return container;
    }
}
