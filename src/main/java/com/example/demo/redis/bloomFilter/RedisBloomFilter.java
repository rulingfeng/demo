package com.example.demo.redis.bloomFilter;

import com.google.common.hash.Funnels;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @Description: redis bloomFilter
 * @Author: RU
 * @Date: 2020/5/25 9:15
 */
@Component
public class RedisBloomFilter {
    private long count = 100000;
    private double fpp = 0.03f;

    @Autowired
    private RedisTemplate redisTemplate;

    //bit数组长度
    private long numBits;
    //hash函数数量
    private int numHashFunctions ;

    public long getCount() {
        return count;
    }

    public void setExpectedInsertions(long count) {
        this.count = count;
    }

    public void setFpp(double fpp) {
        this.fpp = fpp;
    }

    public double getFpp() {
        return fpp;
    }

    @PostConstruct
    public void init(){
        this.numBits = optimalNumOfBits(count, fpp);
        this.numHashFunctions = optimalNumOfHashFunctions(count, numBits);
    }

    //计算hash函数个数
    private int optimalNumOfHashFunctions(long n, long m) {
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }

    //计算bit数组长度
    private long optimalNumOfBits(long n, double p) {
        if (p == 0) {
            p = Double.MIN_VALUE;
        }
        return (long) (-n * Math.log(p) / (Math.log(2) * Math.log(2)));
    }

    /**
     * 判断keys是否存在于集合
     */
    public boolean isExist(String key) {
        long[] indexs = getIndexs(key);
        List list = redisTemplate.executePipelined(new RedisCallback<Object>() {

            @Nullable
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                redisConnection.openPipeline();
                for (long index : indexs) {
                    redisConnection.getBit("bf:hilite".getBytes(), index);
                }
                redisConnection.close();
                return null;
            }
        });
        return list.contains(false);
    }

    /**
     * 将key存入redis bitmap
     */
    public void put(String key) {
        long[] indexs = getIndexs(key);
        redisTemplate.executePipelined(new RedisCallback<Object>() {

            @Nullable
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                redisConnection.openPipeline();
                for (long index : indexs) {
                    redisConnection.setBit("bf:hilite".getBytes(),index,true);
                }
                redisConnection.close();
                return null;
            }
        });
    }

    /**
     * 根据key获取bitmap下标
     */
    private long[] getIndexs(String key) {
        long hash1 = hash(key);
        long hash2 = hash1 >>> 16;
        long[] result = new long[numHashFunctions];
        for (int i = 0; i < numHashFunctions; i++) {
            long combinedHash = hash1 + i * hash2;
            if (combinedHash < 0) {
                combinedHash = ~combinedHash;
            }
            result[i] = combinedHash % numBits;
        }
        return result;
    }

    /**
     * 获取一个hash值
     */
    private long hash(String key) {
        Charset charset = Charset.forName("UTF-8");
        return Hashing.murmur3_128().hashObject(key, Funnels.stringFunnel(charset)).asLong();
    }
}
