/*
 * Copyright 2020 tu.cn All right reserved. This software is the
 * confidential and proprietary information of tu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tu.cn
 */
package com.example.demo.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;

import javax.annotation.Resource;

/**
 * @Author: Administrator
 * @Date: 2020-02-05 14:10
 * @Version 1.0
 */
@Component
public class ClientChannelHandler  extends ChannelInitializer<SocketChannel> {

        /**
     * webSocket协议名
     */
    static final String WEBSOCKET_PROTOCOL = "WebSocket";

    /**
     * webSocket路径
     */
    @Value("${webSocket.netty.path:/webSocket}")
    String webSocketPath;

    @Resource
    private ClientNettyHandler clientNettyHandler;


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                //参数对应 读空闲(客户端发送服务端)  写空闲(服务端发送客户端)  读写空闲(双向读写都没有反应)   0代表永久 默认秒
                .addLast(new IdleStateHandler(60,0,0))
               // .addLast(new ClientHeartbeatHandler())
                .addLast("http-codec", new HttpServerCodec())// http编解码器，websocket 本身是基于http协议的
                .addLast("aggregator", new HttpObjectAggregator(165536))// http的 chunked 的消息聚合为完成的请求FullHttpRequest，内容最大长度65535
                .addLast("http-chunked", new ChunkedWriteHandler())
                //心跳
               //.addLast(new HeartBeatHandler())
                .addLast(clientNettyHandler);

//                .addLast(new LoggingHandler(LogLevel.TRACE))
//                // HttpRequestDecoder和HttpResponseEncoder的一个组合，针对http协议进行编解码
//                .addLast(new HttpServerCodec())
//                // 分块向客户端写数据，防止发送大文件时导致内存溢出， channel.write(new ChunkedFile(new File("bigFile.mkv")))
//                .addLast(new ChunkedWriteHandler())
//                // 将HttpMessage和HttpContents聚合到一个完成的 FullHttpRequest或FullHttpResponse中,具体是FullHttpRequest对象还是FullHttpResponse对象取决于是请求还是响应
//                // 需要放到HttpServerCodec这个处理器后面
//                .addLast(new HttpObjectAggregator(10240))
//                // 聚合 websocket 的数据帧，因为客户端可能分段向服务器端发送数据
//                .addLast(new WebSocketFrameAggregator(10 * 1024 * 1024))
//                // 服务器端向外暴露的 web socket 端点
////                .addLast(new WebSocketServerProtocolHandler("/client"))
//                .addLast(new WebSocketServerProtocolHandler(webSocketPath, WEBSOCKET_PROTOCOL, true, 65536 * 10))
//        // 自定义处理器 - 处理 web socket 文本消息
//                .addLast(new ClientNettyHandler());
    }
}
