package com.example.demo.elasticsearch.bo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Document(indexName = "goods",shards = 3,replicas = 1)
@Data
public class EsGoods {
    @Id
    private Long id;
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String title; //标题
    @Field(type = FieldType.Keyword)
    private String category;// 分类
    @Field(type = FieldType.Keyword)
    private String brand; // 品牌
    @Field(type = FieldType.Double)
    private Double price; // 价格
    @Field(index = false, type = FieldType.Keyword)
    private String images; // 图片地址
    @Field(type = FieldType.Date)
    private Date startTime;
    @Field(type = FieldType.Date)
    private Date endTime;

}