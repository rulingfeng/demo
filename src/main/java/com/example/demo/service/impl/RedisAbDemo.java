package com.example.demo.service.impl;

import com.example.demo.service.ITestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: RU
 * @Date: 2020/5/7 16:32
 */
@Component
@Slf4j
public abstract class RedisAbDemo {

    public abstract void ToDbselect(Integer i);
}
