package com.example.demo.java8.concurrent;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author Benjamin Winterberg
 */
public class CompletableFuture1 {


    private static int coreThreads = 100;

    private static ThreadFactory namedThread = new ThreadFactoryBuilder().setNameFormat("worker-thread-%d").build();

    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(coreThreads, coreThreads << 1 + 1,
            300, TimeUnit.SECONDS, new LinkedBlockingQueue<>(2048), namedThread,new ThreadPoolExecutor.CallerRunsPolicy());

    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        CompletableFuture<String> future = new CompletableFuture<>();
//
//        future.complete("42");
//
//        future
//                .thenAccept(System.out::println)
//                .thenAccept(v -> System.out.println("done"));
        List<Integer> list = Lists.newArrayList();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }
        long l = System.currentTimeMillis();
        List<CompletableFuture<Object>> completableResult = list.stream().map(i -> CompletableFuture.supplyAsync(() -> {
            System.out.println(i);
            try {
                TimeUnit.MILLISECONDS.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }, threadPoolExecutor)).collect(Collectors.toList());
        CompletableFuture.allOf(completableResult.toArray(new CompletableFuture[completableResult.size()])).join();
        System.out.println(System.currentTimeMillis()-l);

    }

}
