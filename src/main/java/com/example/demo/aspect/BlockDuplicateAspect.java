package com.example.demo.aspect;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: RU
 * @Date: 2020/6/6 13:28
 */
@Aspect
@Component
@Slf4j
public class BlockDuplicateAspect {

    @Resource
    private HttpServletResponse response;
    @Autowired
    private RedisTemplate redisTemplate;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private HttpServletRequest request;



    @Pointcut("@annotation(com.example.demo.aspect.BlockDuplicate)")
    public void dsPointCut() {
    }

    @Around(value = "dsPointCut()")
    public Object beforeOrderRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
//        Object[] values = joinPoint.getArgs();
//        String[] names = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
//        String token = null;
//        for (int i = 0; i < names.length; i++) {
//            if(names[i].equals("token")){
//                token = values[i].toString();
//                break;
//            }
//        }

        String token = request.getHeader("token");
        if(StringUtils.isBlank(token)){
            return "token blank";
        }
        RLock lock = redissonClient.getLock(token+"11");
        lock.lock();
        if (!redisTemplate.hasKey(token)){
            lock.unlock();
            return "fail";
        }
        redisTemplate.delete(token);
        lock.unlock();

        Object proceed = joinPoint.proceed();
        return proceed;

    }
}
