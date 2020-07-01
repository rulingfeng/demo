package com.example.demo.config;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.model.User;
import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: RU
 * @Date: 2020/4/7 13:53
 */
@Configuration
public class RabbitMqListener {

    @RabbitListener(queues = "test.demo_aa")
    public void aaa(String message){
        System.out.println(message);
    }

}
