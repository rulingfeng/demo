package com.example.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author RU
 * @date 2020/7/13
 * @Desc
 */
@RestController
@RequestMapping("/listQueue")
@Slf4j
public class RedisListQueueController {

    @Autowired
    RedisTemplate redisTemplate;

    //redis实时队列  list数据结构
    @GetMapping("/add")
    public String add() {
        redisTemplate.opsForList().rightPush("list","a");
        redisTemplate.opsForList().rightPush("list","b");
        redisTemplate.opsForList().rightPush("list","c");

        return "";
    }
}
