package com.example.demo.redis.distributed_lock.redisson;

import org.redisson.Redisson;
import org.redisson.RedissonLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author RU
 * @date 2020/8/29
 * @Desc
 */
public class RedissonLockDemo {

    public void dg(){
        ThreadPoolExecutor threadPoolExecutor =
                new ThreadPoolExecutor(8, 16, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        long start = System.currentTimeMillis();
        Config config = new Config();
        config.useSingleServer().setAddress("redis://10.11.2.58:6379");
        final RedissonClient client = Redisson.create(config);
        final RLock lock = client.getLock("lock1");

        for (int i = 0; i <= 10; i++) {
            threadPoolExecutor.execute(() -> {
                lock.lock();
                //inventory--;
                System.out.println(2);
                lock.unlock();
            });
        }
        long end = System.currentTimeMillis();
       // System.out.println("执行线程数:" + NUM + "   总耗时:" + (end - start) + "  库存数为:" + inventory);
    }

    public static void main(String[] args) {
        RedissonLockDemo redissonLockDemo = new RedissonLockDemo();
        redissonLockDemo.dg();
    }

}
