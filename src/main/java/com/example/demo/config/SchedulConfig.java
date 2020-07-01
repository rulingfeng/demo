//package com.example.demo.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.SchedulingConfigurer;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//import org.springframework.scheduling.config.ScheduledTaskRegistrar;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.Executors;
//
///**
// * @Description:
// * @Author: RU
// * @Date: 2020/6/5 15:04
// */
//@Component
//public class SchedulConfig implements SchedulingConfigurer {
//
//
//    @Override
//    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
//
//        // 设置线程池
//        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
//        taskScheduler.setThreadNamePrefix("Schedule-Task-");
//        taskScheduler.setPoolSize(5);
//        taskScheduler.initialize();
//
//        scheduledTaskRegistrar.setTaskScheduler(taskScheduler);
//    }
//}
