package com.example.demo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.OrderDetail;
import com.example.demo.model.OrderMain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author
 * @since 2021-01-13
 */
 @Mapper
 public interface OrderMainMapper extends BaseMapper<OrderMain> {

      @Update("CREATE TABLE IF NOT EXISTS ${name} LIKE order_main")
      void createTable(@Param("name") String name);
      // 查询 sharding好像查不了
      @Select("SHOW TABLES LIKE 'order_main_%'")
      List<String> showTables();
      // 查询 sharding直接不支持
      @Select("select table_name from information_schema.tables where table_schema='order' and table_name like \"order_main%\"")
      List<String> showTables2();

}
