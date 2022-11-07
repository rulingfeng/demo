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
@TableName("order_main")
public class OrderMain extends Model<OrderMain> implements Serializable {
    private static final long serialVersionUID = 5051049135474510059L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "订单编号")
    private Long orderNo;

    private Long userId;
    private String cstatus;
    @ApiModelProperty(value = "根据月分表的规则,格林尼治 秒")
    private Long dynamicTime;
    private Date createTime;


}
