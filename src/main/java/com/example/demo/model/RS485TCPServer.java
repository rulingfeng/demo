//package com.example.demo.model;
//
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.IOException;
//import java.net.ServerSocket;
//import java.net.Socket;
//
//@Slf4j
//public class RS485TCPServer implements Runnable {
//
//    public void run() {
//        try {
//            ServerSocket serverSocket = new ServerSocket(8888);
//            log.info("数码管服务启动（端口：8888）");
//            while (true) {
//                Socket socket = serverSocket.accept();
//                log.info("socket开始连接====");
//                RS485Connection rs485Connection = new RS485Connection(socket);
//                if (rs485Connection.isResult()) {
//                    Thread thread = new Thread(rs485Connection);
//                    thread.start();
//                    log.info("RS485服务器" + socket.getRemoteSocketAddress() + "连接成功");
//                } else {
//                    try {
//                        if (!socket.isClosed()) {
//                            socket.close();
//                        }
//                        log.info("RS485服务器" + socket.getRemoteSocketAddress() + "连接不成功");
//
//                    } catch (IOException ioException) {
//                        log.error(ioException.getMessage(), ioException);
//                    }
//
//                }
//
//
//            }
//        } catch (IOException ioException) {
//            log.error(ioException.getMessage(), ioException);
//        }
//    }
//
//
//}
