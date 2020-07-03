package com.example.demo.job;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.model.DealAmountPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * @Description:
 * @Author: RU
 * @Date: 2020/4/14 15:50
 */
@Configuration
@EnableScheduling
@EnableAsync
public class SchedulingTest {

    @Autowired
    RedisTemplate redisTemplate;



    @Scheduled(cron ="0/5 * * * * ?")

    public void scheduleTest(){
        System.out.println(Thread.currentThread().getName()+"定时任务取消订单");
        long currentTimeMillis = System.currentTimeMillis();
        Set<String> testZset = redisTemplate.opsForZSet().rangeByScore("testt9", 0, System.currentTimeMillis());
        if(null != testZset && testZset.size()>0){
            redisTemplate.opsForZSet().removeRangeByScore("testt9", 0, currentTimeMillis);
        }
        Iterator<String> iterator = testZset.iterator();
        while (iterator.hasNext()){
            String next = iterator.next();
            DealAmountPo dealAmountPo = JSONObject.parseObject(next, DealAmountPo.class);
            System.out.println(dealAmountPo.toString());
        }

    }


   @Scheduled(cron ="0/5 * * * * ?" )
    public void scheduledddTest(){
        System.out.println(Thread.currentThread().getName()+"111111");
        AtomicInteger atomicInteger = new AtomicInteger();
        atomicInteger.incrementAndGet();
        LongAdder longAdder = new LongAdder();

    }


}
