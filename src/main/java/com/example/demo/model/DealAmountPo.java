package com.example.demo.model;

import com.example.demo.aspect.OrderRateLimit;
import com.google.common.util.concurrent.RateLimiter;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;

/**
 * @Description: 处理临时优惠金额类
 * @Author: RU
 * @Date: 2020/5/30 14:59
 */
@Data
public class DealAmountPo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 购物车id
     */
    private Long cartMainId;

    /**
     * 商品成交价格
     */
    private Integer actPrice;



}
