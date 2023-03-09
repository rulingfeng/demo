package com.example.demo.qywxbot;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * @author rulingfeng
 * @time 2023/3/8 17:10
 * @desc
 */
@Data
public class OffShelfSkuInfo {

    @Excel(
            name = "订单号",
            width = 20.0,
            orderNum = "1"
    )
    private String orderId; // 订单编号

    @Excel(
            name = "支付金额",
            width = 20.0,
            orderNum = "2"
    )
    private String payment; // 支付金额

}
