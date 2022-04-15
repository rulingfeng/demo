package com.example.demo.java8.concurrent;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

/**
 * ConcurrentHashMap的一些高级处理
 * @author Benjamin Winterberg
 */
public class ConcurrentHashMap1 {

    public static void main(String[] args) {
        System.out.println("Parallelism: " + ForkJoinPool.getCommonPoolParallelism());

       // testForEach();
        testSearch();
       // testReduce();
    }

    //ConcurrentHashMap 取出key+value, 然后在以逗号隔开
    //print r2=d2, c3=p0, han=solo, foo=bar
    private static void testReduce() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        map.putIfAbsent("foo", "bar");
        map.putIfAbsent("han", "solo");
        map.putIfAbsent("r2", "d2");
        map.putIfAbsent("c3", "p0");

        String reduced = map.reduce(1, (key, value) -> key + "=" + value,
                (s1, s2) -> s1 + ", " + s2);

        System.out.println(reduced);
    }

    //ConcurrentHashMap搜索keyvalue或者是单独的value
    private static void testSearch() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        map.putIfAbsent("foo", "bar");
        map.putIfAbsent("han", "solo");
        map.putIfAbsent("r2", "d2");
        map.putIfAbsent("c3", "p0");

        System.out.println("\nsearch()\n");
        String result1 = map.search(1, (key, value) -> {
            System.out.println(Thread.currentThread().getName());
            if (key.equals("foo") && value.equals("bar")) {
                return "foobar";
            }
            return null;
        });

        System.out.println(result1);

        System.out.println("\nsearchValues()\n");

        String result2 = map.searchValues(1, value -> {
            System.out.println(Thread.currentThread().getName());
            if (value.length() > 3) {
                return value;
            }
            return null;
        });

        System.out.println(result2);
    }

    //ConcurrentHashMap循环 以及 获取 ConcurrentHashMap的key数量
    private static void testForEach() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        map.putIfAbsent("foo", "bar");
        map.putIfAbsent("han", "solo");
        map.putIfAbsent("r2", "d2");
        map.putIfAbsent("c3", "p0");

        map.forEach(1, (key, value) -> System.out.printf("key: %s; value: %s; thread: %s\n", key, value, Thread.currentThread().getName()));
//        map.forEach(5, (key, value) -> System.out.printf("key: %s; value: %s; thread: %s\n", key, value, Thread.currentThread().getName()));

        System.out.println(map.mappingCount());
    }

}
