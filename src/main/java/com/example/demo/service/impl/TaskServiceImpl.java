package com.example.demo.service.impl;


import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Description:
 * @Author: RU
 * @Date: 2020/4/9 19:27
 */
@Configuration
@EnableScheduling
public class TaskServiceImpl {

    //@org.springframework.scheduling.annotation.Scheduled(cron = "0/2 * * * * ? ")
    public void te(){
        System.out.println(1);
    }
}
