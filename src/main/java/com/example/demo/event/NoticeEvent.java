package com.example.demo.event;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.example.demo.common.RlfHttpUtils;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import lombok.Data;
import lombok.SneakyThrows;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 事件消费者
 *
 * @author EDZ
 * @since 2019-12-26
 */
@Component
@EnableAsync
public class NoticeEvent  {

    @Autowired
    UserService userService;

    @TransactionalEventListener(fallbackExecution = true)
    @Async
    public void onApplicationEvent(NoticeEventObj eventObj){
        System.out.println("进入1");
        //System.out.println("type"+eventObj.getType());
        //System.out.println("source"+eventObj.getSource());
//        String s = RlfHttpUtils.get("http://localhost:8888/sixSix/list",null);
//        System.out.println("调取信息"+s);
    }

}


