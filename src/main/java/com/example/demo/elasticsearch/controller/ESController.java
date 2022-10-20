package com.example.demo.elasticsearch.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        Integer pageNum=1;
        Integer pageSize=120;
        SearchRequest request = new SearchRequest();

        //指定查询索引
        request.indices("goods");

        // 条件构造
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 查询条件，用 QueryBuilders 工具类
        // termQuery 精确查询
        // 有中文需要在字段后加上 .keyword，如下 name.keyword
//        TermQueryBuilder termQuery = QueryBuilders.termQuery("title.keyword", "鲜奶20");
        // 或者使用 matchQuery 精确匹配
        // MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("title", "包装鲜奶");

        // sourceBuilder.query(matchQuery);
        //limit 0,10
        sourceBuilder.from((pageNum-1)*pageSize);
        sourceBuilder.size(pageSize);
        // 高亮、分页等等也在这写
        // 高亮构造，但是后面还需解析高亮


        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //设置高亮字段
        highlightBuilder.field("title");
//        highlightBuilder.field("brand");
        //如果要多个字段高亮,这项要为false
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");

        //下面这两项,如果你要高亮如文字内容等有很多字的字段,必须配置,不然会导致高亮不全,文章内容缺失等
        highlightBuilder.fragmentSize(800000); //最大高亮分片数
        highlightBuilder.numOfFragments(0); //从第一个分片获取高亮片段
        sourceBuilder.highlighter(highlightBuilder);



        //根据自定义字段排序
        //sourceBuilder.sort("id", SortOrder.DESC); //等同于 order by id desc
        //根据分数排序
        sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));

        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //过滤字段
        String[] includes = {};  //等同于 select name from index
        String[] excludes = {"price"};  //要排除的字段
        sourceBuilder.fetchSource(includes,excludes);

//        QueryBuilders.boolQuery()常用函数:
//        1. 多个must(QueryBuilder)          等同于  ( QueryBuilder1 ) and ( QueryBuilder2 ) 多个must 取 ∩ 交集
//        2. 多个should(QueryBuilder)        等同于  ( QueryBuilder1 ) or  ( QueryBuilder2 )  多个should 取 ∪ 并集
//        3. 单个must/should                 等同于  where xx = xx，单个must/should传入的QueryBuilder，符合这个条件



        //相当于select * from table where title like '%鲜奶%'  or brand = "inm16" or brand = "inm15"
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //must -> SQL ‘=’ ‘and’
        boolQueryBuilder.must(QueryBuilders.matchQuery("title", "浙江"));


        //时间比较时间戳
//        boolQueryBuilder.filter(QueryBuilders.rangeQuery("startTime").gte(1666245260450L));
//        boolQueryBuilder.filter(QueryBuilders.rangeQuery("endTime").lte(1666245268348L));

        // boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(5.1));

        //should -> SQL ‘or’
        boolQueryBuilder.should(QueryBuilders.matchQuery("brand", "inm16"));
        boolQueryBuilder.should(QueryBuilders.matchQuery("brand", "inm15"));


        sourceBuilder.query(boolQueryBuilder);



        // 把资源放入 request
        request.source(sourceBuilder);


//        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));


        SearchResponse response = highLevelClient.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        System.out.println(JSON.toJSONString(hits));
        System.out.println("查询总数count:" + hits.getTotalHits().value);

        System.out.println(JSON.toJSONString(response));


        for (SearchHit hit : hits.getHits()) {
            //json格式的数据
            String sourceAsString = hit.getSourceAsString();
//            System.out.println(sourceAsString+hit.getScore());

            //map格式的数据
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //解析高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField field= highlightFields.get("title");
            if(field!= null){
                Text[] fragments = field.fragments();
                String n_field = "";
                for (Text fragment : fragments) {
                    n_field += fragment;
                }
                //高亮标题覆盖原标题
                sourceAsMap.put("title",n_field);
            }
            EsGoods esGoods = JSONObject.parseObject(JSONObject.toJSONString(hit.getSourceAsMap()), EsGoods.class);
            System.out.println(JSONObject.toJSONString(esGoods)+hit.getScore());

        }
    }


}
