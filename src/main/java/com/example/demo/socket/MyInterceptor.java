package com.example.demo.socket;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

@Component
@Slf4j
public class MyInterceptor implements HandshakeInterceptor {
    @Value("${server.port}")
    private String port;
    /**
     * 握手前
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String ip = null;
        try {
            // 获取本地的ip，有多个网卡不清楚会是哪个
            InetAddress addr = InetAddress.getLocalHost();
            ip = addr.getHostAddress();
            ip += ":" + port;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        log.info("握手开始 {}", ip);
        // 获得请求参数
        String query = request.getURI().getQuery();
        if (query == null) {
            return false;
        }
        String uid = null;
        String[] queryArr = query.split("/");
        String tmp;
        if ((tmp = queryArr[0]) != null) {
            uid = tmp;
        }
        if (StrUtil.isNotBlank(uid)) {
            // 放入属性域
            attributes.put("token", uid);
            attributes.put("ip", ip);

            log.info("用户 {} 握手成功!", uid);
            return true;
        }
        log.info("用户登录已失效");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        log.info("握手结束");
    }
}
