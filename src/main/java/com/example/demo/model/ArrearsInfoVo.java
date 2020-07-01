package com.example.demo.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;


/**
 * 某个出场id下的欠费信息
 */
@Data
@Accessors(chain = true)
public class ArrearsInfoVo {

    private Integer a;
    private Integer b;
    private Integer c;

}
