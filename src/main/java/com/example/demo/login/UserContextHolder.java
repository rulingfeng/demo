package com.example.demo.login;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户上下文 在拦截前取到token里面的用户信息，把东西塞进来， 通过threadLocal来存储信息;
 */
public class UserContextHolder {

    //userId
    private ThreadLocal<Long> threadLocal;

    private UserContextHolder() {
        this.threadLocal = new ThreadLocal<>();
    }

    /**
     * 创建实例
     *
     * @return
     */
    public static UserContextHolder getInstance() {
        return SingletonHolder.sInstance;
    }

    /**
     * 静态内部类单例模式
     * 单例初使化
     */
    private static class SingletonHolder {

        private static final UserContextHolder sInstance = new UserContextHolder();
    }

    /**
     * 用户上下文中放入信息
     *
     * @param userId
     */
    public void setContext(Long userId) {
        threadLocal.set(userId);
    }

    /**
     * 获取上下文中的信息
     *
     * @return
     */
    public Long getContext() {
        return threadLocal.get();
    }

    /**
     * 获取上下文中的用户名Id
     *
     * @return
     */
    public Long getUserId() {
        return getContext();
    }

    /**
     * 清空上下文
     */
    public void clear() {
        threadLocal.remove();
    }


    /**字符串转utf-8**/
    private String getUtf8String(String message){
        if(!StrUtil.isBlank(message)){
            message=new String(message.getBytes(),Charset.forName("UTF-8"));
        }
        return message;
    }

}
