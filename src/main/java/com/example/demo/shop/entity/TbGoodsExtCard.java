package com.example.demo.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商品卡片扩展表
 * </p>
 *
 * @author rlf
 * @since 2020-07-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_goods_ext_card")
public class TbGoodsExtCard implements Serializable {

    private static final long serialVersionUID = 1L;

    public static void main(String[] args) {

    }
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 商品ID
     */
    @TableField("goods_spu_id")
    private Long goodsSpuId;

    /**
     * 卡名称
     */
    @TableField("card_name")
    private String cardName;

    /**
     * 初始提货次数
     */
    @TableField("init_total_count")
    private Integer initTotalCount;

    /**
     * 卡片有效时长 (天数)
     */
    @TableField("effective_time")
    private Integer effectiveTime;

    /**
     * 创建时间（时间戳）
     */
    @TableField("create_time")
    private Long createTime;

    /**
     * 更新时间（时间戳）
     */
    @TableField("update_time")
    private Long updateTime;

    /**
     * 删除状态 0正常 1已删除
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;


}
