package com.example.demo.controller;

import com.example.demo.cache.LocalCache;
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
    private LocalCache<Boolean> cache;


    @GetMapping("/test")
    public String test() throws InterruptedException {
        for (int i = 0; i < 30; i++) {
            cache.setLocalCache(String.valueOf(i),true);
            TimeUnit.MILLISECONDS.sleep(50);
        }
        for (int i = 0; i < 30; i++) {
            System.out.println(""+i+cache.getCache(String.valueOf(i)));
        }

        return "ok";
    }
}
