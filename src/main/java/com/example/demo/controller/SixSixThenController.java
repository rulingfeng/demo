package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.aspect.OrderRateLimit;
import com.example.demo.model.ExportOrderDto;
import com.example.demo.model.ImportDataListener;
import com.example.demo.model.User;
import com.example.demo.model.UserVo;
import com.example.demo.service.ITestService;
import com.example.demo.service.UserService;
import com.example.demo.service.impl.RedisToDbOneImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openjdk.jol.info.ClassLayout;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Description:
 * @Author: RU
 * @Date: 2020/5/6 16:51
 */
@RestController
@RequestMapping("/sixSix")
@Slf4j
public class SixSixThenController {

    @Autowired
    UserService  userService;



    @GetMapping("/tran")
    public String tran() throws InterruptedException {
        userService.testTransational();

        return "成功";
    }




    @GetMapping("/list")
    @Transactional
    public String list() {
        User user = new User();
        user.setId(1);
        user.setAge((Math.random()*100)+"");
        boolean b = userService.updateById(user);

        System.out.println(b);

        return "调用成功";
    }
}
