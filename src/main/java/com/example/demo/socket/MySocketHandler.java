package com.example.demo.socket;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Component
public class MySocketHandler extends TextWebSocketHandler {


    /**
     * socket 建立成功事件
     *
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("连接");
        Object token = session.getAttributes().get("token");

            // 用户连接成功，放入在线用户缓存
        WsSessionManager.add(token.toString(), session);


    }

    /**
     * 接收消息事件
     *
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 获得客户端传来的消息
        String payload = message.getPayload();
        Object token = session.getAttributes().get("token");
        JSONObject json = new JSONObject();
        try {
            System.out.println(payload);
            log.info("server 接收到 " + token + " 发送的 {}", payload);
        } catch (JSONException e) {
            json.put("err", "JSON格式错误");
            log.info(json.get("err").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        json.put("msg", "收到消息");
        try {
            session.sendMessage(new TextMessage(json.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * socket 断开连接时
     *
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Object token = session.getAttributes().get("token");
        if (token != null) {
            // 用户退出，移除缓存 刷新页面会调用退出和连接，由于线程安全执行put和remove，会删除最新的连接
            log.info("退出");
            WsSessionManager.removeAndClose(token.toString());

        }
    }
}
