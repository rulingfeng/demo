//package com.example.demo.rabbitmq;
//
//import com.alibaba.fastjson.JSONObject;
//import com.example.demo.rabbitmq.RabbitConfirmsReturnsConfig;
//import com.example.demo.model.User;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.core.*;
//import org.springframework.amqp.rabbit.connection.CorrelationData;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import java.util.*;
//
///**
// * @ProjectName: demo
// * @Package: com.example.demo.controller
// * @ClassName: DemoController
// * @Author: RU
// * @Description:
// * @Date: 2020/3/25 18:48
// * @Version: 1.0
// */
//@RestController
//@RequestMapping("/rabbitMq")
//@Slf4j
//public class RabbitMqController {
//
//    @Resource
//    private RabbitTemplate rabbitTemplate;
//
//
//
//    @GetMapping("/rabbit")
//    public String rabbit(){
//        String correlationId = UUID.randomUUID().toString();
//
//        User user = new User();
//        user.setUserName("ru");
//        user.setId(8888);
//        user.setAge("18");
//
//        String messageId = UUID.randomUUID().toString();
//        Message message = MessageBuilder
//                .withBody(JSONObject.toJSONString(user).getBytes())
//                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
//                .setMessageId(messageId)
//                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
//                .build();
//
//        log.info("发送消息前messageId:{},correlationId:{}",messageId,correlationId);
//        CorrelationData correlationData = new CorrelationData(correlationId);
//        rabbitTemplate.convertAndSend(RabbitConfirmsReturnsConfig.LOGIN_LOG_EXCHANGE_NAME, RabbitConfirmsReturnsConfig.LOGIN_LOG_ROUTING_KEY_NAME
//                , message, correlationData);
//
//        return "ok";
//    }
//
//
//
//}
