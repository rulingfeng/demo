package com.example.demo.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @author rulingfeng
 * @time 2023/4/13 10:26
 * @desc 心跳检测          时间参考IdleStateHandler的构造函数
 */

@Slf4j
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            Channel channel = ctx.channel();
            Attribute attr = ctx.channel().attr(AttributeKey.valueOf("storeCode"));
            Object o = attr.get();
            // 读空闲
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                log.info("client:{} READER_IDLE...", o);
                // 写空闲
            } else if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                log.info("client:{} WRITER_IDLE...", o);
                // 读写空闲
            } else if (idleStateEvent.state() == IdleState.ALL_IDLE) {
                log.info("client:{} ALL_IDLE...", o);
                //关闭链接
                channel.close();
            }
        }
    }
}
