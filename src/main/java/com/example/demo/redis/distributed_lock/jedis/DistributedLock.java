package com.example.demo.redis.distributed_lock.jedis;

/**
 * @author RU
 * @date 2020/8/29
 * @Desc
 */
public interface DistributedLock {
    /**
     * 获取锁
     * @author zhi.li
     * @return 锁标识
     */
    String acquire();

    /**
     * 释放锁
     * @author zhi.li
     * @param indentifier
     * @return
     */
    boolean release(String indentifier);
}
