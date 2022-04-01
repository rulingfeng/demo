package com.example.demo.login;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @ProjectName: demo
 * @Package: com.example.demo.controller
 * @ClassName: DemoController
 * @Author: RU
 * @Description:
 * @Date: 2020/3/25 18:48
 * @Version: 1.0
 */
@RestController
@RequestMapping("/login")
@Slf4j
public class LoginController {


    @GetMapping("/login")
    public String login(){
        System.out.println("登录成功");
        HashMap<String, String> map = Maps.newHashMap();
        map.put("mobile","13755558866");
        map.put("name","李四");
        map.put("address","杭州");

        return JWTUtils.sign(map);
    }




}
