package com.example.demo.email;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author rulingfeng
 * @time 2022/11/8 14:34
 * @desc
 */
@Data
public class MailExportVO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("支付流水id")
    private Integer id;
    @Excel(
            name = "订单号",
            width = 20.0,
            orderNum = "1"
    )
    @ApiModelProperty("订单号")
    private String outOrderSn;
    @Excel(
            name = "订单金额",
            width = 20.0,
            orderNum = "2"
    )
    @ApiModelProperty("订单金额(元)")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING
    )
    private BigDecimal orderAmount;
    @ApiModelProperty("已退款金额(元)")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING
    )
    private BigDecimal refundAmount;
    @ApiModelProperty("最多可退金额(元)")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING
    )
    private BigDecimal resultAmount;
    @Excel(
            name = "下单时间",
            format = "yyyy-MM-dd HH:mm:ss",
            width = 20.0,
            orderNum = "3"
    )
    @ApiModelProperty("下单时间")
    private Date createTime;
    @Excel(
            name = "订单状态",
            replace = {"已下单_0", "已完成_1", "已退款_2", "_null"},
            width = 20.0,
            orderNum = "6"
    )
    @ApiModelProperty("订单状态 0:已下单 1:已完成 2:已退款")
    private Integer status;
    @Excel(
            name = "支付通道",
            replace = {"支付宝_1", "微信支付_2", "农行微信支付_3", "_null"},
            width = 20.0,
            orderNum = "4"
    )
    @ApiModelProperty("支付通道 1:支付宝 2:微信 3:农行微信")
    private Integer payChannel;
    @Excel(
            name = "支付时间",
            format = "yyyy-MM-dd HH:mm:ss",
            width = 20.0,
            orderNum = "5"
    )
    @ApiModelProperty("支付时间")
    private Date payTime;
    @Excel(
            name = "支付流水号",
            width = 35.0,
            orderNum = "7"
    )
    @ApiModelProperty("支付流水号")
    private String channelOrderSn;
    @Excel(
            name = "资金状态",
            replace = {"已入账_1", "已退款_2", "_null"},
            width = 20.0,
            orderNum = "8"
    )
    @ApiModelProperty("资金状态 1:已入账 2:已退款")
    private Integer fundStatus;
}
