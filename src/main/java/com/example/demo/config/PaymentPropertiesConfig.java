package com.example.demo.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: ConfigurationProperties
 * @Description: 支付配置参数
 * @Author: stan
 * @CreateDate: 2019-06-30
 */
@Data
@Configuration
@ConditionalOnExpression("!'${payment}'.isEmpty()")
@ConfigurationProperties(prefix = "payment")
public class PaymentPropertiesConfig {
	/**
	 * 配置url
	 */
	private String config;
	private String appId;
	private String appSecret;
}
