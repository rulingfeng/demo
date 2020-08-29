package com.example.demo.redis.msg_queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

/**
 * @author RU
 * @date 2020/8/29
 * @Desc
 */
@Slf4j
@Component
public class RedisListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("从消息通道={}监听到消息",new String(pattern));
        log.info("从消息通道={}监听到消息",new String(message.getChannel()));

        log.info("元消息={}",new String(message.getBody()));

        // 新建一个用于反序列化的对象，注意这里的对象要和前面配置的一样

        // 因为我前面设置的默认序列化方式为GenericJackson2JsonRedisSerializer

        // 所以这里的实现方式为GenericJackson2JsonRedisSerializer

        RedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        log.info("反序列化后的消息={}",jackson2JsonRedisSerializer.deserialize(message.getBody()));
    }


}
