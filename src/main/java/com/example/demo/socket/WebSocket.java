package com.example.demo.socket;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *  websocket的实例
 * 连接：ws://localhost:8886/ws?8888
 *                 8888可以模拟成门店id
 *                 发送消息的时候，传8888就能给指定通道发消息
 */
@Slf4j
@Service
public class WebSocket {

    @Value("${server.port}")
    private String port;

    public String sendMessage(String token, Object data) {
        try {
            WebSocketSession session = WsSessionManager.get(token);
            session.sendMessage(new TextMessage(String.valueOf(data)));
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "OK";
    }



}