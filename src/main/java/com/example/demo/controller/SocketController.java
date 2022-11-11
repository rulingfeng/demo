package com.example.demo.controller;

import com.example.demo.netty.ClientNettyHandler;
import com.example.demo.socket.WebSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rulingfeng
 * @time 2022/11/11 09:47
 * @desc
 */
@RestController
@RequestMapping("/socket")
@Slf4j
public class SocketController{

    @Autowired
    private WebSocket webSocket;

    @GetMapping("/sendMsg")
    public String sendMsg(String storeCode) throws Exception {
       webSocket.sendMessage(storeCode,"服务端发送消息");
        return "成功socket";
    }

}
