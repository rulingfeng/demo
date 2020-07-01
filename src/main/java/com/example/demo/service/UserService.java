package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.model.User;

public interface UserService extends IService<User> {

    public String getVerifyHash(Integer sid, Integer userId) throws Exception;

    public int addUserCount(Integer userId) throws Exception;

    public boolean getUserIsBanned(Integer userId);

    void asyTest() throws InterruptedException ;

    void testTransational() throws InterruptedException;

}
