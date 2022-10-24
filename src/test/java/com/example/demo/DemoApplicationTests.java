package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.example.demo.dao.OrderMapper;
import com.example.demo.elasticsearch.bo.EsGoods;
import com.example.demo.model.Order;
import com.example.demo.service.OrderService;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.apache.lucene.search.similarities.Lambda;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.boon.core.Typ.list;

@SpringBootTest(classes = DemoApplication.class)
@RunWith(SpringRunner.class)
class DemoApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;
    private static final int size = 1000000;// 100万
    private static BloomFilter<Integer> bloomFilter =BloomFilter.create(Funnels.integerFunnel(), size,0.03);


    @Autowired
    private RestHighLevelClient highLevelClient;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderService orderService;

    @Test
    public void addDocumentTest() throws IOException, InterruptedException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date time = calendar.getTime();
        for (long i = 804806472l; i <804806572l ; i++) {
            Order order = new Order();
            order.setOrderNo(i);
            order.setTime(time);
            int insert = orderMapper.insert(order);
            System.out.println(insert);
        }

    }
    @Test
    void search(){
//        List<Order> list = orderService.lambdaQuery().eq(Order::getOrderNo, 1210525142804806472L).list();
        //全库表
        List<Order> list = orderService.lambdaQuery().list();
        System.out.println(list);
    }

    @Test
    void update(){
        boolean update = orderService.lambdaUpdate().set(Order::getUserId, 123L).eq(Order::getOrderNo, 1210525142804806472L).update();
        System.out.println(update);
    }

    @Test
    void detele(){
        LambdaQueryChainWrapper<Order> eq = orderService.lambdaQuery().eq(Order::getOrderNo, 1210525142804806472L);
        boolean update = orderService.remove(eq);
        System.out.println(update);
    }



    @Test
     void searchTest() throws IOException {
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



    @Test
    void excel() throws IOException, InterruptedException {


    }

}
