package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.dataSource.DataSource;
import com.example.demo.dataSource.DataSourceType;
import com.example.demo.model.User;

import java.util.List;

public interface UserService extends IService<User> {



    public void asd();

    public void asd2();

    public void asd3();

    public String getVerifyHash(Integer sid, Integer userId) throws Exception;

    public int addUserCount(Integer userId) throws Exception;

    public boolean getUserIsBanned(Integer userId);

    void asyTest() throws InterruptedException ;

    void testTransational() throws InterruptedException;

    void getContext();
    void getContext2();


}
