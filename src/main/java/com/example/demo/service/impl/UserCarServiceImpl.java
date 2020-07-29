package com.example.demo.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.common.CacheKey;
import com.example.demo.dao.UserCarMapper;
import com.example.demo.dao.UserMapper;
import com.example.demo.event.NoticeEventObj;
import com.example.demo.model.Stock;
import com.example.demo.model.User;
import com.example.demo.model.UserCar;
import com.example.demo.service.StockService;
import com.example.demo.service.UserCarService;
import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.concurrent.TimeUnit;

@Service
public class UserCarServiceImpl extends ServiceImpl<UserCarMapper, UserCar> implements UserCarService {


}
