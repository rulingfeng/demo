package com.example.demo.socket;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WsSessionManager {

    /*
      保存连接 session 的地方
     */
    private static final ConcurrentHashMap<String, WebSocketSession> SESSION_POOL = new ConcurrentHashMap<>();


    /**
     * 添加 session
     */
    public static void add(String key, WebSocketSession session) {
        // 刷新页面连接的请求会比断开的优先，加上以下判断线程安全会先执行下面的删除，然后执行退出socket的删除，再执行添加
        // 不然优先添加后，退出的socket请求会等待新增，导致删除了最新session
        if (get(key) != null) {
            removeAndClose(key);
        }
        String ip = session.getAttributes().get("ip").toString();
        SESSION_POOL.put(key, session);

    }

    /**
     * 删除 session,会返回删除的 session
     */
    public static WebSocketSession remove(String key) {
        // 删除 session
        return SESSION_POOL.remove(key);
    }

    /**
     * 删除并同步关闭连接
     */
    public static void removeAndClose(String key) {
        WebSocketSession session = remove(key);
        if (session != null) {
            try {
                // 关闭连接
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获得 session
     */
    public static WebSocketSession get(String key) {
        return SESSION_POOL.get(key);
    }
}
