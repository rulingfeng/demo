package com.example.demo.redis.distributed_lock.redisson;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author RU
 * @date 2020/8/29
 * @Desc
 */
@Service
public class NewRedissonLockDemo {
    @Resource
    private RedissonClient redissonClient;

    public void dg(){
        ThreadPoolExecutor threadPoolExecutor =
                new ThreadPoolExecutor(1, 8, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        RLock lock = redissonClient.getLock("newlock1");

        for (int i = 1; i <= 10; i++) {

            threadPoolExecutor.execute(() -> {
                long l = System.nanoTime();

                lock.lock();
                System.out.println(1);
                lock.unlock();
                System.out.println(l);
            });
        }
    }


}
