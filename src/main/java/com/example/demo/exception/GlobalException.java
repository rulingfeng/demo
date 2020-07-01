package com.example.demo.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常捕捉
 *
 * @author EDZ
 * @version 1.0
 * @since 2019/12/9 13:42
 */
@Slf4j
@ControllerAdvice
public class GlobalException {

    /**
     * 处理异常
     *
     * @param ex 异常信息
     * @return Result
     */
    @ResponseBody
    @ExceptionHandler(value = Throwable.class)
    public String exceptionHandler(HttpServletRequest request, Throwable ex) {
        log.error("全局异常捕获{}",ex);
        return ex.getMessage();
    }
}
