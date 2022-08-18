package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@TableName("user_car")
public class UserCar extends Model<UserCar> implements Serializable {
    private static final long serialVersionUID = -7757471143347689303L;
    @TableId
    protected Integer id;

    @TableField("user_id")
    @NotNull(message = "userId不能为空")
    protected Integer userId;

    @TableField("car")
    protected String car;




}