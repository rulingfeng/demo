package com.example.demo.redis.hyperloglog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisHyperLogLogDemo {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //统计uv（当日用户量）
    public void hyperloglog() throws InterruptedException {
        String str = "hyper21";

        //向这个key增加value，返回1新增成功
        System.out.println(redisTemplate.opsForHyperLogLog().add(str,"a","b")); //1
        //查询该key里面的value数量（是去重的）
        System.out.println(redisTemplate.opsForHyperLogLog().size(str));                //2
        System.out.println(redisTemplate.opsForHyperLogLog().add(str,"b","c")); //1
        System.out.println(redisTemplate.opsForHyperLogLog().size(str));                //3
        System.out.println(redisTemplate.opsForHyperLogLog().add(str,"d","e")); //1
        System.out.println(redisTemplate.opsForHyperLogLog().size(str));                //5
        //有效期设置
        redisTemplate.expire(str,5, TimeUnit.SECONDS);


    }
}
