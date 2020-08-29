package com.example.demo.redis.msg_queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author RU
 * @date 2020/8/29
 * @Desc
 */
@RestController
@RequestMapping("/redisQueue")
@Slf4j
public class RedisQueueController {

    @Autowired
    RedisTemplate redisTemplate;

    @GetMapping("/queue")
    public void sdg(){


        redisTemplate.convertAndSend("demo-channel","你好");


    }
}
