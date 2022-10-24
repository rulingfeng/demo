package com.example.demo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: RU
 * @Date: 2020/4/14 16:30
 */
@Aspect
@Component
@Slf4j
public class ScheduleAspect {
    @Autowired
    RedisTemplate redisTemplate;
    @Pointcut("execution(public * com.example.demo.job.*.*(..))")
    public void bbb() {
    }

    @Around("bbb()")
    public Object aaa(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean flag = redisTemplate.opsForValue().setIfAbsent("scheduleJob","11");
        boolean scheduleJob;
        if(flag){
            scheduleJob = redisTemplate.expire("scheduleJob", 4, TimeUnit.SECONDS);
            Object result = joinPoint.proceed();
            if (!scheduleJob){
                redisTemplate.delete("scheduleJob");
            }
            return result;
        }
        return null;
    }

}
