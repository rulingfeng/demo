package com.example.demo.dataSource;

/**
 * @program: parking_client
 * @description:
 * @author: 栗翱
 * @create: 2020-04-08 13:50
 **/
public enum  DataSourceType {
    db1("db1"),
    db2("db2");
    private String value;

    DataSourceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
