package com.example.demo.controller;

import com.example.demo.config.GZHConfig;
import com.example.demo.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * @Description: webFlux demo类
 * @Author: RU
 * @Date: 2020/4/27 13:40
 */
@RestController
@RequestMapping("/flux")
@Slf4j
public class FluxController {

    @Resource
    private Scheduler scheduler;
    @Autowired
    private GZHConfig gzhConfig;

    @GetMapping("/date")
    public String data(){
        System.out.println(gzhConfig.getAppId());
        System.out.println(gzhConfig.getAppSecret());
        System.out.println(gzhConfig.getToken());
        System.out.println(gzhConfig.getCarIn());
        System.out.println(gzhConfig.getPay());
        System.out.println(gzhConfig.getCarInError());
        return null;
    }

    @GetMapping("/monoDemo")
    public Mono<String> monoDemo() {
        return Mono.just("hello webflux");
    }


    @GetMapping("/mono")
    public Mono<Object> mono() {
        Map<String,Object> map = new HashMap<>();
        map.put("data","hello webflux demo");
        map.put("code",0);
        map.put("isSuccess",true);
        return Mono.create(monoSink -> {
            log.info("创建 Mono");
            monoSink.success(map);
        })
                .doOnSubscribe(subscription -> { //当订阅者去订阅发布者的时候，该方法会调用
                    log.info("{}","Subscribe--->"+subscription);
                }).doOnNext(o -> { //当订阅者收到数据时，改方法会调用
                    log.info("{}","OnNext"+o);

                });
    }

    @GetMapping("/mono1")
    public Mono<User> mono1() {
        User user = new User();
        user.setId(5);

        Mono<User> result = Mono.fromCallable(() ->
                user
        ).subscribeOn(scheduler);

        System.out.println(result.block().getId());
        return result;

    }

    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<>();
        map.put("data","hello webflux demo");
        map.put("code",0);
        map.put("isSuccess",true);
        Mono<Object> aaa = Mono.create(monoSink -> {
            log.info("创建 Mono");
            monoSink.success(map);
        })
                .doOnSubscribe(subscription -> { //当订阅者去订阅发布者的时候，该方法会调用
                    log.info("{}", "Subscribe--->" + subscription);
                }).doOnNext(o -> { //当订阅者收到数据时，改方法会调用
                    log.info("{}", "OnNext" + o);

                });
        Object block = aaa.block();
        Map<String,Object> block1 = (Map) block;
        System.out.println(block1.get("code"));
    }
}
