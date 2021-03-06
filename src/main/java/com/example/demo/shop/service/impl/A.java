package com.example.demo.shop.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author RU
 * @date 2020/9/2
 * @Desc
 */

//循环依赖
@Component
public class A {


    private B b;

    //构造器注入
//    @Autowired
//    public A(B b) {
//        this.b = b;
//    }


    //set注入
    @Autowired
    private void setB(B b){
        this.b = b;
    }
    @PostConstruct
    public void dgf(){
        System.out.println( "b:"+b);
    }
}
