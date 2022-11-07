package com.example.demo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author
 * @since 2021-01-13
 */
 @Mapper
 public interface OrderDetailMapper extends BaseMapper<OrderDetail> {


}
