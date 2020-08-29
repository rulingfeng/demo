package com.example.demo.redis.distributed_lock.jedis;

import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author RU
 * @date 2020/8/29
 * @Desc
 */
public class RedisDistributedLockTest {

    static int n = 500;
    static LongAdder x =new LongAdder();
    public static void secskill() {
        System.out.println(--n);
    }




    public static void main(String[] args) throws InterruptedException {
        Runnable runnable = () -> {
            RedisDistributedLock lock = null;
            String unLockIdentify = null;
            try {
                Jedis conn = new Jedis("127.0.0.1",6379);
                lock = new RedisDistributedLock(conn, "test1");
                unLockIdentify = lock.acquire();
                System.out.println(Thread.currentThread().getName() + "正在运行");
                secskill();
            } finally {
                if (lock != null) {
                    boolean release = lock.release(unLockIdentify);
                    System.out.println(release);
                    if(release) x.increment();
                }
            }
        };

        for (int i = 0; i < 100; i++) {
            Thread t = new Thread(runnable);
            t.start();
        }
        TimeUnit.SECONDS.sleep(2);
        System.out.println(x);
    }
}
