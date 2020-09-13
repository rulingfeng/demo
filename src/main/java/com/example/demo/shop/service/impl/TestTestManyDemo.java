package com.example.demo.shop.service.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author 茹凌丰
 * @Title:
 * @Package
 * @Description:
 * @date 2020/9/139:00
 */
public class TestTestManyDemo {

    public static  ThreadGroup g = new ThreadGroup("test");
    public static void main(String[] args) throws InterruptedException {
        ThreadGroup g=new ThreadGroup("child thread group");
        System.out.println(g.getName());
        System.out.println(g.getParent().getName());

        //g1 is the child of g.
        //g's name is child thread group
        //g1's name is sub child group.
        ThreadGroup g1=new ThreadGroup(g,"sub child group");
        System.out.println(g1.getName());
        System.out.println(g1.getParent().getName());
        Executors.newWorkStealingPool();
    }
}
