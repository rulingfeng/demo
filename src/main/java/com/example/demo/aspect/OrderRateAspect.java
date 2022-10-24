package com.example.demo.aspect;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: RU
 * @Date: 2020/6/6 13:28
 */
@Aspect
@Component
@Slf4j
public class OrderRateAspect {

    @Resource
    private HttpServletResponse response;

    RateLimiter rateLimiter = RateLimiter.create(50);

    @Pointcut("@annotation(com.example.demo.aspect.OrderRateLimit)")
    public void dsPointCut() {
    }

    @Around(value = "dsPointCut()")
    public Object beforeOrderRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        if(rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)){
            Object proceed = joinPoint.proceed();
            return proceed;
        }

        output(response, "{\n" +
                "    \"code\": 404,\n" +
                "    \"message\": \"您被限流啦\"\n" +
                "}");

        return null;

    }

    private void output(HttpServletResponse response, String msg)  {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        ServletOutputStream os = null;
        try {
            os = response.getOutputStream();
            os.write(msg.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
