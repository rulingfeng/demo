package com.example.demo;

import com.beust.jcommander.internal.Lists;
import com.example.demo.controller.RedisListQueueController;
import com.example.demo.redis.bitmap.RedisBitMapDemo;
import com.example.demo.redis.bloomFilter.RedisBloomFilter;
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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
