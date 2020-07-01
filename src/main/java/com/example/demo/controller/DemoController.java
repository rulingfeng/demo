package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.config.PaymentPropertiesConfig;
import com.example.demo.es.OrderDelayQueue;
import com.example.demo.es.OrderDelayedElement;
import com.example.demo.model.SmsHomeBrand;
import com.example.demo.model.User;
import com.example.demo.model.WXTemplateMsgDto;
import com.example.demo.service.ITestService;
import com.github.pagehelper.Page;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ITestService testService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PaymentPropertiesConfig paymentPropertiesConfig;
    @Autowired
    private ApplicationContext applicationContext;

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
