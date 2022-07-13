package com.example.demo.redis.bloomFilter.redisson;

import io.swagger.models.auth.In;
import org.redisson.api.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

/**
 * @author: 茹凌丰
 * @date: 2022/7/12
 * @description: redisson实现布隆过滤器
 */
@Service
public class RedissonBlommFilterDemo {

    @Resource
    private RedissonClient redissonClient;

    public void test(){
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("rlfbloomfilter");
        //尝试初始化，预计元素55，期望误判率0.03
        bloomFilter.tryInit(55L, 0.03);
        //添加元素到布隆过滤器中
        bloomFilter.add("tom");
        bloomFilter.add("mike");
        bloomFilter.add("rose");
        bloomFilter.add("blue");
        System.out.println("布隆过滤器元素总数为：" + bloomFilter.count());//布隆过滤器元素总数为：4

        //一个元素如果判断结果为存在的时候元素不一定存在；但是判断结果为不存在的时候则一定不存在

        System.out.println("是否包含tom：" + bloomFilter.contains("tom"));//是否包含tom：true
        System.out.println("是否包含lei：" + bloomFilter.contains("lei"));//是否包含lei：false


    }

    public void test2(){
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("rlfbloomfilter22");
        //尝试初始化，预计元素55，期望误判率0.03
        bloomFilter.tryInit(100000L, 0.02);
        for (int i = 100000; i < 1100000; i++) {
            bloomFilter.add("tom"+i);
        }

    }




}
