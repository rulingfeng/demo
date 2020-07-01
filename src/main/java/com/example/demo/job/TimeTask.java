package com.example.demo.job;


import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author maguoxin
 * @Date : 2020/04/16
 */
@Component
@Slf4j
public class TimeTask {


    /**
     * 统计车辆占比
     */
    //@Scheduled(cron = "0/5 * * * * ?")
    public void queryCarPercent(){
        System.out.println("222222");

    }








}