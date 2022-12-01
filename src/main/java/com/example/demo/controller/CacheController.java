package com.example.demo.controller;


import com.example.demo.cache.LocalCaffeine;
import com.example.demo.cache.LocalGuavaCache;
import com.example.demo.es.OrderDelayQueue;
import com.example.demo.es.OrderDelayedElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author rulingfeng
 * @time 2022/11/30 14:51
 * @desc
 */
@RestController
@RequestMapping("/cache")
@Slf4j
public class CacheController {

    @Autowired
    private LocalGuavaCache<Boolean> cache;
    @Autowired
    private LocalCaffeine<String,Boolean> caffeine;


    @GetMapping("/guava")
    public String guava() throws InterruptedException {
        for (int i = 0; i < 30; i++) {
            cache.setLocalCache(String.valueOf(i),true);
            TimeUnit.MILLISECONDS.sleep(50);
        }
        for (int i = 0; i < 30; i++) {
            System.out.println(""+i+cache.getCache(String.valueOf(i)));
        }

        return "ok";
    }

    @GetMapping("/caffeine")
    public String caffeine() throws InterruptedException {
        for (int i = 0; i < 30; i++) {
            caffeine.setLocalCache(String.valueOf(i),true);
            TimeUnit.MILLISECONDS.sleep(50);
        }
        for (int i = 0; i < 30; i++) {
            System.out.println(""+i+caffeine.getCacheIfPresent(String.valueOf(i)));
        }

        return "ok";
    }

    @GetMapping("/caffeine1")
    public String caffeine1() throws InterruptedException {
        Object cache1 = caffeine.getCache("15");
        System.out.println(cache1);//true
      //  caffeine.setLocalCache(String.valueOf(i),true);


        System.out.println(""+caffeine.getCacheIfPresent("15"));//true

        System.out.println(""+caffeine.getCacheIfPresent("14"));//null
        return "ok";
    }

}
