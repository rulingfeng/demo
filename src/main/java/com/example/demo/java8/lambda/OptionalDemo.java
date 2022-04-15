package com.example.demo.java8.lambda;

import com.example.demo.model.User;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: 茹凌丰
 * @date: 2022/4/15
 * @description:
 */
public class OptionalDemo {

    public static void main(String[] args) {
        List<Integer> list = Lists.newArrayList(1,7,4);

        //ofNullable用来装任何一个对象,ifPresent执行条件为不null的时候执行
        //代替 if(null != list){
        //      todo...
        // }
        Optional.ofNullable(list).ifPresent(i-> list.sort(Comparator.naturalOrder()));
        System.out.println(list);//[1, 4, 7]

        User user = new User();
        user.setAge("2");
        System.out.println(user);
        Optional.ofNullable(user.getAge()).ifPresent(u->{
            if("3".equals(u)){
                throw new RuntimeException("出错");
            }else if("2".equals(u)){
                user.setAge("5");
            }
        });

    }
}
