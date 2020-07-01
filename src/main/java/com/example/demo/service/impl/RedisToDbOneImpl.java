package com.example.demo.service.impl;

import com.example.demo.service.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @Description:
 * @Author: RU
 * @Date: 2020/5/7 16:40
 */
@Component
public class RedisToDbOneImpl extends RedisAbDemo  {
    @Autowired
    private ITestService iTestService;


    @Override
    public void ToDbselect(Integer i) {
        Object o = iTestService.redisToDb(i);
        if(Objects.isNull(o)){
            throw  new RuntimeException("捕捉到null异常");
        }
    }

}
