package com.example.demo.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * Caffeine是基于java8实现的新一代缓存工具，缓存性能接近理论最优。
 * 可以看作是Guava Cache的增强版，功能上两者类似，不同的是Caffeine采用了一种结合LRU、LFU优点的算法：W-TinyLFU，在性能上有明显的优越性
 **/
@Slf4j
@Component
public class LocalCaffeine<K,V> {
    private Cache<K,V> localCache = null;

    @PostConstruct
    private void init(){
        localCache = Caffeine.newBuilder()
                //设置本地缓存容器的初始容量
                .initialCapacity(10)
                //设置本地缓存的最大容量
                .maximumSize(20)
                //设置多少时间未被访问后过期
                .expireAfterAccess(10,TimeUnit.SECONDS)
                //设置写缓存后多少秒过期
                .expireAfterWrite(10, TimeUnit.SECONDS).build();
    }

    public void setLocalCache(K key,V object){
        localCache.put(key,object);
    }

    /***
     * 返回值 如果不存在返回null
     * @param key
     * @param <V>
     * @return
     */
    public <V> V getCacheIfPresent(K key){
       return (V) localCache.getIfPresent(key);
    }

    /**
     * 如果缓存中没有,没去数据库等其他地方查询，返回则返回null
     * @param key
     * @return
     * @param <V>
     */
    public <V> V getCache(K key){
        return (V) localCache.get(key,this::getValueFromDB);
    }

    public <V> V getValueFromDB(K key) {
        return (V)Boolean.TRUE;
    }

    public void remove(K key){
        localCache.invalidate(key);
    }

}
