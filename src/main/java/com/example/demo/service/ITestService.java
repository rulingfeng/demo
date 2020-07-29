package com.example.demo.service;

import com.example.demo.model.SmsHomeBrand;
import com.example.demo.model.Stock;
import com.example.demo.model.User;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.boon.di.In;

import java.util.List;
import java.util.Map;

/**
 * @ProjectName: demo
 * @Package: com.example.demo.service
 * @ClassName: ITestService
 * @Author: RU
 * @Description:
 * @Date: 2020/3/20 11:19
 * @Version: 1.0
 */
public interface ITestService {
    public PageInfo<SmsHomeBrand> selectAll();
    public Page<SmsHomeBrand> select();
    public Page<SmsHomeBrand> select(Integer aa);
    Page<Stock> selectStock();
    Integer updateNum(String name)throws Exception;

    void async() throws Exception ;
    Object redisToDb(Integer i) ;
    User cache(Integer id);

    List<User> jetCache(Integer id);
    Integer jetCache111(Integer id);

    Integer delete(Integer id);
    Integer update(Integer id);

    void compleableSave();
    boolean dfsdf(Integer num);
}
