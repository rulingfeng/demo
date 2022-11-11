package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.netty.ClientNettyHandler;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
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

    @Autowired
    private ClientNettyHandler clientNettyHandler;

    @GetMapping("/sendMsg")
    public String sendMsg(String boxId) throws Exception {
        ClientNettyHandler.sendMsg(boxId);
        return "成功";
    }
}
