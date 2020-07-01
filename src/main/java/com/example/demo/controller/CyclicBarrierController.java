package com.example.demo.controller;

import com.example.demo.dao.TestMapper;
import com.example.demo.dao.UserMapper;
import com.example.demo.event.NoticeEventObj;
import com.example.demo.model.SmsHomeBrand;
import com.example.demo.model.Stock;
import com.example.demo.model.User;
import com.example.demo.service.ITestService;
import com.github.pagehelper.Page;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

//import io.seata.spring.annotation.GlobalTransactional;

/**
 * @ProjectName: demo
 * @Package: com.example.demo.controller
 * @ClassName: TestController
 * @Author: RU
 * @Description:
 * @Date: 2020/3/19 17:34
 * @Version: 1.0
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class CyclicBarrierController implements ApplicationEventPublisherAware {
    @Autowired
    private ITestService testService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;







    // 请求的数量
    private static final int threadCount = 100000;
    // 需要同步的线程数量
    private static final CyclicBarrier cyclicBarrier = new CyclicBarrier(3);
    static CountDownLatch countDownLatch = new CountDownLatch(3);
    public static void main(String[] args) throws InterruptedException, ExecutionException {

        //ExecutorService threadPool = Executors.newFixedThreadPool(10);


//        for (int i = 0; i <3; i++) {
//            final int threadNum = i;
//            threadPool.execute(() -> {
//                try {
//                    test(threadNum);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    countDownLatch.countDown();
//                }
//            });
//        }
        List<String> list = new ArrayList<>();
        for (int i = 0; i <3; i++) {

            FutureTask<String> ft = new FutureTask<>(()  -> {
                Thread.sleep(2000);

                return System.nanoTime()+"";
            });
            new Thread(ft).start();
            String s = ft.get();
            list.add(s);

            countDownLatch.countDown();
        }
        //threadPool.shutdown();
        countDownLatch.await();
        System.out.println("全部完成");
        System.out.println(list.toString());
    }
    public static void test(int threadnum) throws Exception {
        if(threadnum==2){
            throw new Exception("错误");
        }
        System.out.println(threadnum);
        countDownLatch.countDown();

    }


    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

    }
}
