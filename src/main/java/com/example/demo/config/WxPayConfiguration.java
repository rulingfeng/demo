package com.example.demo.config;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Binary Wang
 */
@Configuration
@EnableConfigurationProperties({WxPayPropertiesForApp.class})
@AllArgsConstructor
public class WxPayConfiguration {
  private WxPayPropertiesForApp wxPayPropertiesForApp;

  @Bean
//  @ConditionalOnMissingBean
  public Object wxService() {
    System.out.println(wxPayPropertiesForApp.getAppId());
    System.out.println("aa:"+wxPayPropertiesForApp.getKeyPath());
    return new Object();
  }



}
