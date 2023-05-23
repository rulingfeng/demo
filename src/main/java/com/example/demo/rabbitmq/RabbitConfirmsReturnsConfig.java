//package com.example.demo.rabbitmq;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.core.*;
//import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
//import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.annotation.Resource;
//
///**
// * @Description:
// * @Author: RU
// * @Date: 2020/4/7 13:47
// */
//@Configuration
//@Slf4j
//public class RabbitConfirmsReturnsConfig {
//
//    @Autowired
//    private CachingConnectionFactory connectionFactory;
//    @Resource
//    private LoginLogListener loginLogListener;
//
//    @Bean
//    public RabbitTemplate rabbitTemplate() {
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setMessageConverter(converter());
//
//        // 消息是否成功发送到Exchange
//        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
//            if (ack) {
//                String msgId = correlationData.getId();
//                log.info("消息成功发送到Exchange:{}",msgId);
//
//            } else {
//                log.info("消息发送到Exchange失败, {}, cause: {}", correlationData, cause);
//            }
//        });
//
//        // 触发setReturnCallback回调必须设置mandatory=true, 否则Exchange没有找到Queue就会丢弃掉消息, 而不会触发回调
//        rabbitTemplate.setMandatory(true);
//        // 消息是否从Exchange路由到Queue, 注意: 这是一个失败回调, 只有消息从Exchange路由到Queue失败才会回调这个方法
//        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
//            log.info("消息从Exchange路由到Queue失败: exchange: {}, route: {}, replyCode: {}, replyText: {}, message: {}", exchange, routingKey, replyCode, replyText, message);
//        });
//
//        return rabbitTemplate;
//    }
//
//    @Bean
//    public Jackson2JsonMessageConverter converter() {
//        return new Jackson2JsonMessageConverter();
//    }
//
//    // 登录日志
//    public static final String LOGIN_LOG_QUEUE_NAME = "login.log.queue";
//    public static final String LOGIN_LOG_EXCHANGE_NAME = "login.log.exchange";
//    public static final String LOGIN_LOG_ROUTING_KEY_NAME = "login.log.routing.key";
//
////    @Bean
////    public Queue logUserQueue() {
////        return new Queue(LOGIN_LOG_QUEUE_NAME, true);
////    }
////
////    @Bean
////    public DirectExchange logUserExchange() {
////        return new DirectExchange(LOGIN_LOG_EXCHANGE_NAME, true, false);
////    }
////
////    @Bean
////    public Binding logUserBinding() {
////        return BindingBuilder.bind(logUserQueue()).to(logUserExchange()).with(LOGIN_LOG_ROUTING_KEY_NAME);
////    }
//
//    /**
//     * 门店链接socket发送门店信息
//     */
//    @Bean(LOGIN_LOG_EXCHANGE_NAME)
//    DirectExchange storeConnectExchange() {
//        return new DirectExchange(LOGIN_LOG_EXCHANGE_NAME,true,false);
//    }
//
//    @Bean(LOGIN_LOG_QUEUE_NAME)
//    public Queue storeConnectQueue() {
//        return new Queue(LOGIN_LOG_QUEUE_NAME, true);
//    }
//
//    @Bean
//    public Binding bindingstoreConnectQueue(@Qualifier(LOGIN_LOG_QUEUE_NAME) Queue queue,
//                                            @Qualifier(LOGIN_LOG_EXCHANGE_NAME) DirectExchange exchange){
//        return BindingBuilder.bind(queue).to(exchange).with(LOGIN_LOG_ROUTING_KEY_NAME);
//    }
//
//
//    @Bean
//    public SimpleMessageListenerContainer listenerContainerTemplateMessage(@Qualifier(LOGIN_LOG_QUEUE_NAME) Queue delayTqSLQueue) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//       // container.setMessageConverter(new Jackson2JsonMessageConverter());
//        container.setConcurrentConsumers(10);
//        container.setMaxConcurrentConsumers(20);
//        container.setPrefetchCount(50);
//        container.setQueues(delayTqSLQueue);
//        container.setMessageListener(loginLogListener);
//        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
//        return container;
//    }
//
//}
