package com.example.demo.dao;

import com.example.demo.model.Goods;
import com.example.demo.model.SmsHomeBrand;
import com.example.demo.model.Stock;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ProjectName: demo
 * @Package: com.example.demo.dao
 * @ClassName: TestMapper
 * @Author: RU
 * @Description:
 * @Date: 2020/3/20 11:21
 * @Version: 1.0
 */
@Repository
public interface TestMapper {

    List<SmsHomeBrand> selectAll();
    Integer updateNum();
    Integer insert(String name);
    Goods select();
    List<Stock> selectStock();
}
