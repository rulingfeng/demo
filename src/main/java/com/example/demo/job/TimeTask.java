package com.example.demo.job;


import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author maguoxin
 * @Date : 2020/04/16
 */
@Component
@Slf4j
public class TimeTask {

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 统计车辆占比
     */
    //@Scheduled(cron = "0/5 * * * * ?")
    //@PostConstruct
    public void queryCarPercent(){
        new Thread(()->{
            while (true){
                Object list = redisTemplate.opsForList().leftPop("list");
                if (null != list){
                    System.out.println(list);
                }
            }
        }).start();


    }








}