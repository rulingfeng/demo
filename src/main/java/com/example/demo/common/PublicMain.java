package com.example.demo.common;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author: 茹凌丰
 * @date: 2022/8/3
 * @description:
 */
public class PublicMain {




    public static void main(String[] args) throws ExecutionException, InterruptedException {

        for (int i = 0; i < 10000; i++) {
            completableFutureRunAsyncThreadSafetyProblem();
        }
    }

    public static void completableFutureRunAsyncThreadSafetyProblem() throws ExecutionException, InterruptedException {
        //必须要用线程安全list,CountDownLatch的时候需要切换主线程,导致线程安全问题
        List<Integer> listOut =  Lists.newCopyOnWriteArrayList();

        List<Integer> marketFullMinusActivityRelations = Lists.newArrayList(1,2,3);
        CountDownLatch countDownLatch = new CountDownLatch(marketFullMinusActivityRelations.size());


        marketFullMinusActivityRelations.forEach(i->{
            CompletableFuture.runAsync(() -> {
                listOut.add(i);
               countDownLatch.countDown();
            });

        });
        countDownLatch.await();
        //理想[2,1,3] 或者 [1,2,3]
        //[null,1,2] 会有这种输出
        if(listOut.contains(null)){
            System.out.println(listOut);
        }



    }

}
