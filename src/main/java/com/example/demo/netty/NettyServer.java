/*
 * Copyright 2020 tu.cn All right reserved. This software is the
 * confidential and proprietary information of tu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tu.cn
 */
package com.example.demo.netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * @Author: Administrator
 * @Date: 2020-02-05 13:40
 * @Version 1.0
 */
@Slf4j
@Component
public class NettyServer implements CommandLineRunner {

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workGroup = new NioEventLoopGroup();
    /**
     * 正式端口
     */
    private Integer clientPort = 1118;
    /**
     * 测试端口
     */
//    private Integer clientPort = 1117,devicePort = 1116;

    /**
     * 销毁
     */
    @PreDestroy
    public void destroy() {
        bossGroup.shutdownGracefully().syncUninterruptibly();
        workGroup.shutdownGracefully().syncUninterruptibly();
        log.info("关闭 Netty 成功");
    }

    @Override
    public void run(String... args) throws Exception {

        ServerBootstrap client = new ServerBootstrap();
        client.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new ClientChannelHandler());
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);


        //开启需要监听 的端口
        ChannelFuture clientFuture = client.bind(clientPort).sync();
        if (clientFuture.isSuccess()) {
            log.info("启动客户端Netty成功端口:"+ clientPort);
        }
    }
}
