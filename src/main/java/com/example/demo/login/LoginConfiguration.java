package com.example.demo.login;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LoginConfiguration  implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        LoginInterceptor loginInterceptor = new LoginInterceptor();
        InterceptorRegistration loginRegistry = registry.addInterceptor(loginInterceptor);
       loginRegistry.addPathPatterns("/**");
        //loginRegistry.excludePathPatterns("/","/index.html","/css/**","/js/**","/image/**","/assets/**","/ueditor/**");//根据自己目录添加更改
      //  loginRegistry.excludePathPatterns("/admin");//根据自己目录添加更改
       loginRegistry.excludePathPatterns("/login/*");//根据自己目录添加更改
    }
}
