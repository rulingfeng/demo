package com.example.demo.service.impl;

import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheRefresh;
import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.common.DataSourceTypeEnum;
import com.example.demo.dao.TestMapper;
import com.example.demo.dataSource.DataSource;
import com.example.demo.dataSource.DataSourceType;
import com.example.demo.model.Goods;
import com.example.demo.model.SmsHomeBrand;
import com.example.demo.model.Stock;
import com.example.demo.model.User;
import com.example.demo.service.ITestService;

import com.example.demo.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: demo
 * @Package: com.example.demo.service.impl
 * @ClassName: TestServiceImpl
 * @Author: RU
 * @Description:
 * @Date: 2020/3/20 11:19
 * @Version: 1.0
 */
@Service
@Slf4j
public class TestServiceImpl implements ITestService {
    @Resource
    private TestMapper testMapper;
    @Autowired
    UserService userService;
    @Override
    public PageInfo<SmsHomeBrand> selectAll() {
        PageInfo<SmsHomeBrand> page = PageHelper.startPage(1, 5).doSelectPageInfo(() -> testMapper.selectAll());
        return page;
    }

    @Override
    @DataSource(DataSourceType.db1)
    public Page<SmsHomeBrand> select() {
        Page<SmsHomeBrand> page = PageHelper.startPage(1, 5).doSelectPage(() -> testMapper.selectAll());
        return page;
    }
    @Override
    public Page<SmsHomeBrand> select(Integer aa) {
        Page<SmsHomeBrand> page = PageHelper.startPage(1, 5).doSelectPage(() -> testMapper.selectAll());
        return page;
    }

    @Override
    @DataSource(DataSourceType.db2)
    public Page<Stock> selectStock() {
        Page<Stock> page = PageHelper.startPage(1, 5).doSelectPage(() -> testMapper.selectStock());
        return page;
    }

    @Override
    public Integer updateNum(String name) throws Exception{
        //校验库存
        Goods select = testMapper.select();
        if (select.getSaleNum()== select.getTotalNum()){
            throw new Exception("库存不足");
        }

        //扣库存
        Integer integer = testMapper.updateNum();
        if (integer > 0){
            //创建订单
            testMapper.insert(name);
        }
        return null;
    }

    @Override
    public void async() throws Exception {
        userService.asyTest();
    }



    @Override
    public Object redisToDb(Integer i)  {
        System.out.println("redisToDb方法"+i);
        return new Object();
    }

    @Override
    @Cacheable(value = "test_cache",key = "#id",condition = "#id<50")
    public User cache(Integer id) {
        System.out.println("cache方法"+id);
        User user = new User();
        user.setId(id);
        return user;
    }

    @Override
    @Cached(name = "jetCache",key = "#id")
    public List<User> jetCache(Integer id) {
        System.out.println("jetCache方法"+id);
        LambdaQueryWrapper<User> query = Wrappers.lambdaQuery();
        query.eq(User::getAge,id);
        List<User> list = userService.list(query);
        return list;
    }

    @Override
    @Cached(name = "jetCache",key = "#id")
    public Integer jetCache111(Integer id) {
        System.out.println(1);
        return 10;
    }

    @Override
    @CacheInvalidate(name = "jetCache",key = "#id")
    public Integer delete(Integer id) {
        return  5;
    }

    @Override
    @CacheUpdate(name = "jetCache",key = "#id",value = "#id")
    public Integer update(Integer id) {
        return 5;
    }
}
