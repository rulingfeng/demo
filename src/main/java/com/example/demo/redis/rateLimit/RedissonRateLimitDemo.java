package com.example.demo.redis.rateLimit;

import org.redisson.api.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: 茹凌丰
 * @date: 2022/7/13
 * @description: 分布式限流
 */
@Service
public class RedissonRateLimitDemo {
    @Resource
    private RedissonClient redissonClient;

    public void ratelimit() {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter("myRateLimiter");
        // 初始化
        // 最大流速 = 每1秒钟产生10个令牌
        rateLimiter.setRate(RateType.OVERALL, 10, 2, RateIntervalUnit.SECONDS);
        System.out.println(rateLimiter.getConfig());

        //直到拿到令牌为止
        rateLimiter.acquire();

        //尝试拿一次
        rateLimiter.tryAcquire();

        //拿指定令牌数量 拿到为止
        rateLimiter.acquire(3);

        //拿指定令牌数量 尝试拿一次
        rateLimiter.tryAcquire(3);

    }
}
