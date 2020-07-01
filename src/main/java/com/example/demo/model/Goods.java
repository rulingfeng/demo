package com.example.demo.model;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.Data;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: RU
 * @Date: 2020/4/9 13:42
 */
@Data
public class Goods {
    private Integer id;
    private String name;
    private Integer totalNum;
    private Integer saleNum;



}
