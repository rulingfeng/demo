package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: RU
 * @Date: 2020/4/7 13:47
 */
@Configuration
public class RabbitConfig {

//        @Bean
//    public Queue orderQueue(){
//        return new Queue("test.demo_aa");
//    }


//        @Bean
//    public Queue orderQueue(){
//        return new Queue("vinsuan.rlf");
//    }


//    @Bean
//    public Queue orderQueue(){
//        return new Queue("vinsuan.test");
//    }
//
//    @Bean
//    DirectExchange orderDirect() {
//        return (DirectExchange) ExchangeBuilder
//                .directExchange("vinsuan.exchange.test")
//                .durable(true)
//                .build();
//    }
//
//    /**
//     * 将订单队列绑定到交换机
//     */
//    @Bean
//    Binding orderBinding(DirectExchange orderDirect, Queue orderQueue){
//        return BindingBuilder
//                .bind(orderQueue)
//                .to(orderDirect)
//                .with("vinsuan.routingKey.test");
//    }

//    @Bean
//    public Queue orderttQueue(){
//        return new Queue("testtest");
//    }
//
//    @Bean
//    Binding ordertBinding(DirectExchange orderDirect){
//        return BindingBuilder
//                .bind(new Queue("testtest"))
//                .to(orderDirect)
//                .with("vinsuan.routingKey.test");
//    }


}
