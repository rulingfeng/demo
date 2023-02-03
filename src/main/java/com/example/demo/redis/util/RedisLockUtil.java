package com.example.demo.redis.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 用法
 *  String openIdKey = REGISTERKEY + registerBo.getVxOpenId() + registerBo.getZfbOpenId();
 *             try (RedisLockUtil redisLockUtil = new RedisLockUtil(redisTemplate, openIdKey, 5)) {
 *                 if(!redisLockUtil.getLock()){
 *                     throw new BizException("注册中,请稍等!");
 *                 }else{
 *                     return registerHandle(registerBo);
 *                 }
 *             }catch (Exception e){
 *                 log.error("register lock error",e);
 *                 throw new BizException(e.getMessage());
 *             }
 */
@Slf4j
public class RedisLockUtil implements AutoCloseable{

    private RedisTemplate redisTemplate;
    private String key;
    private String value;
    //单位：秒
    private int expireTime;

    public RedisLockUtil(RedisTemplate redisTemplate, String key, int expireTime){
        this.redisTemplate = redisTemplate;
        this.key = key;
        this.expireTime=expireTime;
        this.value = UUID.randomUUID().toString();
    }

    /**
     * 获取分布式锁
     * @return
     */
    public boolean getLock(){
        RedisCallback<Boolean> redisCallback = connection -> {
            //设置NX
            RedisStringCommands.SetOption setOption = RedisStringCommands.SetOption.ifAbsent();
            //设置过期时间
            Expiration expiration = Expiration.seconds(expireTime);
            //序列化key
            byte[] redisKey = redisTemplate.getKeySerializer().serialize(key);
            //序列化value
            byte[] redisValue = redisTemplate.getValueSerializer().serialize(value);
            //执行setnx操作
            Boolean result = connection.set(redisKey, redisValue, expiration, setOption);
            return result;
        };

        //获取分布式锁
        Boolean lock = (Boolean)redisTemplate.execute(redisCallback);
        return lock;
    }

    public boolean unLock() {
        String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                "    return redis.call(\"del\",KEYS[1])\n" +
                "else\n" +
                "    return 0\n" +
                "end";
        RedisScript<Boolean> redisScript = RedisScript.of(script,Boolean.class);
        List<String> keys = Arrays.asList(key);

        Boolean result = (Boolean)redisTemplate.execute(redisScript, keys, value);
        log.info("释放锁的结果："+result);
        return result;
    }
    @Override
    public void close() throws Exception {
        unLock();
    }
}
