package com.example.demo.model;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.example.demo.mapstruct.UserConverter;
import lombok.Data;

import java.io.Serializable;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Data
@TableName("user")
public class User extends Model<User> implements Serializable {
    private static final long serialVersionUID = -7757471143347689303L;
    @TableId
    protected Integer id;

    @TableField("user_name")
    protected String userName;

    @TableField("age")
    protected String age;

    @TableField(exist = false)
    protected String type;

    @TableField(exist = false)
    protected String brithday;


    public static void main(String[] args) {
        String a = "\"{\"age\":\"1\",\"id\":1,\"userName\":\"名1\"}\"";
        User user = JSONObject.parseObject(a.substring(1,a.length()-1), User.class);
        System.out.println(user);
    }

}