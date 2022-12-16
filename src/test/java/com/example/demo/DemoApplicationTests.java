package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.controller.RedisListQueueController;
import com.example.demo.elasticsearch.bo.EsGoods;
import com.example.demo.elasticsearch.bo.GoodsSku;
import com.example.demo.redis.bitmap.RedisBitMapDemo;
import com.example.demo.redis.bloomFilter.redisson.RedissonBlommFilterDemo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
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
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
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
import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SpringBootTest(classes = DemoApplication.class)
@RunWith(SpringRunner.class)
class DemoApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;
    private static final int size = 1000000;// 100万
    private static BloomFilter<Integer> bloomFilter =BloomFilter.create(Funnels.integerFunnel(), size,0.03);
    @Autowired
    private RedisBitMapDemo redisBitMapDemo;
    @Autowired
    RedisListQueueController redisListQueueController;
    @Resource
    private RedissonBlommFilterDemo redissonBlommFilterDemo;

    @Autowired
    private RestHighLevelClient highLevelClient;


    /**
     * 搜索查询
     */

    @Test
    public void searchTestaggregation() throws IOException {
        Integer pageNum=1;
        Integer pageSize=120;
        SearchRequest request = new SearchRequest();

        //指定查询索引
        request.indices("goods_sku");

        // 条件构造
        SearchSourceBuilder builder = new SearchSourceBuilder();

        builder.from((pageNum-1)*pageSize);
        builder.size(pageSize);

//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        //留下匹配上的，没有匹配上的则查询不出来
//        boolQueryBuilder.filter(QueryBuilders.termQuery("catelogId", 99L));
//        builder.query(boolQueryBuilder);

        // 6.聚合分析，分析得到的商品所涉及到的分类、品牌、规格参数，
        // term值的是分布情况，就是存在哪些值，每种值下有几个数据; size是取所有结果的前几种，(按id聚合后肯定是同一种，所以可以指定为1)
        // 6.1 分类部分，按照分类id聚合，划分出分类后，每个分类内按照分类名字聚合就得到分类名，不用再根据id再去查询数据库
        TermsAggregationBuilder catelogAgg = AggregationBuilders.terms("catelogAgg").field("catelogId");
        catelogAgg.subAggregation(AggregationBuilders.terms("catelogNameAgg").field("catelogName").size(1));
        builder.aggregation(catelogAgg);
        // 6.2 分类部分，按照品牌id聚合，划分出品牌后，每个品牌内按照品牌名字聚合就得到品牌名，不用再根据id再去查询数据库
        // 每个品牌内按照品牌logo聚合就得到品牌logo，不用再根据id再去查询数据库
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brandAgg").field("brandId");
        brandAgg.subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName").size(1));
        brandAgg.subAggregation(AggregationBuilders.terms("brandImgAgg").field("brandImg").size(1));
        builder.aggregation(brandAgg);
        // 6.3 规格参数部分，按照规格参数id聚合，划分出规格参数后，每个品牌内按照规格参数名字聚合就得到规格参数名，不用再根据id再去查询数据库
        // 每个规格参数内按照规格参数值聚合就得到规格参数值，不用再根据id再去查询数据库
        NestedAggregationBuilder nestedAggregationBuilder = AggregationBuilders.nested("attrAgg", "attrs");
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId");
        //如果一个id只对应一个name，那么后面加size(1)就可以，在取的时候也是通过get(0)去取
        //如果一个id对应多个name，那么后面不用加size()方法，在取的时候通过获取一个list得到
        //参考这里的attrName和attrValue，  attrName对应id只有一个，attrValue对应id有多个
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName").size(1));
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue"));
        nestedAggregationBuilder.subAggregation(attrIdAgg);
        builder.aggregation(nestedAggregationBuilder);

        System.out.println("搜索参数构建的DSL语句：" + builder);

        // 把资源放入 request
        request.source(builder);


        SearchResponse response = highLevelClient.search(request, RequestOptions.DEFAULT);


        SearchHits hits = response.getHits();

        System.out.println("查询总数count:" + hits.getTotalHits().value);

        for (SearchHit hit : hits.getHits()) {
            //json格式的数据
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
            //map格式的数据
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

        }

        /**
         * 聚合结果--分类
         */
        Aggregations aggregations = response.getAggregations();
        // debug模式下确定这个返回的具体类型
        ParsedLongTerms catelogAggAgg = aggregations.get("catelogAgg");
        // 每一个bucket是一种分类，有几个bucket就会有几个分类
        List<HashMap<Object, Object>> catelogs = catelogAggAgg.getBuckets().stream().map(bucket -> {
            // debug查看下结果
            long catelogId = bucket.getKeyAsNumber().longValue();
            // debug模式下确定这个返回的具体类型
            ParsedStringTerms catelogNameAgg = bucket.getAggregations().get("catelogNameAgg");
            // 根据id分类后肯定是同一类，只可能有一种名字，所以直接取第一个bucket
            String catelogName = catelogNameAgg.getBuckets().get(0).getKeyAsString();
            HashMap<Object, Object> map = Maps.newHashMap();
            map.put("catelogId",catelogId);
            map.put("catelogName",catelogName);
            return map;
        }).collect(Collectors.toList());
        //打印:-> 分类:[{catelogName=熟食, catelogId=99}, {catelogName=奶类, catelogId=100}, {catelogName=休闲食品, catelogId=55}]
        System.out.println("分类:"+catelogs);

        /**
         * 聚合结果--品牌，与上面过程类似
         */
        ParsedLongTerms brandAggAgg = aggregations.get("brandAgg");
        List< HashMap<Object, Object>> brands = brandAggAgg.getBuckets().stream().map(bucket -> {
            long brandId = bucket.getKeyAsNumber().longValue();
            ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brandNameAgg");
            String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
            ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brandImgAgg");
            String brandImg = brandImgAgg.getBuckets().get(0).getKeyAsString();
            HashMap<Object, Object> map = Maps.newHashMap();
            map.put("BrandId",brandId);
            map.put("BrandName",brandName);
            map.put("BrandImg",brandImg);
            return map;
        }).collect(Collectors.toList());
        //打印-》品牌[{BrandName=烘培, BrandImg=http://hongpei.com, BrandId=66}, {BrandName=牛奶, BrandImg=http://niunai.com, BrandId=67}, {BrandName=零食, BrandImg=http://lingshi.com, BrandId=70}]
        System.out.println("品牌"+brands);


        /**
         * 聚合结果--规格参数
         */
        ParsedNested attrAgg = aggregations.get("attrAgg");
        ParsedLongTerms attrIdAggAgg = attrAgg.getAggregations().get("attrIdAgg");
        List< HashMap<Object, Object>> attrs = attrIdAggAgg.getBuckets().stream().map(bucket -> {
            long attrId = bucket.getKeyAsNumber().longValue();
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attrNameAgg");
            // 根据id分类后肯定是同一类，只可能有一种名字，所以直接取第一个bucket
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();

            // 根据id分类后肯定是同一类，但是可以有多个值，所以会有多个bucket，把所有值组合起来
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attrValueAgg");
            List<String> attrValue = attrValueAgg.getBuckets().stream().map(b -> b.getKeyAsString()).collect(Collectors.toList());

            HashMap<Object, Object> map = Maps.newHashMap();
            map.put("AttrId",attrId);
            map.put("AttrName",attrName);
            map.put("AttrValue",attrValue);
            return map;
        }).collect(Collectors.toList());
        //打印-》规格参数[{AttrName=冷的, AttrId=46, AttrValue=[冷的, 常温的]}, {AttrName=热的, AttrId=45, AttrValue=[很热]}, {AttrName=冰冻, AttrId=47, AttrValue=[冰冻的]}, {AttrName=大的, AttrId=79, AttrValue=[大包装]}, {AttrName=小的, AttrId=80, AttrValue=[小包装]}]
        System.out.println("规格参数"+ attrs);


    }


    /**
     * 批量插入
     */

    @Test
    public void bulkTest() throws IOException {
        BulkRequest request = new BulkRequest();

        GoodsSku sku1 = GoodsSku.builder().spuId(11L).skuId(2L).skuTitle("面包").skuPrice(new BigDecimal("5.5"))
                .skuImg("http://baidu.com").saleCount(5L).hasStock(true).hotScore(8L)
                .brandId(66L).brandName("烘培").brandImg("http://hongpei.com").catelogId(99L).catelogName("熟食")
                .attrs(Lists.newArrayList(
                        GoodsSku.Attrs.builder().attrId(45L).attrName("热的").attrValue("很热").build(),
                        GoodsSku.Attrs.builder().attrId(46L).attrName("冷的").attrValue("冷的").build()
                )).build();

        GoodsSku sku2 = GoodsSku.builder().spuId(12L).skuId(3L).skuTitle("酸奶").skuPrice(new BigDecimal("6.5"))
                .skuImg("http://suannai.com").saleCount(4L).hasStock(true).hotScore(9L)
                .brandId(67L).brandName("牛奶").brandImg("http://niunai.com").catelogId(100L).catelogName("奶类")
                .attrs(Lists.newArrayList(
                        GoodsSku.Attrs.builder().attrId(46L).attrName("常温").attrValue("常温的").build(),
                        GoodsSku.Attrs.builder().attrId(47L).attrName("冰冻").attrValue("冰冻的").build()
                )).build();

        GoodsSku sku3 = GoodsSku.builder().spuId(13L).skuId(4L).skuTitle("面包吐司").skuPrice(new BigDecimal("9"))
                .skuImg("http://baidu.com").saleCount(5L).hasStock(true).hotScore(8L)
                .brandId(66L).brandName("烘培").brandImg("http://hongpei.com").catelogId(99L).catelogName("熟食")
                .attrs(Lists.newArrayList(
                        GoodsSku.Attrs.builder().attrId(45L).attrName("热的").attrValue("很热").build(),
                        GoodsSku.Attrs.builder().attrId(46L).attrName("冷的").attrValue("冷的").build()
                )).build();

        GoodsSku sku4 = GoodsSku.builder().spuId(14L).skuId(5L).skuTitle("烤冷酸奶").skuPrice(new BigDecimal("11"))
                .skuImg("http://baidu.com").saleCount(5L).hasStock(true).hotScore(8L)
                .brandId(67L).brandName("牛奶").brandImg("http://niunai.com").catelogId(100L).catelogName("奶类")
                .attrs(Lists.newArrayList(
                        GoodsSku.Attrs.builder().attrId(46L).attrName("常温").attrValue("常温的").build(),
                        GoodsSku.Attrs.builder().attrId(47L).attrName("冰冻").attrValue("冰冻的").build()
                )).build();

        GoodsSku sku5 = GoodsSku.builder().spuId(15L).skuId(6L).skuTitle("果冻").skuPrice(new BigDecimal("2"))
                .skuImg("http://guodong.com").saleCount(1L).hasStock(true).hotScore(88L)
                .brandId(70L).brandName("零食").brandImg("http://lingshi.com").catelogId(55L).catelogName("休闲食品")
                .attrs(Lists.newArrayList(
                        GoodsSku.Attrs.builder().attrId(79L).attrName("大的").attrValue("大包装").build(),
                        GoodsSku.Attrs.builder().attrId(80L).attrName("小的").attrValue("小包装").build()
                )).build();

        List<GoodsSku> list = Lists.newArrayList(sku1,sku2,sku3,sku4,sku5);

        // 不设置id就会随机生成
        // 批处理请求
        for (GoodsSku goodsSku : list) {
            // 批量更新、删除也在这里操作
            request.add(new IndexRequest("goods_sku")
                    .id(goodsSku.getSkuId().toString())
                    .source(JSON.toJSONString(goodsSku), XContentType.JSON));
        }
        BulkResponse bulk = highLevelClient.bulk(request, RequestOptions.DEFAULT);
        System.out.println(bulk.status());
    }


@Test
    public void addDocumentTest() throws IOException, InterruptedException {

            EsGoods esGoods = new EsGoods();
            esGoods.setId(14L);
            esGoods.setBrand("inm"+14);
            esGoods.setCategory("nai");
            esGoods.setImages("hhtp");
            esGoods.setPrice(5.8);
            esGoods.setTitle("我来自浙江"+14);
            esGoods.setStartTime(new Date());
            esGoods.setEndTime(new Date());
            // 拿到索引
            IndexRequest request = new IndexRequest("goods");
            // 设置文档id
            request.id(String.valueOf(14));

            // 将User对象转化为JSON，数据放入请求
            request.source(JSON.toJSONString(esGoods), XContentType.JSON);
            // 客户端发送请求后获取响应
            IndexResponse index = highLevelClient.index(request, RequestOptions.DEFAULT);

            System.out.println(index.toString());
            // 索引状态
            System.out.println(index.status());



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
    public void in(){
        String add = redisListQueueController.add();
        System.out.println(add
        );
    }

    @Test
    void excel() throws IOException, InterruptedException {


    }

}
