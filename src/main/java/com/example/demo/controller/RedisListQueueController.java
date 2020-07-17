package com.example.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        List<Integer> list3 = new ArrayList<>(10000000);
        for (int k = 0; k <100 ; k++) {
            for (int i = 0; i <1000000 ; i++) {
                list2.add(i);
            }
        }
//        new Thread(()->{
//            long l = System.currentTimeMillis();
//            list.addAll(list2);
//            System.out.println("addAll:"+ (System.currentTimeMillis()-l));
//        }).start();

        new Thread(()->{
            long l = System.currentTimeMillis();
            for (Integer integer : list2) {
                list3.add(integer);
            }
            System.out.println("stream:"+ (System.currentTimeMillis()-l));
        }).start();
    }
}
