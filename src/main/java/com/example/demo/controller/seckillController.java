package com.example.demo.controller;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author rulingfeng
 * @time 2022/12/15 16:45
 * @desc
 */
@RestController
@RequestMapping("/seckill")
@Slf4j
public class seckillController {
    @Resource
    private RedissonClient redissonClient;

    @GetMapping("/stock")
    public void stock(){
        /**
         *  设置库存
         */
        RSemaphore semaphore = redissonClient.getSemaphore("semaphoreKey");
        // 设置秒杀库存量为信号量的值
        semaphore.trySetPermits(10);
        // 设置库存信号量过期时间
        semaphore.expireAt(1671522880000l);


        /**
         * 模拟并发秒杀
         */
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                RSemaphore semaphore2 = redissonClient.getSemaphore("semaphoreKey");
                // 5ms内扣减库存
                try {
                    //每次抢3个，5ms的获取信号量的时间，抢成功返回true，失败返回false
                    boolean b = semaphore2.tryAcquire(3, 5, TimeUnit.MILLISECONDS);

                    System.out.println(b);
                }catch (Exception e){
                    System.out.println("报错"+e.getMessage());
                }
            }).start();
        }


    }
}
