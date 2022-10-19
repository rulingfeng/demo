package com.example.demo.controller;

import com.example.demo.aspect.BlockDuplicate;
import org.elasticsearch.client.RestHighLevelClient;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@RequestMapping("/es")
@RestController
public class Elasticsearch7Controller {
    @Autowired
    private RestHighLevelClient highLevelClient;

    @GetMapping("/getToken")
    public String getToken(){

        return null;
    }



}
