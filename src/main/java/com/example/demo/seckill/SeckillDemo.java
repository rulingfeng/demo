package com.example.demo.seckill;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author rulingfeng
 * @time 2022/12/2 09:21
 * @desc
 */
public class SeckillDemo {
    private static ConcurrentLinkedQueue<String> orderIdList = new ConcurrentLinkedQueue();
    private static ConcurrentLinkedQueue<String> orderItemIdList = new ConcurrentLinkedQueue();
    public static final int ORDER_COUNT_LIMIT_SECOND = 2000;
    public static final int FETCH_PERIOD = 100;
    private static ScheduledExecutorService refreshService = Executors.newSingleThreadScheduledExecutor();


    public static void main(String[] args) throws InterruptedException {
        init();
    }

    public static void init() throws InterruptedException {
        StopWatch sw = new Slf4JStopWatch();
        TimeUnit.SECONDS.sleep(1);
        //有start，从start开始计时， 没有start，从new Slf4JStopWatch()开始计时
        sw.start();
        TimeUnit.SECONDS.sleep(1);
        sw.stop("updateSegmentFromDb", "renid");//输出:start[1669948543307] time[1005] tag[updateSegmentFromDb] message[renid]


        StopWatch stopWatch = new StopWatch();

        TimeUnit.MILLISECONDS.sleep(1500);
        System.out.println(stopWatch.stop("ceshi","re"));//输出:start[1669968355344] time[505] tag[ceshi] message[re]

        //stopWatch.getElapsedTime()
//        refreshService.scheduleAtFixedRate(new RefreshIdListTask(orderIdList,orderItemIdList),
//                0, 500, TimeUnit.MILLISECONDS);
//        //取出索引0的元素
//        String orderIdStr = orderIdList.poll();
//        System.out.println(orderIdStr);

    }
}
