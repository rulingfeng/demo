package com.example.demo.shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author RU
 * @date 2020/9/2
 * @Desc
 */

//循环依赖
@Component
public class A {

    private final static ThreadLocal<String> threadLocal = new ThreadLocal();
    private final static InheritableThreadLocal inheritableThreadLocal = new InheritableThreadLocal();





    public static void main(String[] args)  {


    }


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
