package com.example.demo.event;

import org.springframework.context.ApplicationEvent;

/**
 * 事件对象
 */
public class NoticeEventObj extends ApplicationEvent {
    private String type ;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public NoticeEventObj(Object source, String type) {
        super(source);
        this.type = type;
    }

    public static void main(String[] args) {
        String a = new String("你好").intern();
        String b = new String("你好").intern();
        System.out.println(a==b);
    }
}
