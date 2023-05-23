//package com.example.demo.rabbitmq;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.example.demo.model.User;
//import com.rabbitmq.client.Channel;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.core.MessageProperties;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.Date;
//import java.util.Map;
//
//@Component
//@Slf4j
//
//public class LoginLogListener implements ChannelAwareMessageListener {
//
//
//
//
//   // @RabbitListener(queues = RabbitConfirmsReturnsConfig.LOGIN_LOG_QUEUE_NAME)
//    public void consume(Message message, Channel channel) throws IOException {
//
//    }
//
//    @Override
//    public void onMessage(Message message, Channel channel) throws Exception {
//        long tag = message.getMessageProperties().getDeliveryTag();
//        String messageId = message.getMessageProperties().getMessageId();
//        try {
//            String correlationId = null;
//
//            MessageProperties properties = message.getMessageProperties();
//            Map<String, Object> headers = properties.getHeaders();
//            for (Map.Entry entry : headers.entrySet()) {
//                String key = (String) entry.getKey();
//                String value = (String) entry.getValue();
//                if (key.equals("spring_returned_message_correlation")) {
//                    correlationId = value;
//                }
//            }
//
//
//            log.info("消费messageId:{},correlationId:{}",messageId,correlationId);
//            User user = JSONObject.parseObject(message.getBody(), User.class);
//            log.info("消费内容:{}",user);
//            channel.basicAck(tag, true);
//        } catch (Exception e) {
//
//            try {
//                channel.basicReject(tag, false);
//            } catch (IOException ex) {
//                log.error("消费失败",e);
//            }
//        }
//    }
//}
