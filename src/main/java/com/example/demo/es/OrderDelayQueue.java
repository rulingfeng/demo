package com.example.demo.es;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

/**
 * @ProjectName: parking_road
 * @Package: com.vinsuan.parking.platform.service.delay
 * @ClassName: OrderDelayQueue
 * @Author: RU
 * @Description: 订单延迟队列
 * @Date: 2020/3/12 16:23
 * @Version: 1.0
 */
@Component
@Slf4j
public class OrderDelayQueue {



    private final static BlockingQueue<OrderDelayedElement> deque = new DelayQueue<>();

    public static BlockingQueue<OrderDelayedElement> getDeque() {
        return deque;
    }

    @PostConstruct
    public void customerRunnable(){
        new Thread(()->{
            log.info("进到支付订单延迟队列>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            while (true) {
                try {
                    Integer id = deque.take().getId();
                    log.info("消费到一个订单id:{}" , id);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
