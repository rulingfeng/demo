package com.example.demo.config;

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
