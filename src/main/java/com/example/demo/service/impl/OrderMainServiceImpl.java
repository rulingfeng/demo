package com.example.demo.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dao.OrderDetailMapper;
import com.example.demo.dao.OrderMainMapper;
import com.example.demo.model.OrderDetail;
import com.example.demo.model.OrderMain;
import com.example.demo.service.OrderDetailService;
import com.example.demo.service.OrderMainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderMainServiceImpl extends ServiceImpl<OrderMainMapper, OrderMain> implements OrderMainService {


}
