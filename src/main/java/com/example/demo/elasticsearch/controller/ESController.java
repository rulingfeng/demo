package com.example.demo.elasticsearch.controller;

import com.alibaba.fastjson.JSON;
import com.example.demo.elasticsearch.bo.EsGoods;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rulingfeng
 * @time 2022/10/19 13:47
 * @desc
 */
@RestController
public class ESController {
    @Autowired
    private RestHighLevelClient highLevelClient;

    
    public void addDocumentTest() throws IOException {
        for (int i = 1; i < 11; i++) {
            EsGoods esGoods = new EsGoods();
            esGoods.setId((long)i);
            esGoods.setBrand("inm"+i);
            esGoods.setCategory("nai");
            esGoods.setImages("hhtp");
            esGoods.setPrice(5.6);
            esGoods.setTitle("鲜奶"+i);
            // 拿到索引
            IndexRequest request = new IndexRequest("goods");
            // 设置文档id
            request.id(String.valueOf(i));

            // 将User对象转化为JSON，数据放入请求
            request.source(JSON.toJSONString(esGoods), XContentType.JSON);
            // 客户端发送请求后获取响应
            IndexResponse index = highLevelClient.index(request, RequestOptions.DEFAULT);

            System.out.println(index.toString());
            // 索引状态
            System.out.println(index.status());
        }

    }

    /**
     * 批量插入
     */
    
    public void bulkTest() throws IOException {
        BulkRequest request = new BulkRequest();

        List<EsGoods> list = new ArrayList<>();
        for (int i = 11; i < 21; i++) {
            EsGoods esGoods = new EsGoods();
            esGoods.setId((long)i);
            esGoods.setBrand("inm"+i);
            esGoods.setCategory("nai");
            esGoods.setImages("hhtp");
            esGoods.setPrice(5.6);
            esGoods.setTitle("鲜奶"+i);
            list.add(esGoods);
        }


        // 不设置id就会随机生成
        // 批处理请求
        for (EsGoods esGoods : list) {
            // 批量更新、删除也在这里操作
            request.add(new IndexRequest("goods")
                    .id(esGoods.getId().toString())
                    .source(JSON.toJSONString(esGoods), XContentType.JSON));
        }
        BulkResponse bulk = highLevelClient.bulk(request, RequestOptions.DEFAULT);
        System.out.println(bulk.status());
    }


    /**
     * 获取文档，判断是否存在
     */
    
    public void existDocument() throws IOException {
        GetRequest request = new GetRequest("goods","1");
        boolean exists = highLevelClient.exists(request,RequestOptions.DEFAULT);
        System.out.println(exists);

        request = new GetRequest("goods","11");
        exists = highLevelClient.exists(request,RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    /**
     * 获取文档信息
     */
    
    public void getDocInfo() throws IOException {
        GetRequest request = new GetRequest("goods", "15");
        GetResponse response = highLevelClient.get(request, RequestOptions.DEFAULT);
        // 从响应中获取各种信息
        System.out.println(response.getSourceAsString());
        System.out.println(response.getVersion());
        System.out.println(response);
    }

    /**
     * 更新文档信息
     */
    
    public void updateDocument() throws IOException {
        UpdateRequest request = new UpdateRequest("goods", "1");

        EsGoods esGoods = new EsGoods();
        esGoods.setId((long)1);
        esGoods.setBrand("inm"+100);
        esGoods.setCategory("nai");
        esGoods.setImages("hhtp");
        esGoods.setPrice(5.6100);
        esGoods.setTitle("鲜奶"+100);
        request.doc(JSON.toJSONString(esGoods), XContentType.JSON);

        UpdateResponse response = highLevelClient.update(request, RequestOptions.DEFAULT);
        System.out.println(response.status());
        System.out.println(response.toString());
    }

    /**
     * 删除文档
     */
    
    public void deleteDocument() throws IOException {
        DeleteRequest request = new DeleteRequest("goods", "1");
        DeleteResponse response = highLevelClient.delete(request, RequestOptions.DEFAULT);
        System.out.println(response.status());
    }


    /**
     * 搜索查询
     */
    
    public void searchTest() throws IOException {
        SearchRequest request = new SearchRequest();

        // 条件构造
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 查询条件，用 QueryBuilders 工具类
        // termQuery 精确查询
        // 有中文需要在字段后加上 .keyword，如下 name.keyword
//        TermQueryBuilder termQuery = QueryBuilders.termQuery("title.keyword", "鲜奶20");
        // 或者使用 matchQuery 精确匹配
        MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("title", "包装鲜奶");

        sourceBuilder.query(matchQuery);
        //类似于mysql limit 0,10  从第1条开始取10条
        sourceBuilder.from(0);
        sourceBuilder.size(10);
        // 高亮、分页等等也在这写
        // 高亮构造，但是后面还需解析高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("奶");
        sourceBuilder.highlighter(highlightBuilder);

        // 把资源放入 request
        request.source(sourceBuilder);

        SearchResponse response = highLevelClient.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        System.out.println(JSON.toJSONString(hits));
        System.out.println(JSON.toJSONString(response));
        for (SearchHit hit : hits.getHits()) {
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
        }
    }


}
