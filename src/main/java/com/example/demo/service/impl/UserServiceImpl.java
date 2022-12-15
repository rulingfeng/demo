package com.example.demo.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.common.CacheKey;
import com.example.demo.common.RlfHttpUtils;
import com.example.demo.dao.UserMapper;
import com.example.demo.dataSource.DataSource;
import com.example.demo.dataSource.DataSourceType;
import com.example.demo.event.NoticeEventObj;
import com.example.demo.model.Stock;
import com.example.demo.model.User;
import com.example.demo.service.StockService;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String SALT = "randomString";
    private static final int ALLOW_COUNT = 10;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StockService stockService;
    @Autowired
    ApplicationContext applicationContext;


    @Override
    @DataSource(DataSourceType.db1)
    public void asd(){
        List<User> list = this.lambdaQuery().list();
        System.out.println(1);
        System.out.println(list);
    }

    @Override
    @DataSource(DataSourceType.db2)
    public void asd2(){
        List<User> list = this.lambdaQuery().list();
        System.out.println(2);
        System.out.println(list);
    }

    @Override
    @DataSource(DataSourceType.db3)//需要配合Override使用
    public void asd3(){
        List<User> list = this.lambdaQuery().list();
        System.out.println(3);
        System.out.println(list);
    }


    @Override
    public void testTransational() throws InterruptedException {
        User user = new User();
        user.setId(1);
        user.setUserName("nihao11111");
        user.setAge("15");
        this.save(user);
        applicationContext.publishEvent(new NoticeEventObj(1111,"订阅"));

        Thread.sleep(1000);
        System.out.println(1);
        return;
    }

    @Override
    public String getVerifyHash(Integer sid, Integer userId) throws Exception {

        // 验证是否在抢购时间内
        LOGGER.info("请自行验证是否在抢购时间内");


        // 检查用户合法性
        User user = userMapper.selectByPrimaryKey(userId.longValue());
        if (user == null) {
            throw new Exception("用户不存在");
        }
        LOGGER.info("用户信息：[{}]", user.toString());

        // 检查商品合法性
        Stock stock = stockService.getStockById(sid);
        if (stock == null) {
            throw new Exception("商品不存在");
        }
        LOGGER.info("商品信息：[{}]", stock.toString());

        // 生成hash
        String verify = SALT + sid + userId;
        String verifyHash = DigestUtils.md5DigestAsHex(verify.getBytes());

        // 将hash和用户商品信息存入redis
        String hashKey = CacheKey.HASH_KEY.getKey() + "_" + sid + "_" + userId;
        stringRedisTemplate.opsForValue().set(hashKey, verifyHash, 3600, TimeUnit.SECONDS);
        LOGGER.info("Redis写入：[{}] [{}]", hashKey, verifyHash);
        return verifyHash;
    }

    @Override
    public int addUserCount(Integer userId) throws Exception {
        String limitKey = CacheKey.LIMIT_KEY.getKey() + "_" + userId;
        String limitNum = stringRedisTemplate.opsForValue().get(limitKey);
        int limit = -1;
        if (limitNum == null) {
            stringRedisTemplate.opsForValue().set(limitKey, "0", 3600, TimeUnit.SECONDS);
        } else {
            limit = Integer.parseInt(limitNum) + 1;
            stringRedisTemplate.opsForValue().set(limitKey, String.valueOf(limit), 3600, TimeUnit.SECONDS);
        }
        return limit;
    }

    @Override
    public boolean getUserIsBanned(Integer userId) {
        String limitKey = CacheKey.LIMIT_KEY.getKey() + "_" + userId;
        String limitNum = stringRedisTemplate.opsForValue().get(limitKey);
        if (limitNum == null) {
            LOGGER.error("该用户没有访问申请验证值记录，疑似异常");
            return true;
        }
        return Integer.parseInt(limitNum) > ALLOW_COUNT;
    }

    @Async
    @Override
    public void asyTest() throws InterruptedException {
        Thread.sleep(5000);
        System.out.println("111111");
    }
}
