package com.example.demo.dynamicUrl;

import lombok.Data;

/**
 * @author RU
 * @date 2020/8/29
 * @Desc
 */
@Data
public class Exposer {

    private String md5;

    public Exposer(String md5){
        this.md5 = md5;
    }
}
