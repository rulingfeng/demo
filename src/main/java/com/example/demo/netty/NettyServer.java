/*
 * Copyright 2020 tu.cn All right reserved. This software is the
 * confidential and proprietary information of tu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tu.cn
 */
package com.example.demo.netty;


import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.InetAddress;

/**
 * @Author: Administrator
 * @Date: 2020-02-05 13:40
 * @Version 1.0
 *
 *
 * SpringCloud Nacos Gateway 负载均衡 Netty的Websocket
 *  网址：https://blog.csdn.net/qq_50909707/article/details/129010931
 *
 *  一、Gateway的WS协议配置
 *   - id: inm-msgpush-netty
 *       uri: lb:ws://inm-msgpush-netty
 *       predicates:
 *          - Path=/msgpushwss/**
 *       filters:
 *          - StripPrefix=1
 *
 *  二、yaml配置
 *   netty:
 *   port: 1117
 *   application:
 *     name: inm-msgpush-netty
 *
 *  三 加代码
 *   该类run方法，开启需要监听 的端口后
 *
 *     @Value("${netty.port}")
 *     private Integer port;
 *
 *     @Value("${netty.application.name}")
 *     private String serverName;
 *
 *     @Autowired
 *     private NacosDiscoveryProperties nacosDiscoveryProperties;
 *
 *     try {
 *             NamingService namingService = NamingFactory.createNamingService(nacosDiscoveryProperties.getServerAddr());
 *             InetAddress address = InetAddress.getLocalHost();
 *             namingService.registerInstance(serverName, address.getHostAddress(), port);
 *         } catch (Exception e) {
 *             throw new RuntimeException(e);
 *         }
 *
 *  四 链接方式
 *      wss://nainmdev.inm.cc/msgpushwss/storeCode=8883,33333
 *      tip：msgpushwss是根据gateway配置的Path后面的路径
 *
 *
 *  多集群netty 用户长链接，
 *  如果服务端需要发送消息， 需要把客户端连上的机器的ip给保存下来，只能通过这个ip给客户端发消息（因为通道只保存在这个ip的服务里面）
 *
 *              //获取当前机器ip加端口  端口从yaml的server.port拿
 *             //String localIp = InetAddress.getLocalHost().getHostAddress().concat(":").concat(port);
 *
 *             //请求接口地址   ip+端口 后面直接跟接口名 （不用在加服务名了）
 *             //String url2 = String.format("http://%s/%s?token=%s", ip, "user/ws/payResultMessage", token);
 *
 *
 *
 *
 *
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

//    @Autowired
//      private com.alibaba.cloud.nacos.NacosDiscoveryProperties nacosDiscoveryProperties;
    /**
     * 测试端口
     */
//    private Integer clientPort = 1117,devicePort = 1116;
    @Resource
    private ClientChannelHandler clientChannelHandler;
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
                .childHandler(clientChannelHandler);
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);


        //开启需要监听 的端口
        ChannelFuture clientFuture = client.bind(clientPort).sync();


//        try {
//              NamingService namingService = NamingFactory.createNamingService(nacosDiscoveryProperties.getServerAddr());
//              InetAddress address = InetAddress.getLocalHost();
//              namingService.registerInstance(serverName, address.getHostAddress(), port);
//        } catch (Exception e) {
//              throw new RuntimeException(e);
//        }



        if (clientFuture.isSuccess()) {
            log.info("启动客户端Netty成功端口:"+ clientPort);
        }
    }
}
