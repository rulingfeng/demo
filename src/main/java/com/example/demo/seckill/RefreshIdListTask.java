package com.example.demo.seckill;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;


@Slf4j
public class RefreshIdListTask implements Runnable{

    public static final String LEAF_ORDER_ID_KEY = "order_id";
    public static final String LEAF_ORDER_ITEM_ID_KEY = "order_item_id";

    private ConcurrentLinkedQueue<String> orderIdList;
    private ConcurrentLinkedQueue<String> orderItemIdList;


    public RefreshIdListTask(ConcurrentLinkedQueue<String> orderIdList,

                             ConcurrentLinkedQueue<String> orderItemIdList) {
        this.orderIdList = orderIdList;

        this.orderItemIdList = orderItemIdList;
    }

    @Override
    public void run() {
        System.out.println(new Date());
        if (!orderIdList.isEmpty()) return;
        try {
            List<String> segmentIdList = Lists.newArrayList();
            for (int i = 0; i < 3; i++) {
                segmentIdList.add(String.valueOf((int) ((Math.random()*9+1)*1000)));
            }
            orderIdList.addAll(segmentIdList);
            log.info("成功刷新订单id列表，个数{}",segmentIdList.size());
            List<String> segmentItemIdList = Lists.newArrayList();
            for (int i = 0; i < 3; i++) {
                segmentItemIdList.add(String.valueOf( (int)((Math.random()*9+1)*1000)));
            }
            orderItemIdList.addAll(segmentItemIdList);
            log.info("成功刷新订单详情id列表，个数{}",segmentItemIdList.size());
        } catch (Exception e) {
            log.error("获取订单id列表异常：",e);
        }
    }
}
