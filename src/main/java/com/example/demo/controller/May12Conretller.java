package com.example.demo.controller;

import com.example.demo.model.UserVo;
import com.google.common.util.concurrent.ThreadFactoryBuilder;


import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description:
 * @Author: RU
 * @Date: 2020/5/12 13:06
 */
public class May12Conretller implements Serializable{

    private static final long serialVersionUID = -2673271731114020501L;

    private static final int THREAD_COUNT = 100000;

    private static ReentrantLock lock = new ReentrantLock();
    public static void main(String[] args) throws Exception{
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().build();
        ExecutorService executor = new ThreadPoolExecutor(5, 200,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
        int b = 50;
        for (int i = 0; i < b; i++){
            executor.execute(()->{
                lock.lock();
                try{
                    System.out.println("lock.getHoldCount()"+lock.getHoldCount());
                    System.out.println(System.nanoTime());
                }catch (Exception e){

                }finally {
                    lock.unlock();
                }



            });
        }
        executor.shutdown();

        System.out.println("成功");

    }
    @Deprecated
    public static void test(int threadnum) {

        System.out.println("threadnum:" + threadnum + "is ready");

        System.out.println(threadnum+"号:"+threadnum);
        System.out.println("threadnum:" + threadnum + "is finish");
        String a = "1";
        int v = 100;
        for(int i = 0;i < v;i++){
            a+= threadnum+"";
        }
    }
}
