package com.example.demo.event;

import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 事件消费者
 *
 * @author EDZ
 * @since 2019-12-26
 */
@Component
@EnableAsync
public class NoticeEvent_2 {

    @Autowired
    UserService userService;

    @TransactionalEventListener(fallbackExecution = true)
    @Async
    public void onApplicationEvent(NoticeEventObj eventObj){
        System.out.println("进入2");
        //System.out.println("type"+eventObj.getType());
        //System.out.println("source"+eventObj.getSource());
//        String s = RlfHttpUtils.get("http://localhost:8888/sixSix/list",null);
//        System.out.println("调取信息"+s);
    }
}
