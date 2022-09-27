package com.example.demo.controller;

import com.example.demo.model.User;
import com.google.common.collect.Lists;
import com.rabbitmq.utility.IntAllocator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author rulingfeng
 * @time 2022/9/27 09:26
 * @desc 模拟分布式下扣减库存，防止超卖
 */
@RestController
@RequestMapping("/lua")
@Slf4j
public class LuaScriptController {
    @Autowired
    private DefaultRedisScript defaultRedisScript;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    private AtomicInteger longAdder = new AtomicInteger();

    @GetMapping("/add")
    public String add(){
//        redisTemplate.opsForHash().put("testluascript1","testluascript2",200);
//        return String.valueOf(redisTemplate.opsForHash().get("testluascript1","testluascript2"));

        redisTemplate.opsForValue().set("testluascript",10);
        return  String.valueOf(redisTemplate.opsForValue().get("testluascript"));
    }




    @GetMapping("/test")
    public String lua(Integer num){
//        List<String> strings = Lists.newArrayList("testluascript1","testluascript2");
//        Long execute = (Long)redisTemplate.execute(defaultRedisScript, strings);

        //redis中的key
        List<String> strings = Lists.newArrayList("testluascript");
        //num表示需要扣减的库存数量
        Long execute = (Long)redisTemplate.execute(defaultRedisScript, strings,num);
        if(execute.equals(-1L)){
            return "false";
        }else if(execute.equals(-2L)){
            return "没这个key";
        }
        longAdder.incrementAndGet();
        return execute.toString();
    }

    @GetMapping("/get")
    public String get(){
        return longAdder.get()+"";
    }
}
