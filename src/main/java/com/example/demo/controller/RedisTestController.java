package com.example.demo.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.model.User;
import com.example.demo.redis.util.RedisAllUtils;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.mvel2.util.Make;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.stream.Collectors;

/**
 * @author RU
 * @date 2020/7/13
 * @Desc
 */
@RestController
@RequestMapping("/redisTest")
@Slf4j
public class RedisTestController {

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserService userService;
    @Autowired
    RedisAllUtils redisAllUtils;

    LongAdder ad  = new LongAdder();
     final RedissonClient client;
     final RLock lock;

     public RedisTestController(){
         Config config = new Config();
         config.useSingleServer().setAddress("redis://127.0.0.1:6379");
         client = Redisson.create(config);
         lock = client.getLock("lockkey");
     }

    //redis延迟队列 Zset的数据结构, 通过score设置过期时间,然后在提取时传入时间从而实现延迟
    @GetMapping("/delayQueue")
    public String delayQueue() {
         String key = "delayqueue5";
        User user = new User();
        user.setId(1);
        long l = System.currentTimeMillis();
        //存 (发送消息)
        redisAllUtils.zAdd(key,user,(l + 9000L));
        user.setId(2);
        //存 (发送消息)
        redisAllUtils.zAdd(key,user,(l + 5000L));
        user.setId(3);
        //存 (发送消息)
        redisAllUtils.zAdd(key,user,(l + 7000L));
        System.out.println(l);

        new Thread(()->{
            //延迟消费
            while (true){
                long l1 = System.currentTimeMillis();
                Set<Object> objects = redisAllUtils.zRangeByScore(key, 0d, (double) l1);
                if(!CollectionUtil.isEmpty(objects)){
                    for (Object objectTypedTuple : objects) {
                        User user1 = JSONObject.parseObject(JSONObject.toJSONString(objectTypedTuple), User.class);
                        System.out.print(user1);
                        Long delayqueue1 = redisAllUtils.zRemove(key, objectTypedTuple);
                        System.out.println("删除"+delayqueue1);
                        System.out.println(l1);
                    }
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }).start();

        return "ok";
    }


    //redis实时队列  list数据结构
    @GetMapping("/redissonLock")
    @Transactional
    public void redissonLock() {
        Boolean flag ;
        lock.lock();
        Integer num = Integer.valueOf(redisTemplate.opsForValue().get("miaosha").toString());
        if (num > 0) {
            redisTemplate.opsForValue().set("miaosha",num-1);
            ad.increment();
            flag =  true;
        }else{
            flag =  false;
        }
        System.out.println(flag);
        lock.unlock();
    }

    //获取分布式锁
    public Boolean getLock(){
        long timeOut = 1000L;
        String value = String.valueOf(timeOut+ System.currentTimeMillis());
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent("lockkey", value);
        System.out.println("isLock:"+isLock);
        long beginTime = System.currentTimeMillis();
        if(Boolean.FALSE.equals(isLock)){  //没有获取到锁
            Object sourceLockkey = redisTemplate.opsForValue().get("lockkey");
            long oldTime = Long.valueOf(sourceLockkey.toString());
            if(oldTime < System.currentTimeMillis()){  //原锁已经超时
                String value1 = String.valueOf(timeOut+ System.currentTimeMillis());
                Object newLockkey = redisTemplate.opsForValue().getAndSet("lockkey", value1);
                long newTime = Long.valueOf(newLockkey.toString());
                if(oldTime != newTime){
                    return false;
                }
                return true;

            }
            return false;
        }
        return true;
    }

    public Boolean unLock(){
        return true;
    }

    @GetMapping("/get")
    public String get() {
        return ad.toString();
    }


    @GetMapping("/boom11")
    public String boom11() {
        System.out.println(1);
        return "2";
    }




    //redis实时队列  list数据结构
    @GetMapping("/add")
    public String add() {
        redisTemplate.opsForList().rightPush("list","a");
        redisTemplate.opsForList().rightPush("list","b");
        redisTemplate.opsForList().rightPush("list","c");

        return "";
    }

    @GetMapping("/bitMap")
    public String bitMap() {
        redisTemplate.opsForValue().setBit("testBitMap",1,true);
        redisTemplate.opsForValue().setBit("testBitMap",3,true);
        redisTemplate.opsForValue().setBit("testBitMap",4,true);
        System.out.println(redisTemplate.opsForValue().getBit("testBitMap",1));
        System.out.println(redisTemplate.opsForValue().getBit("testBitMap",3));
        System.out.println(redisTemplate.opsForValue().getBit("testBitMap",2));
        System.out.println( redisTemplate.execute((RedisCallback<Long>) con -> con.bitCount("testBitMap".getBytes())));
//        for (int i = 0; i < 50_0000; i++) {
//            redisTemplate.opsForValue().setBit("testBitMap",i,true);
//        }
//        for (int i = 50_0000; i < 100_0000; i++) {
//            redisTemplate.opsForValue().setBit("testBitMap",i,false);
//        }
        System.out.println( redisTemplate.execute((RedisCallback<Long>) con -> con.bitCount("testBitMap".getBytes())));
        return "";
    }


    @GetMapping("/listPage")
    public String list(@RequestParam("current")Integer current,@RequestParam("size")Integer size){
//        for (int i = 1; i < 11; i++) {
//            User user = new User();
//            user.setId(i);
//            user.setAge(i+"");
//            user.setUserName("名"+i);
//            redisTemplate.opsForList().rightPush("listObjectPage",user);
//        }
        Integer start = (current - 1) * size;
        Integer end = start + size - 1;
        List<byte[]> list =(List<byte[]>) redisTemplate.execute((RedisCallback) con -> con.listCommands().lRange("listObjectPage".getBytes(), start, end));
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        List<Object> collect = list.stream().map(i -> jackson2JsonRedisSerializer.deserialize(i)).collect(Collectors.toList());
//        Long listPage = redisTemplate.opsForList().size("listObjectPage");
//        System.out.println("listObjectPage:"+listPage);
        return JSONObject.toJSONString(collect);

    }

    @GetMapping("/jedis")
    public String jedis(@RequestParam("current")Integer current,@RequestParam("size")Integer size){
//                for (int i = 1; i < 11; i++) {
//            User user = new User();
//            user.setId(i);
//            user.setAge(i+"");
//            user.setUserName("名"+i);
//            redisTemplate.opsForList().rightPush("listObjectPage",JSONObject.toJSONString(user));
//        }
        Integer start = (current - 1) * size;
        Integer end = start + size - 1;
        Jedis jedis = new Jedis("localhost");
        List<String> listObjectPage = jedis.lrange("listObjectPage", start, end);
        List<User> collect = listObjectPage.stream().map(i -> {
            String temp = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(i);
            return JSONObject.parseObject(temp.substring(1,temp.length()-1),User.class);
        }).collect(Collectors.toList());


        return JSONObject.toJSONString(collect);

    }


    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        List<Integer> list3 = new ArrayList<>(10000000);
        for (int k = 0; k <100 ; k++) {
            for (int i = 0; i <1000000 ; i++) {
                list2.add(i);
            }
        }
//        new Thread(()->{
//            long l = System.currentTimeMillis();
//            list.addAll(list2);
//            System.out.println("addAll:"+ (System.currentTimeMillis()-l));
//        }).start();

        new Thread(()->{
            long l = System.currentTimeMillis();
            for (Integer integer : list2) {
                list3.add(integer);
            }
            System.out.println("stream:"+ (System.currentTimeMillis()-l));
        }).start();
    }
}
