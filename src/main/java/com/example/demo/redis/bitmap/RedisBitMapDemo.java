package com.example.demo.redis.bitmap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisBitMapDemo {

    @Autowired
    private RedisTemplate redisTemplate;

    //用户签到 key是用户id+年+月   offset是日   value 是 true（代表已签到）
    /**
     * 设置key字段第offset位bit数值
     *
     * @param key    字段
     * @param offset 位置
     * @param value  数值
     */
    public void setBit(String key, long offset, boolean value) {
        redisTemplate.execute((RedisCallback) con -> con.setBit(key.getBytes(), offset, value));
    }

    /**
     * 判断该key字段offset位否为1
     *
     * @param key    字段
     * @param offset 位置
     * @return 结果
     */
    public boolean getBit(String key, long offset) {
        return (boolean) redisTemplate.execute((RedisCallback) con -> con.getBit(key.getBytes(), offset));
    }


    public void demo() throws InterruptedException {
        this.setBit("2022-03", 31,false);
        boolean bit = this.getBit("2022-03", 31);//false

        this.setBit("2022-03", 31,true);
        boolean bit1 = this.getBit("2022-03", 31);//true


        redisTemplate.expire("2022-03",5, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(7);

        boolean bit2 = this.getBit("2022-03", 31);//false
    }
}
