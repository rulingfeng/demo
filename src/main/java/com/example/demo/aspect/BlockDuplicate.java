package com.example.demo.aspect;

import java.lang.annotation.*;

/**
 * @Description:
 * @Author: RU
 * @Date: 2020/6/6 13:27
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BlockDuplicate {

}
