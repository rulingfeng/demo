package com.example.demo.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @description: 缓存管理工具，采用LRU淘汰策略
 **/
@Slf4j
@Component
public class LocalGuavaCache<T> {
    private Cache<String,T> localCache = null;

    @PostConstruct
    private void init(){
        localCache = CacheBuilder.newBuilder()
                //设置本地缓存容器的初始容量
                .initialCapacity(10)
                //设置本地缓存的最大容量
                .maximumSize(20)
                //设置多少时间未被访问后过期
                .expireAfterAccess(10,TimeUnit.SECONDS)
                //设置写缓存后多少秒过期
                .expireAfterWrite(10, TimeUnit.SECONDS).build();
    }

    public void setLocalCache(String key,T object){
        localCache.put(key,object);
    }

    /***
     * 返回值 如果不存在返回null
     * @param key
     * @param <T>
     * @return
     */
    public <T> T getCache(String key){
       return (T) localCache.getIfPresent(key);
    }

    public void remove(String key){
        localCache.invalidate(key);
    }

}