package com.example.demo.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dao.OrderDetailMapper;
import com.example.demo.dao.OrderMapper;
import com.example.demo.model.Order;
import com.example.demo.model.OrderDetail;
import com.example.demo.service.OrderDetailService;
import com.example.demo.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {


}
