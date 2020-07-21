package com.example.demo.controller;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static java.util.stream.Collectors.toList;

/**
 * @author RU
 * @date 2020/7/21
 * @Desc
 */
@RequestMapping("/complateable")
@RestController
public class CompletableFutureController {

    private int coreThreads = Runtime.getRuntime().availableProcessors();

    private ThreadFactory namedThread = new ThreadFactoryBuilder().setNameFormat("worker-thread-%d").build();

    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(coreThreads, coreThreads << 1 + 1,
            300, TimeUnit.SECONDS, new LinkedBlockingQueue<>(3), namedThread);

    @GetMapping("/s")
    public   void gsg(){
        List<List<String>> aList = new ArrayList<>();
        aList.add(new ArrayList<>(Arrays.asList("xyz", "abc")));
        aList.add(new ArrayList<>(Arrays.asList("qwe", "poi")));
        ExecutorService executor = Executors.newFixedThreadPool(aList.size());
        //List<String> collect = aList.stream().flatMap(List::stream).collect(Collectors.toList());
        //System.out.println(collect);
        List<CompletableFuture<Boolean>> futureList = aList.stream()
                .map(strings -> CompletableFuture.supplyAsync(() -> processList(strings), threadPoolExecutor))
                .collect(toList());

        //Wait for them all to complete
        //CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        List<Boolean> collect = futureList.stream()
                .map(CompletableFuture::join).collect(toList());
        System.out.println(collect);
        //threadPoolExecutor.shutdown();
        System.out.println(threadPoolExecutor==null);


    }
    public static boolean processList(List<String> tempList) {
        for (String string : tempList) {
            System.out.println("Output: " + string);
        }
        return true;
    }
}
