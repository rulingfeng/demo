package com.example.demo.aspect;


import com.alibaba.fastjson.JSONObject;
import com.example.demo.common.CommonResult;
import com.example.demo.model.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * HibernateValidator错误结果处理切面
 * Created by macro on 2018/4/26.
 */
@Aspect
@Component
@Order(2)
public class BindingResultAspect {

    @Resource
    private HttpServletResponse response;

    @Pointcut("execution(public * com.example.demo.controller.*.*(..))")
    public void BindingResult() {
    }

    @Around("BindingResult()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof BindingResult) {
                BindingResult result = (BindingResult) arg;
                if (result.hasErrors()) {
                    FieldError fieldError = result.getFieldError();
                    if(fieldError!=null){
                        CommonResult<Object> objectCommonResult = CommonResult.validateFailed(fieldError.getDefaultMessage());
                        String s = JSONObject.toJSONString(objectCommonResult);
                        //只有输出在流里面,客户端才能接受到返回信息, 直接return是接收不到的,是空
                        output(response,s);
                        return null;
                    }else{
                        return CommonResult.validateFailed();
                    }
                }
            }
        }
        return joinPoint.proceed();
    }

    private void output(HttpServletResponse response, String msg)  {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        try (ServletOutputStream os = response.getOutputStream()){
            os.write(msg.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
