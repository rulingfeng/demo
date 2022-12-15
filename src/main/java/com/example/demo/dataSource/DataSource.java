package com.example.demo.dataSource;



import java.lang.annotation.*;

/**
 * @program: parking_client
 * @description:
 * @author: 栗翱
 * @create: 2020-04-08 14:06
 *
 *
 * //需要配合Override使用
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
    /**
     * 切换数据源名称
     */
    DataSourceType value() default DataSourceType.db1;
}
