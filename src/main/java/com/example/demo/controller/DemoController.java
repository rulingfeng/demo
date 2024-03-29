package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.extension.api.R;
import com.example.demo.config.PaymentPropertiesConfig;
import com.example.demo.es.OrderDelayQueue;
import com.example.demo.es.OrderDelayedElement;
import com.example.demo.login.UserContextHolder;
import com.example.demo.model.SmsHomeBrand;
import com.example.demo.model.User;
import com.example.demo.model.WXTemplateMsgDto;
import com.example.demo.service.ITestService;
import com.example.demo.service.UserService;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

/**
 * @ProjectName: demo
 * @Package: com.example.demo.controller
 * @ClassName: DemoController
 * @Author: RU
 * @Description:
 * @Date: 2020/3/25 18:48
 * @Version: 1.0
 */
@RestController
@RequestMapping("/demo")
@Slf4j
public class DemoController {
//    @Autowired
//    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ITestService testService;
    @Resource
    private UserService userService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PaymentPropertiesConfig paymentPropertiesConfig;
    @Autowired
    private ApplicationContext applicationContext;
    @Resource
    private HttpServletRequest request;


    @GetMapping("/go22111")
    public String getLimit(){
        ArrayList<Long> objects = Lists.newArrayList();
        for (long i = 1; i <= 50; i++) {
            objects.add(i);
        }
        TreeMap<Long,Integer> map = new TreeMap<>(Comparator.reverseOrder());
        map.put(60L,18);
        map.put(1L,3);
        long a = 0l;
        for (Long shopId : objects) {
            boolean flag = true;
            List<Long> mapKeyList = Lists.newArrayList();
            for (Map.Entry<Long, Integer> longIntegerEntry : map.entrySet()) {
                boolean asf = asf(shopId,longIntegerEntry.getKey(),longIntegerEntry.getValue());
                if(!asf){
                    flag = false;
                    //需要删除之前的key
                    asfga(shopId,mapKeyList);
                    break;
                }
                mapKeyList.add(longIntegerEntry.getKey());
            }
            if(flag){
                System.out.println(shopId+"成功");
                a++;
            }else{
                asfga(shopId,map.keySet().stream().collect(Collectors.toList()));
            }




        }
        System.out.println(a);

        return "OK";

    }

    public void asfga(Long shopId,List<Long> mapKeyList){
        if(CollectionUtils.isEmpty(mapKeyList)){
            return;
        }
        for (Long aLong : mapKeyList) {
            redisTemplate.opsForZSet().remove("elemGetShop"+aLong, shopId);
        }

    }
    public boolean asf(Long shopId,Long windowSizeInSeconds,Integer limit){

        long currentTimeMillis = System.currentTimeMillis();
        String key = "elemGetShop"+windowSizeInSeconds;
        redisTemplate.opsForZSet().add(key,shopId,currentTimeMillis);
        Set<ZSetOperations.TypedTuple<Long>> elements = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
        for (ZSetOperations.TypedTuple<Long> element : elements) {
            long elementTimestamp = element.getScore().longValue();
            if (currentTimeMillis - elementTimestamp > windowSizeInSeconds * 1000) {
                redisTemplate.opsForZSet().remove(key, element.getValue());
            }
        }
        long size = redisTemplate.opsForZSet().size(key);
        if (size-1>=limit) {
            return false;
        }

        return true;
    }

    @GetMapping("/go22")
    public String go11(@RequestParam String name,@RequestParam String namer){

        testService.setContext(name,namer);

        userService.getContext();
        userService.getContext2();

        return "ok";

    }
    @GetMapping("/go")
    public Object go() throws ExecutionException, InterruptedException {
        String token = request.getHeader("token");
        System.out.println(token);
        HashMap<Object, Object> map = Maps.newHashMap();
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();

        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            map.put(1, token);
        });
        CompletableFuture<Void> token1 = CompletableFuture.runAsync(() -> {
            //异步执行，请求头会丢失
            //因为在这个线程中，也需要用到请求头的信息， 需要设置， 如果不设置会报错，
            // 包括feign调用（如果被调用方也需要用到请求头）
            RequestContextHolder.setRequestAttributes(requestAttributes);
            String token2 = request.getHeader("token")+"123";
            map.put(2, token2);
        });
        CompletableFuture.allOf(voidCompletableFuture,token1).get();

        return map;
    }


    @GetMapping("/nnn11")
    public String nnn11(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        System.out.println(requestAttributes.getRequest().getHeader("token"));


        CompletableFuture<Void> orderItemsTask = CompletableFuture.runAsync(() -> {
            // 赋值到新线程绑定的request
            RequestContextHolder.setRequestAttributes(requestAttributes);
            // TODO 1.异步查询购物车
            ServletRequestAttributes requestAttributes1 =(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            System.out.println(requestAttributes1.getRequest().getHeader("token"));
        });
        try {
            CompletableFuture.allOf(orderItemsTask).get();
        } catch (Exception e) {
            log.error("异步编排获取确认订单页面数据失败：{}", e);

        }
        return "l";
    }


    @GetMapping("/nnn")
    public String nnn(@RequestParam String name)throws Exception{
        OrderDelayQueue.getDeque().put(new OrderDelayedElement( 5 * 1000L , 1));//0代表在场缴费订单
        OrderDelayQueue.getDeque().put(new OrderDelayedElement( 5 * 1000L , 2));//0代表在场缴费订单
        OrderDelayQueue.getDeque().put(new OrderDelayedElement( 5 * 1000L , 3));//0代表在场缴费订单
        OrderDelayQueue.getDeque().remove(new OrderDelayedElement( 5 * 1000L , 2));
        System.out.println(paymentPropertiesConfig.getConfig());
        System.out.println(paymentPropertiesConfig.getAppId());
        System.out.println(paymentPropertiesConfig.getAppSecret());
        return name;
    }



    public static void main(String[] args) {
        boolean a  = true;
        Object obj = "B000123";
        if(!  ( Objects.equals(obj, "0")
                ||obj==null
                || Objects.equals(obj, ""))){

            System.out.println("if");
        }else {
            System.out.println("else");
        }

    }
    public static void grw(String cc,String bb,String... aaa){
        System.out.println(aaa.length);
        System.out.println(cc);
        System.out.println(bb);
        System.out.println(aaa[0]);
        System.out.println(aaa[1]);
        System.out.println(aaa[2]);

    }
    @GetMapping("/aaa")
    public Map<String, Object> aaa(){
        log.info("进入aaa方法--------------------------------");
//        IndexQuery indexQuery = new IndexQuery();
//
//        elasticsearchTemplate.index(indexQuery);
        Map<String,Object> map = new HashMap<>(2);
        map.put(null,null);
        map.put("null",null);
        map.put("asd",null);
        Object o = map.get(null);
        System.out.println(o);
        System.out.println(map.size());
        Map<String, Object> currentMap = Collections.synchronizedMap( new HashMap<>());
        Hashtable<Object, Object> objectObjectHashtable = new Hashtable<>();
        log.info("aaa方法执行完毕--------------------------------");
        currentMap.put("aaa","bbb");
        currentMap.put("ccc","ddd");
        currentMap.put("userId", UserContextHolder.getInstance().getUserId());
        return currentMap;
    }


    @GetMapping("/bbb")
    public String bbb(){
        Map<String,Object> map = new HashMap<>(17);
        map.put("name","testName");
        map.put("age",17);
        String s = JSONObject.toJSONString(map);
        amqpTemplate.convertAndSend("test.demo_aa",s);
        log.info("发送数据[{}]",s);
        return null;
    }
    @RequestMapping("/ccc")
    public String ccc(){
        System.out.println(1);
        return "111";

    }

    @GetMapping("/testJmeter")
    public String testJmeter()throws Exception{

        return null;
    }

    @GetMapping("/testRestTemplate")
    public String testRestTemplate()throws Exception{
        String url = "http://localhost:8080/test/restTemplate";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //构造表单参数
        MultiValueMap<String, Object> params= new LinkedMultiValueMap<>();
        params.add("id", 6);
        params.add("name", "rlf");
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
        return responseEntity.getBody();
    }


    public static String getBase64FromInputStream(InputStream in) {
        Assert.notNull(in,"asfwqr123");
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;
        // 读取图片字节数组
        ByteArrayOutputStream swapStream=null;
        try {
            swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = in.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            data = swapStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new String(swapStream.toByteArray());
        // return new String(Base64.getEncoder().encodeToString(data));
    }

}
