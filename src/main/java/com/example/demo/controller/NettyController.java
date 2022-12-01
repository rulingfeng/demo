package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.netty.ClientNettyHandler;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rulingfeng
 * @time 2022/11/11 09:47
 * @desc
 */
@RestController
@RequestMapping("/netty")
@Slf4j
public class NettyController {


    @GetMapping("/sendMsg")
    public String sendMsg(String storeCode) throws Exception {
        ClientNettyHandler.sendMsg(storeCode);
        return "成功";
    }
}
