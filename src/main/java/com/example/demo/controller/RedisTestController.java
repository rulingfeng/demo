package com.example.demo.controller;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.mvel2.util.Make;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
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


    //redis实时队列  list数据结构
    @GetMapping("/boom")
    public void boom() {
        Boolean execute = (Boolean)redisTemplate.execute((RedisCallback) connection -> {
            byte[] bytes = connection.stringCommands().get("miaosha".getBytes());
            Integer num = Integer.valueOf(new String(bytes));
            if (num > 0) {
                connection.stringCommands().set("miaosha".getBytes(), String.valueOf(num - 1).getBytes());
                return true;
            } else {
                return false;
            }

        });
        System.out.println(execute);
        List exec = redisTemplate.exec();



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

    @GetMapping("/hyperLogLog")
    public String hyperLogLog(){
        for (int i = 0; i < 50_0000; i++) {
            redisTemplate.opsForHyperLogLog().add("hyper",i);
        }
//        redisTemplate.opsForHyperLogLog().add("hyper",222);
//        redisTemplate.opsForHyperLogLog().add("hyper",111);
//        redisTemplate.opsForHyperLogLog().add("hyper",333);
        System.out.println(redisTemplate.opsForHyperLogLog().size("hyper"));

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
