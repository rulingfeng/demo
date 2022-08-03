package com.example.demo.model;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.example.demo.annotation.FlagValidator;
import com.example.demo.common.RSAUtil;
import com.example.demo.mapstruct.UserConverter;
import lombok.Data;
import org.apache.commons.codec.Decoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.*;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Data
@TableName("user")
public class User extends Model<User> implements Serializable {
    private static final long serialVersionUID = -7757471143347689303L;
    @TableId
    @FlagValidator(value = {"1","3"},message ="id只能是1和3") //com.example.demo.controller.TestController.testVaild
    protected Integer id;

    @TableField("user_name")
    @NotBlank(message = "名字不能为空")
    protected String userName;

    @TableField("age")
    protected String age;

    @TableField(exist = false)
    protected String type;

    @TableField(exist = false)
    protected String brithday;


    public static void main(String[] args) throws IOException, DocumentException {
        BigDecimal divide = new BigDecimal((1000000 - 956120) + "").divide(new BigDecimal(1000000 + ""), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
        System.out.println(divide.intValue());

        ShearCaptcha shearCaptcha= CaptchaUtil.createShearCaptcha(150, 30, 4, 2);
        System.out.println(shearCaptcha.getCode());







    }





}