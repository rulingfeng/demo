package com.example.demo.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: RU
 * @Date: 2020/4/28 16:01
 */
@Configuration
@Data
public class GZHConfig {
    @Value("${gzhInfo.info.appId}")
    private String appId;
    @Value("${gzhInfo.info.appSecret}")
    private String appSecret;
    @Value("${gzhInfo.info.token}")
    private String token;
    @Value("${gzhInfo.templateId.carIn}")
    private String carIn;
    @Value("${gzhInfo.templateId.pay}")
    private String pay;
    @Value("${gzhInfo.templateId.carInError}")
    private String carInError;





}
