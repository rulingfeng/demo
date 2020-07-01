package com.example.demo.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 订单列表导出dto
 * @Author: RU
 * @Date: 2020/6/2 16:56
 */
@Data
public class ExportOrderDto extends BaseRowModel implements Serializable {
    @ExcelProperty(value = "订单号",index = 0)
    private String id;
    @ExcelProperty(value = "购买人手机号",index = 1)
    private String buyPhone;
    @ExcelProperty(value = "商品ID",index = 2)
    private String goodsId;
    @ExcelProperty(value = "商品类型",index = 3)
    private String goodsType;
    @ExcelProperty(value = "商品标题",index = 4)
    private String goodsName;
    @ExcelProperty(value = "商品规格",index = 5)
    private String skuInfo;
    @ExcelProperty(value = "数量",index = 6)
    private String goodsNum;
    @ExcelProperty(value = "单价",index = 7)
    private String price;
    @ExcelProperty(value = "运费",index = 8)
    private String expPrice;
    @ExcelProperty(value = "优惠券",index = 9)
    private String couponPrice;
    @ExcelProperty(value = "总价",index = 10)
    private String goodsPrice;
    @ExcelProperty(value = "下单时间",index = 11)
    private String createTime;
    @ExcelProperty(value = "支付时间",index = 12)
    private String payTime;
    @ExcelProperty(value = "收货人",index = 13)
    private String name;
    @ExcelProperty(value = "收货人手机号",index = 14)
    private String phone;
    @ExcelProperty(value = "收货地址",index = 15)
    private String address;
    @ExcelProperty(value = "状态",index = 16)
    private String orderStatusStr;
    @ExcelProperty(value = "快递公司",index = 17)
    private String expressName;
    @ExcelProperty(value = "快递单号",index = 18)
    private String expressNum;
    @ExcelProperty(value = "发货时间",index = 19)
    private String expressTime;
}
