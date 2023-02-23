package com.example.demo.redis.rateLimit;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.common.OkHttpUtil;
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

    public static void main(String[] args) {
        String post = OkHttpUtil.postJsonParamsForAuthUserNameAndPassword("http://piprd.inm.cc:57000/RESTAdapter/ZMALL001/", "{\n" +
                "    \"ET_IN\": {\n" +
                "        \"item\": [\n" +
                "            {\n" +
                "                \"ZSCDDH\": \"mm831518054906068993\"}\n" +
                "        ]\n" +
                "    }\n" +
                "}");
        System.out.println(post);
    }
}
