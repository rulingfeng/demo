package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.UserCar;
import com.example.demo.service.ITestService;
import com.example.demo.service.UserCarService;
import com.example.demo.service.UserService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    ITestService testService;
    @Autowired
    UserService userService;
    @Autowired
    UserCarService userCarService;

    @GetMapping("/aa")
    public String gggg(){
        testService.compleableSave();
        return "aa";
    }

    @GetMapping("/bb")
    @Transactional(rollbackFor = Exception.class)
    public String bb(){
        Integer num = 2;
        User user = new User();
        user.setId(num);
        user.setUserName(num+"");
        user.setAge(num+"");
        userService.save(user);

//        UserCar userCar = new UserCar();
//        userCar.setId(num);
//        userCar.setCar(num+"");
//        userCar.setUserId(num);
//        userCarService.save(userCar);
        return "bb";
    }



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
