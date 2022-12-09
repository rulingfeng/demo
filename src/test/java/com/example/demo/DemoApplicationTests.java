package com.example.demo;

import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.example.demo.common.HttpUtil;
import com.example.demo.common.OkHttpUtil;
import com.example.demo.common.RlfHttpUtils;
import com.example.demo.controller.RedisListQueueController;
import com.example.demo.email.SendEmailService;
import com.example.demo.redis.bitmap.RedisBitMapDemo;
import com.example.demo.redis.bloomFilter.RedisBloomFilter;
import com.example.demo.redis.bloomFilter.redisson.RedissonBlommFilterDemo;
import com.example.demo.redis.distributed_lock.redisson.NewRedissonLockDemo;
import com.example.demo.redis.hyperloglog.RedisHyperLogLogDemo;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import static org.apache.coyote.http11.Constants.a;

@SpringBootTest(classes = DemoApplication.class)
@RunWith(SpringRunner.class)
class DemoApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;
    private static final int size = 1000000;// 100万
    private static BloomFilter<Integer> bloomFilter =BloomFilter.create(Funnels.integerFunnel(), size,0.03);
    @Autowired
    private RedisBitMapDemo redisBitMapDemo;
    @Autowired
    RedisListQueueController redisListQueueController;
    @Resource
    private RedissonBlommFilterDemo redissonBlommFilterDemo;
@Resource
    SendEmailService sendEmailService;


    @Test
    void sendEmail11(){
        String url = "https://nainm.inm.cc/inm-sms-center/public/app/sendMassage";
        Map<String, String> params = Maps.newHashMap();
        params.put("activityName","奶卡七折购，错过等明年！");
        params.put("activityDesc","奶吧全品箱装奶3箱7折");
        params.put("activityTime","2022年12月01日 00:00");
        params.put("activityDeadlineTime","2022年12月10日 24:00");
        params.put("reminder","1年核销有效期，7折箱装奶想提就提！");
        params.put("path","packageActivity/pages/couponGroup/coupon/coupon?aType=92&qd=317");
        params.put("msgType","18");
        params.put("userId","5429049");
        String post = HttpUtil.doPostJsonString(url, JSONObject.toJSONString(params));
        System.out.println(post);


    }

    @Test
    void sendEmail(){
        sendEmailService.sendMessage();
    }
    @Test
    void contextLoads() {
        System.out.println();
        for (int i = 0; i < size; i++) {
            bloomFilter.put(i);
        }
        List<Integer> list = new ArrayList<>(1000);
        //故意取10000个不在过滤器里的值，看看有多少个会被认为在过滤器里
        for (int i = size + 20000; i < size + 40000; i++) {
            if (bloomFilter.mightContain(i)) {
                list.add(i);
            }
        }
        System.out.println("误判的数量：" + list.size()); //在300左右
    }

    @Test
    public void in(){
        String add = redisListQueueController.add();
        System.out.println(add
        );
    }

    @Test
    void excel() throws IOException, InterruptedException {


    }

}
