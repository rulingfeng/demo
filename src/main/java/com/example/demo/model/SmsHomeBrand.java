package com.example.demo.model;

import lombok.Data;

/**
 * @ProjectName: demo
 * @Package: com.example.demo.model
 * @ClassName: SmsHomeBrand
 * @Author: RU
 * @Description:
 * @Date: 2020/3/26 10:25
 * @Version: 1.0
 */
@Data
public class SmsHomeBrand {
    private Integer id;
    private Integer brandId;
    private String brandName;
    private Integer recommendStatus;
    private Integer sort;
}
