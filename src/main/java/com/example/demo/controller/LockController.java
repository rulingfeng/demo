package com.example.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Author: RU
 * @Date: 2020/4/11 9:49
 */
@RestController
@RequestMapping("/lock")
@Slf4j
public class LockController {

    @Autowired
    private RedisTemplate redisTemplate;
 /**   介绍完要使用的命令后，具体的使用步骤如下：

    setnx(lockkey, 当前时间+过期超时时间) ，如果返回1，则获取锁成功；如果返回0则没有获取到锁，转向2。

    get(lockkey)获取值oldExpireTime ，并将这个value值与当前的系统时间进行比较，如果小于当前系统时间，则认为这个锁已经超时，可以允许别的请求重新获取，转向3。

    计算newExpireTime=当前时间+过期超时时间，然后getset(lockkey, newExpireTime) 会返回当前lockkey的值currentExpireTime。

    判断currentExpireTime与oldExpireTime 是否相等，如果相等，说明当前getset设置成功，获取到了锁。如果不相等，说明这个锁又被别的请求获取走了，那么当前请求可以直接返回失败，或者继续重试。

    在获取到锁之后，当前线程可以开始自己的业务处理，当处理完毕后，比较自己的处理时间和对于锁设置的超时时间，如果小于锁设置的超时时间，则直接执行delete释放锁；如果大于锁设置的超时时间，则不需要再锁进行处理。
*/
    private Integer count = 0;
    @GetMapping("/lockDemo")
    public String lock()throws Exception{
        long timeOut = 1000L;
        String value = String.valueOf(timeOut+ System.currentTimeMillis());
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent("lockkey", value);
        long beginTime = System.currentTimeMillis();
        if(!isLock){  //没有获取到锁
            Object sourceLockkey = redisTemplate.opsForValue().get("lockkey");
            long oldTime = Long.valueOf(sourceLockkey.toString());
            if(oldTime < System.currentTimeMillis()){  //原锁已经超时
                String value1 = String.valueOf(timeOut+ System.currentTimeMillis());
                Object newLockkey = redisTemplate.opsForValue().getAndSet("lockkey", value1);
                long newTime = Long.valueOf(newLockkey.toString());
                if(!(oldTime == newTime)){
                    throw new Exception("被别人抢走了锁");
                }else{
                    count++;
                    System.out.println("得到了锁");
                    beginTime = System.currentTimeMillis();
                }
            }
            throw new Exception("锁被他人占有中!");
        }else{
            count++;
            System.out.println("获取锁成功");
        }
        System.out.println(count);
        //业务逻辑  已获取到锁
        log.info("抢到了锁+++++++++++++++执行业务逻辑++++++++++++");
        long executeTime = System.currentTimeMillis()-beginTime;
        if(executeTime<timeOut){
            //释放锁
            Boolean lockkey = redisTemplate.delete("lockkey");
            if(lockkey){
                System.out.println("成功释放锁");
            }
        }
        return "成功";
    }

    @GetMapping("/demo")
    public String demo(){

        Jedis jedis = new Jedis();

        redisTemplate.opsForValue().set("aa","aa");

        Object andSet = redisTemplate.opsForValue().getAndSet("aa", "bb");
        System.out.println(redisTemplate.opsForValue().get("aa"));
        return andSet.toString();
    }

    //解析xml
    public static void main(String[] args) {
        String xmlString = "<xml>"
                + "<appid><![CDATA[wx2421b1c4370ec43b]]></appid>"
                + "<attach><![CDATA[支付测试]]></attach>"
                + "<bank_type><![CDATA[CFT]]></bank_type>"
                + "<fee_type><![CDATA[CNY]]></fee_type>"
                + "<total_fee>1</total_fee>"
                + "</xml>";
        //去掉前后的xml标签
        xmlString = xmlString.replaceAll("</?xml>", "");
        System.out.println(xmlString);
        //匹配一段一段这样的数据   <attach><![CDATA[支付测试]]></attach>
        Pattern pattern = Pattern.compile("<.*?/.*?>");
        Matcher matcher = pattern.matcher(xmlString);
        //配置是否包含<![CDATA[CNY]]> CDATA 包裹的数据
        Pattern pattern2 = Pattern.compile("!.*]");
        Map<String, String> map = new HashMap<>();
        while(matcher.find()) {
            //获取键
            String key = matcher.group().replaceAll(".*/", "");
            key = key.substring(0, key.length() - 1);
            Matcher matcher2 = pattern2.matcher(matcher.group());
            String value = matcher.group().replaceAll("</?.*?>", "");
            //获取值
            if(matcher2.find() && !value.equals("DATA")) {
                value = matcher2.group().replaceAll("!.*\\[", "");
                value = value.substring(0, value.length() - 2);
            }
            map.put(key, value);
        }
        System.out.println(map);
        System.out.println(map.get("total_fee"));
        Integer total_fee =Integer.valueOf( map.get("total_fee"));
        System.out.println(total_fee);
    }
}
