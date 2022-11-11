/*
 * Copyright 2020 tu.cn All right reserved. This software is the
 * confidential and proprietary information of tu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tu.cn
 */
package com.example.demo.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.PlatformDependent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author: Administrator
 * @Date: 2020-02-05 14:11
 * @Version 1.0
 */
@Component
@Slf4j
public class ClientNettyHandler extends SimpleChannelInboundHandler<Object> {

    public static ChannelGroup group = new DefaultChannelGroup("CLIENT_GROUP", GlobalEventExecutor.INSTANCE);
    private static final ConcurrentMap<String, Channel> clientChannels = PlatformDependent.newConcurrentHashMap();

    private WebSocketServerHandshaker handshaker;
    private static ClientNettyHandler clientNettyHandler;



    @PostConstruct
    public void init() {
        clientNettyHandler = this;
    }

    /**
     * channel 通道 action 活跃的 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive方法");
        ChannelId id = ctx.channel().id();
        System.out.println(id);

        log.info("+++++++++++++++客户端与服务端连接开启：{}", ctx.channel().remoteAddress().toString());
    }

    /**
     * channel 通道 Inactive 不活跃的 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端关闭了通信通道并且不可以传输数据
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive方法");
        Attribute clientAttr = ctx.channel().attr(AttributeKey.valueOf("storeCode"));
        try {
            if (null != clientAttr && null != clientAttr.get()) {
                String storeCode = clientAttr.get().toString();
                log.info("+++++++++++++++客户端与服务端连接关闭：{}", storeCode);
                clientChannels.remove(storeCode, ctx.channel());
            }
        } catch (Exception e) {
            log.error("+++++++++++++++关闭不活跃客户端连接发生异常：{}");
        }
        group.remove(ctx.channel());
        log.info("+++++++++++++++客户端与服务端连接关闭:{},当前连接数量:{}，group连接数量:{}", ctx.channel().remoteAddress().toString(), clientChannels.size(), group.size());
    }

    /**
     * 接收客户端发送的消息 channel 通道 Read 读 简而言之就是从通道中读取数据，也就是服务端接收客户端发来的数据。但是这个数据在不进行解码时它是ByteBuf类型的
     *
     * @param channelHandlerContext
     * @param msg 客户端传过来的数据
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        System.out.println("channelRead0方法");
        //初始握手
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(channelHandlerContext, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handlerWebSocketFrame(channelHandlerContext, (WebSocketFrame) msg);
        } else {
            //TODO 其他处理
            log.info("+++++++++++++++其他情况暂不处理："+ msg.getClass().getName());
            return;
        }
    }

    /**
     * channel 通道 Read 读取 Complete 完成 在通道读取完成后会在这个方法里通知，对应可以做刷新操作 ctx.flush()
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelReadComplete方法");
        ctx.flush();
    }

    /**
     * exception 异常 Caught 抓住 抓住异常，当发生异常的时候，可以做一些相应的处理，比如打印日志、关闭链接
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exceptionCaught方法");
        ctx.close();
        String e = cause.getLocalizedMessage();
        if(cause instanceof Exception){
        }
        log.error("+++++++++++++++连接发生异常：{}",e);
    }

    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        System.out.println("handlerWebSocketFrame方法");
        //获取设备id
        Attribute attr = ctx.channel().attr(AttributeKey.valueOf("storeCode"));
        String storeCode = (null != attr && null != attr.get()) ? attr.get().toString() : "";

        //关闭连接请求
        if (frame instanceof CloseWebSocketFrame) {
            log.info("+++++++++++++++门店连接关闭,storeCode：{}", storeCode);
            ctx.channel().close();
            return;
        }
        //ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        //文本消息
        if (frame instanceof TextWebSocketFrame) {
            String msg = ((TextWebSocketFrame) frame).text();
            handlerMessage(ctx, msg, storeCode);
        }
    }

    /**
     * 处理 http 请求，WebSocket 初始握手 (opening handshake ) 都始于一个 HTTP 请求
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        System.out.println("handleHttpRequest方法");
        // 如果HTTP解码失败，返回HTTP异常
        if (!req.getDecoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade")))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        //获取url后置参数
        HttpMethod method = req.getMethod();
        String uri = req.getUri();
        uri = uri.replaceAll("/", "");
        String[] split = uri.split("=");
        if (method == HttpMethod.GET && "storeCode".equals(split[0])) {
            ctx.channel().attr(AttributeKey.valueOf("storeCode")).set(split[1]);
            clientChannels.put(split[1], ctx.channel());
        }

        // 构造握手响应返回，本机测试
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://" + req.headers().get(HttpHeaders.Names.HOST) + uri, null, false, 65536 * 10);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    /**
     * 响应非 WebSocket 初始握手请求
     */
    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {
        System.out.println("sendHttpResponse方法");
        // 返回应答给客户端
        if (res.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpHeaders.isKeepAlive(req) || res.getStatus().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }



    private static void handlerMessage(ChannelHandlerContext channelHandlerContext, String msg, String storeCode) {
        System.out.println("handlerMessage方法");
        System.out.println(channelHandlerContext.channel());
        System.out.println(channelHandlerContext.channel().id());
        try {
            //非心跳检测
            if (!(msg.indexOf("HEARTBEAT")> 0)){
                log.info("+++++++++++++++处理后台发送过来的数据,msg：{}" , msg);
            }
            log.info("收到{}发来消息[{}]",storeCode,msg);
        } catch (Exception e) {
            log.error("+++++++++++++++门店id：{}，处理推送数据发生异常：{}" ,storeCode);
        }
    }

    public static void sendMsg(String storeCode) throws Exception {
        //System.out.println("sendMsg方法");
        try {
            if (clientChannels.containsKey(storeCode)){
                log.info("+++++++++++++推送门店id：" + storeCode + ",channel id:" + clientChannels.get(storeCode).id());
                 TextWebSocketFrame send = new TextWebSocketFrame(storeCode);
                 clientChannels.get(storeCode).writeAndFlush(send);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
