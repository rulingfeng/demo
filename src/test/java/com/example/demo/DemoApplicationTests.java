package com.example.demo;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.model.OrderMain;
import com.example.demo.service.OrderDetailService;
import com.example.demo.service.OrderErrorService;
import com.example.demo.service.OrderLogService;

import com.example.demo.service.OrderMainService;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import org.elasticsearch.client.RestHighLevelClient;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Date;
import java.util.List;


@SpringBootTest(classes = DemoApplication.class)
@RunWith(SpringRunner.class)
class DemoApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;
    private static final int size = 1000000;// 100万
    private static BloomFilter<Integer> bloomFilter =BloomFilter.create(Funnels.integerFunnel(), size,0.03);


    @Autowired
    private RestHighLevelClient highLevelClient;


    @Autowired
    OrderDetailService orderDetailService;
    @Autowired
    OrderLogService orderLogService;
    @Autowired
    OrderErrorService orderErrorService;
    @Autowired
    OrderMainService orderMainService;

    //单库多表
    @Test
    public void addDocumentDetailTest() throws IOException, InterruptedException {

    }



    @Test
    public void query(){
        /**
         *  查询 一定要增加分表规则条件， 才能精确查询到那张表
         */
        List<OrderMain> list = orderMainService.lambdaQuery()
                .ge(OrderMain::getDynamicTime,System.currentTimeMillis()/1000 - 86400L)
                .le(OrderMain::getDynamicTime,System.currentTimeMillis()/1000 + (86400L * 30)).list();
           //     .ge(OrderMain::getDynamicTime,System.currentTimeMillis()/1000 + (86400L * 24)).list();
        for (OrderMain entity : list) {
            System.out.println(entity);
        }
        System.out.println(list.size() + "条数");
    }
    @Test
    public void shardingSave() {
        OrderMain entity = new OrderMain();
        entity.setCstatus("11");
        entity.setOrderNo(7912591511251L);
        entity.setUserId(123456L);
        //分表策略字段，格林尼治 秒值
        entity.setDynamicTime(System.currentTimeMillis() /1000 + (86400L * 60));
        entity.setCreateTime(new Date());
        orderMainService.save(entity);
    }

}
