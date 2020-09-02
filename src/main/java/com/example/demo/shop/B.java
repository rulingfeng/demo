package com.example.demo.shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author RU
 * @date 2020/9/2
 * @Desc
 */
@Component
public class B {


    private A a;

    @Autowired
    public B(A a) {
        this.a = a;
    }

//    @Autowired
//    private void setA(A a){
//        this.a = a;
//    }

    @PostConstruct
    public void dgf(){
        System.out.println( "a:"+a);
    }
}
