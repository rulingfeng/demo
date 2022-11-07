package com.example.demo;


import com.example.demo.model.OrderDetail;
import com.example.demo.model.OrderMain;
import com.example.demo.model.MainOrderTwo;
import com.example.demo.service.*;

import com.github.pagehelper.PageHelper;
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
    @Autowired
    MainOrderTwoService orderMainTwoService;

    //单库多表
    @Test
    public void orderDetailSave() throws IOException, InterruptedException {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderNo(124125412L);
        orderDetail.setUserId(124125412L);
        orderDetail.setTime(new Date());
        orderDetailService.save(orderDetail);
    }


    @Test
    public void orderDetailSearch() throws IOException, InterruptedException {
        List<OrderDetail> list = orderDetailService.lambdaQuery().list();
        for (OrderDetail entity : list) {
            System.out.println(entity);
        }
        System.out.println(list.size() + "条数");
    }

    @Test
    public void query(){
        /**
         *  查询 一定要增加分表规则条件， 才能精确查询到那张表，否则报错 必须有开始和结束时间 才能确定需要那个表
         *  并且查询是通过每月的表名 union all 集合所有数据的  可以分页查询
         */
        PageHelper.startPage(2,2);
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
        entity.setDynamicTime(System.currentTimeMillis() /1000 + (86400L * 30));
        entity.setCreateTime(new Date());
        orderMainService.save(entity);
    }



    @Test
    public void queryTwo(){
        /**
         *  查询 一定要增加分表规则条件， 才能精确查询到那张表
         */
        List<MainOrderTwo> list = orderMainTwoService.lambdaQuery()
                .ge(MainOrderTwo::getDynamicTime,System.currentTimeMillis()/1000 - 86400L)
                .le(MainOrderTwo::getDynamicTime,System.currentTimeMillis()/1000 + (86400L * 60)).list();
        //     .ge(OrderMain::getDynamicTime,System.currentTimeMillis()/1000 + (86400L * 24)).list();
        for (MainOrderTwo entity : list) {
            System.out.println(entity);
        }
        System.out.println(list.size() + "条数");
    }



    @Test
    public void shardingSaveTwo() {
        MainOrderTwo entity = new MainOrderTwo();
        entity.setCstatus("11");
        entity.setOrderNo(7912591511251L);
        entity.setUserId(123456L);
        //分表策略字段，格林尼治 秒值
        entity.setDynamicTime(System.currentTimeMillis() /1000 + (86400L * 60));
        entity.setCreateTime(new Date());
        orderMainTwoService.save(entity);
    }

}
