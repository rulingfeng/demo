package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author rulingfeng
 * @time 2022/10/24 14:32
 * @desc
 */
@Data
@TableName("order_log")
public class OrderLog extends Model<OrderLog> implements Serializable {
    private static final long serialVersionUID = 5051049135474518889L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "订单编号")
    private Long orderNo;


    private Long userId;
    private Date time;


}
