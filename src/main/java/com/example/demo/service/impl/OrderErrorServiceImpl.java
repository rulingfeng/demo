package com.example.demo.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dao.OrderErrorMapper;
import com.example.demo.dao.OrderLogMapper;
import com.example.demo.model.OrderError;
import com.example.demo.model.OrderLog;
import com.example.demo.service.OrderErrorService;
import com.example.demo.service.OrderLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderErrorServiceImpl extends ServiceImpl<OrderErrorMapper, OrderError> implements OrderErrorService {


}
