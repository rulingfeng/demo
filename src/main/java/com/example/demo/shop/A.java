package com.example.demo.shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author RU
 * @date 2020/9/2
 * @Desc
 */

//循环依赖
@Component
public class A {

    private final static ThreadLocal threadLocal = new ThreadLocal();
    private final static InheritableThreadLocal inheritableThreadLocal = new InheritableThreadLocal();



    public static native void sleep(long millis) ;

    public static void main(String[] args) {
        sleep(1000);
        System.out.println(1);
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
