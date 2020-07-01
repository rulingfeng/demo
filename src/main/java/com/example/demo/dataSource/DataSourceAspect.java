package com.example.demo.dataSource;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @program: parking_client
 * @description:
 * @author: 栗翱
 * @create: 2020-04-08 14:07
 **/
@Slf4j
@Aspect
@Order(1)
@Component
public class DataSourceAspect {

    @Pointcut("@annotation(com.example.demo.dataSource.DataSource)")
    public void dsPointCut() {

    }

    @Before(value = "dsPointCut()")
    public void beforeDataSource(JoinPoint joinPoint){
        log.info("进入切点>>>>>>>>>>>>>>>>>>>>>>>>>>>执行切换数据源");
        log.info("aop执行切换数据源！！！");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DataSource dataSource = method.getAnnotation(DataSource.class);
        if (dataSource != null) {
            DynamicDataSourceContextHolder.setDataSourceType(dataSource.value().name());
        }
    }

    @After("dsPointCut()")
    public void afterDataSource(){
        DynamicDataSourceContextHolder.clearDataSourceType();
    }
}
