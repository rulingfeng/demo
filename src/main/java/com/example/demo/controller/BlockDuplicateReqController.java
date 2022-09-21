package com.example.demo.controller;

import com.example.demo.aspect.BlockDuplicate;
import com.example.demo.aspect.OrderRateLimit;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@RequestMapping("/blockDuplicate")
@RestController
public class BlockDuplicateReqController {
    @Autowired
    private RedisTemplate redisTemplate;
    @Resource
    private RedissonClient redissonClient;

    @GetMapping("/getToken")
    public String getToken(){
        String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(uuid,1,10, TimeUnit.SECONDS);
        return uuid;
    }

    @GetMapping("/limit")
    @BlockDuplicate
//    public String limit(@RequestParam(value = "token") String token){
    public String limit(){
//        RLock lock = redissonClient.getLock(token+"11");
//        lock.lock();
//        if (!redisTemplate.hasKey(token)){
//            lock.unlock();
//            return "fail";
//        }
//        redisTemplate.delete(token);
//        lock.unlock();

        //业务逻辑
        return "ok";
    }

}
