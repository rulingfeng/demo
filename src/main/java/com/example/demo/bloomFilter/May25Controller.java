package com.example.demo.bloomFilter;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.es.OrderDelayQueue;
import com.example.demo.es.OrderDelayedElement;
import com.example.demo.model.DealAmountPo;
import com.example.demo.model.UserTest;
import com.example.demo.service.ITestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: redis布隆过滤器
 * @Author: RU
 * @Date: 2020/5/25 9:20
 */
@RestController
@Slf4j
@RequestMapping("/redisBloom")
public class May25Controller {
    @Autowired
    RedisBloomFilter redisBloomFilter;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    ITestService testService;

    @GetMapping("/set")
    public String set(){
        List<Integer> list = new ArrayList<>();
        for (int i = 2000000 ; i< 3000000;i++){
            redisTemplate.opsForValue().set(i+"",i+"");
        }
        return "";

    }

    @GetMapping("/test")
    public String ree(){
        List<Integer> list = new ArrayList<>();
        for (int i = 3000000 ; i< 3020000;i++){
            if(redisBloomFilter.isExist(i+"")){
                list.add(i);
            }
        }
        System.out.println("误判:"+list.size());
        return null;
    }
    @GetMapping("/queue")
    public String queue() throws InterruptedException {
        OrderDelayQueue.getDeque().put(new OrderDelayedElement( 5 * 1000L , 13654543));
        OrderDelayQueue.getDeque().put(new OrderDelayedElement( 5 * 1000L , 251243));
        OrderDelayQueue.getDeque().put(new OrderDelayedElement( 5 * 1000L , 334346));

        OrderDelayQueue.getDeque().remove(new OrderDelayedElement( 5 * 1000L , 251243));

        return "111";
    }
    @GetMapping("/asd")
    public String asd() throws Exception {
        testService.async();
        return "111";
    }

    @GetMapping("/redisZset")
    public Object redisZset() {

        long currentTimeMillis = System.currentTimeMillis();
        Map<String,Object> map = new HashMap<>();
        map.put("cartMainId",123123123123123L);
        map.put("actPrice",5);

        Map<String,Object> map1 = new HashMap<>();
        map1.put("cartMainId",12312312315151515L);
        map1.put("actPrice",15);

        Boolean testt9 = redisTemplate.opsForZSet().add("testt9", JSONObject.toJSONString(map), currentTimeMillis + 5 * 1000);
        Boolean testt91 = redisTemplate.opsForZSet().add("testt9", JSONObject.toJSONString(map1), currentTimeMillis + 15 * 1000);

        return testt9 && testt91;
    }

    @GetMapping("/del")
    public Object del() throws InterruptedException {

        Map<String,Object> map1 = new HashMap<>();
        map1.put("cartMainId",12312312315151515L);
        map1.put("actPrice",15);

        Long remove = redisTemplate.opsForZSet().remove("testt9",JSONObject.toJSONString(map1));


        return remove;
    }


    @GetMapping("/getZset")
    public String getZset() throws InterruptedException {
        Set<Integer> testZset = redisTemplate.opsForZSet().rangeByScore("testt5", 0, System.currentTimeMillis());
        Iterator<Integer> iterator = testZset.iterator();
        while (iterator.hasNext()){
            Integer next = iterator.next();
            System.out.println(next);
        }
        return "111";
    }




    @GetMapping("/getT")
    public void getT(){
        for(;;){
            long currentTimeMillis = System.currentTimeMillis();
            Set<Integer> testZset = redisTemplate.opsForZSet().rangeByScore("testt8", 0, currentTimeMillis);
            if(null != testZset && testZset.size()>0){
                redisTemplate.opsForZSet().removeRangeByScore("testt8", 0, currentTimeMillis);
            }
            Iterator<Integer> iterator = testZset.iterator();
            while (iterator.hasNext()){
                Integer next = iterator.next();
                System.out.println(next);
            }

        }

    }


    public static  Map<String,String> teasg(UserTest userTest){
        userTest.setAge( userTest.getAge() + 1);
        return new HashMap<>();
    }
    public static void main(String[] args) {
        List<DealAmountPo> list = new ArrayList<>();
        DealAmountPo d1 = new DealAmountPo();
        d1.setCartMainId(1L);
        d1.setActPrice(100);
        list.add(d1);
        DealAmountPo d2 = new DealAmountPo();
        d2.setCartMainId(2L);
        d2.setActPrice(200);
        list.add(d2);
        DealAmountPo d3 = new DealAmountPo();
        d3.setCartMainId(3L);
        d3.setActPrice(300);
        list.add(d3);
        DealAmountPo d4 = new DealAmountPo();
        d4.setCartMainId(1L);
        d4.setActPrice(300);
        list.add(d4);
        Map<Long, DealAmountPo> collect1 = list.parallelStream().collect(Collectors.toMap(DealAmountPo::getCartMainId, Function.identity(), (u1, u2) -> {
            //u1.setCartMainId(u1.getCartMainId() + u2.getCartMainId());
            u1.setActPrice(u1.getActPrice() + u2.getActPrice());
            return u1;
        }));
        System.out.println(collect1);


    }


}
