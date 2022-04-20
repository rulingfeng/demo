package com.example.demo.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.dao.TestMapper;
import com.example.demo.dao.UserMapper;
import com.example.demo.event.NoticeEventObj;
import com.example.demo.mapstruct.UserConverter;
import com.example.demo.model.SmsHomeBrand;
import com.example.demo.model.Stock;
import com.example.demo.model.User;
import com.example.demo.model.UserVo;
import com.example.demo.service.ITestService;
import com.github.pagehelper.Page;
//import io.seata.spring.annotation.GlobalTransactional;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.objects.annotations.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ProjectName: demo
 * @Package: com.example.demo.controller
 * @ClassName: TestController
 * @Author: RU
 * @Description:
 * @Date: 2020/3/19 17:34
 * @Version: 1.0
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController implements ApplicationEventPublisherAware {
    @Autowired
    private ITestService testService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TestMapper testMapper;
    @Resource
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    private ApplicationEventPublisher applicationEventPublisher;


    private static Integer count = 10;
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    //see BindingResultAspect
    @PostMapping("/testVaild")
    public void testVaild(@RequestBody @Valid User user, BindingResult result){
        System.out.println(user);
    }

    @GetMapping("/testMapstruct")
    public void testMapstruct(){
        User user = new User();
        user.setId(1);
        user.setUserName("你");
        user.setAge("18");
        user.setType("888");
        user.setBrithday("2020-08-21 10:10:10");
        UserVo userVo = UserConverter.INSTANCE.userToUserVo(user);
        System.out.println(JSONObject.toJSONString(userVo));
        //{"age1":"18","brithday":1597975810000,"id":1,"name":"改名","type":{"id":888}}
    }



    @GetMapping("/testRedisHash")
    public String testRedisHash(){

        redisTemplate.opsForValue().set("valueSet","你好");
        redisTemplate.opsForHash().put("testHash","name","ru");
        redisTemplate.opsForHash().put("testHash","age","18");
        redisTemplate.opsForHash().put("testHash","home","杭州");
        System.out.println(redisTemplate.opsForHash().get("testHash","name"));
        System.out.println(redisTemplate.opsForHash().get("testHash","age"));
        System.out.println(redisTemplate.opsForHash().get("testHash","home"));
        System.out.println(redisTemplate.opsForHash().get("testHash","homemem")); //null
        //redisTemplate.opsForHash().delete("testHash","name","age");
        return "ok";
    }
    @GetMapping("/testAssert")
    public String testAssert(){

        System.out.println(1);
        Integer a = null;
        org.springframework.util.Assert.notNull(a,"不能为空");
        System.out.println("finish");
        return "ok";
    }

    @GetMapping("/mapa")
    public void mapa(){
        List<User> list = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            User user = new User();
            user.setId(1+i);
            user.setUserName("你"+i);
            user.setAge("18"+i);
            user.setType("888"+i);
            user.setBrithday("2020-08-21 10:10:10");
            list.add(user);
        }
        long l = System.currentTimeMillis();
        List<UserVo> collect = list.parallelStream().map(i -> UserConverter.INSTANCE.userToUserVo(i)).collect(Collectors.toList());
        System.out.println(System.currentTimeMillis() - l);
        System.out.println(collect.size());
    }
    @GetMapping("/mapb")
    public void mapb(){
        List<User> list = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            User user = new User();
            user.setId(1+i);
            user.setUserName("你"+i);
            user.setAge("18"+i);
            user.setType("888"+i);
            user.setBrithday("2020-08-21 10:10:10");
            list.add(user);
            BeanUtils.copyProperties(user,UserVo.class);
        }
        long l = System.currentTimeMillis();
        List<UserVo> collect = Collections.synchronizedList(new ArrayList<>(10005));
        list.parallelStream().forEach(i->{
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(i,userVo);
            collect.add(userVo);
        });
        System.out.println(System.currentTimeMillis() - l);
        System.out.println(collect.size());
    }

    @GetMapping("/abserverd")
    public void abserver(){
        User user = new User();
        user.setId(111);
        user.setUserName("健康");
        applicationEventPublisher.publishEvent(new NoticeEventObj(user,"3"));
    }

    @GetMapping("/aaa")
    @ApiOperation("测试撒是")
    //@GlobalTransactional
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public User aaa(@RequestParam("id") Integer id,@RequestParam(value = "name",required = false) String name){
        User user = new User();
        user.setId(id);
        user.setUserName("健康");
        applicationEventPublisher.publishEvent(new NoticeEventObj(user,"3"));
        applicationEventPublisher.publishEvent(new NoticeEventObj(user,"2"));
//        String result = HttpRequest.get("localhost:8080/test/t")
//                .header(Header.ACCEPT_CHARSET, "UTF-8")//头信息，多个头信息多次调用此方法即可
//                .form("id",id).timeout(1000)//超时，毫秒
//                .execute().body();
//        int a = 1/0;
        return null;

    }



    @GetMapping("/ddd")
    public Boolean ddd(){
        Boolean aBoolean = setLock();
        return aBoolean;
    }

    @GetMapping("/asd")
    public Object asd(){
        redis.clients.jedis.Jedis jedis = new Jedis("192.168.1.188");
        byte[] sanbas = new JdkSerializationRedisSerializer().serialize("carIn_浙C");
        byte[] a = new JdkSerializationRedisSerializer().serialize("12");
        String sanba = jedis.set(sanbas, a);
        Object carIn_浙C = redisTemplate.opsForValue().get("carIn_浙C");
        System.out.println(redisTemplate.getKeySerializer());
        System.out.println(redisTemplate.getValueSerializer());
        System.out.println(carIn_浙C);


        return null;
    }

    private Boolean setLock() {
        String lockKey = "feng";
        log.info("lockKey : {}" , lockKey);
        // 使用sessionCallBack处理
//        SessionCallback<Boolean> sessionCallback = new SessionCallback<Boolean>() {
//            List<Object> exec = null;
//            @Override
//            @SuppressWarnings("unchecked")
//            public Boolean execute(RedisOperations operations) throws DataAccessException {
//                operations.multi();
//                stringRedisTemplate.opsForValue().setIfAbsent(lockKey,"888888888888888");
//                stringRedisTemplate.expire(lockKey,10000, TimeUnit.SECONDS);
//                exec = operations.exec();
//                if(exec.size() > 0) {
//                    exec.forEach(System.out::println);
//                    return (Boolean) exec.get(0);
//                }
//                return false;
//            }
//        };
//        return (boolean)stringRedisTemplate.execute(sessionCallback);
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(lockKey, "888888888888888");
        System.out.println(aBoolean);
        if (aBoolean) {
            Boolean expire = redisTemplate.expire(lockKey, 10000, TimeUnit.SECONDS);
            System.out.println(expire);
        }

        return null;
    }
    // 请求的数量
    private static final int threadCount = 100000;
    // 需要同步的线程数量
    private static final CyclicBarrier cyclicBarrier = new CyclicBarrier(10);
    private static AtomicInteger  atomicInteger = new AtomicInteger();
    static  CountDownLatch countDownLatch = new CountDownLatch(100000);
    public ThreadFactory threadFactory = new ThreadFactoryBuilder().build();
    public static void main(String[] args) throws InterruptedException {
//        redis.clients.jedis.Jedis jedis = new Jedis("192.168.1.188");
//        jedis.set("carIn_feng","10");
//        String carIn_feng = jedis.get("carIn_feng");
//        System.out.println(carIn_feng);
//        jedis.watch("aaa");
//        if("ddd".equals(jedis.get("aaa"))){
//            Transaction transaction = jedis.multi();
//            transaction.del("aaa");
//            List<Object> list = transaction.exec();
//            if(list ==null){
//                System.out.println("list==null");
//            }
//            jedis.unwatch();
//        }
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        long currentTimeMillis = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            final int threadNum = i;
            //Thread.sleep(1000);
            threadPool.execute(() -> {
                try {

                    test(threadNum);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
        }
        threadPool.shutdown();
        countDownLatch.await();
        long end = System.currentTimeMillis();
        System.out.println(end-currentTimeMillis);
    }
    public static void test(int threadnum) throws InterruptedException, BrokenBarrierException {

        System.out.println("threadnum:" + threadnum + "is ready");
        try {
            cyclicBarrier.await();
        } catch (Exception e) {
            System.out.println(threadnum+"-----CyclicBarrierException------");
        }
        String a = "1";
        for(int i = 0;i<100;i++){
            a+= i+"";
        }
        //System.out.println(threadnum+"号:"+atomicInteger.incrementAndGet());
        System.out.println("threadnum:" + threadnum + "is finish");
        countDownLatch.countDown();
    }

    public static void sgewq(){
        redis.clients.jedis.Jedis jedis = new Jedis("localhost");
        String lua =
                "local key = KEYS[1] " +
                        " local limit = tonumber(ARGV[1]) " +
                        " local current = tonumber(redis.call('get', key) or '0')" +
                        " if current + 1 > limit " +
                        " then  return 0 " +
                        " else "+
                        " redis.call('INCRBY', key,'1')" +
                        " redis.call('expire', key,'2') " +
                        " end return 1 ";

        String key = "ip:" + System.currentTimeMillis()/1000; // 当前秒
        String limit = "3"; // 最大限制
        List<String> keys = new ArrayList<String>();
        keys.add(key);
        List<String> arg = new ArrayList<String>();
        arg.add(limit);
        String luaScript = jedis.scriptLoad(lua);
        Long result = (Long)jedis.evalsha(luaScript, keys, arg);
        boolean b = result == 1;
        System.out.println(b+"---------"+result);
    }

    @GetMapping("/bbb")
    public String bbb()throws Exception{
        //PageInfo<SmsHomeBrand> smsHomeBrandPageInfo = testService.selectAll();
        Page<SmsHomeBrand> smsHomeBrandPageInfo = testService.select();
        smsHomeBrandPageInfo.getResult().forEach(System.out::println);
        System.out.println("----------------------------------");
        Page<Stock> stocks = testService.selectStock();
        stocks.getResult().forEach(System.out::println);
        return smsHomeBrandPageInfo.toString();
        //CommonPage<SmsHomeBrand> page = CommonPage.restPage(pageInfo);
        //page.getList().forEach(System.out::println);
    }
    @GetMapping("/bbbaaa")
    public String bbbaaa(){
        //PageInfo<SmsHomeBrand> smsHomeBrandPageInfo = testService.selectAll();
        Page<SmsHomeBrand> smsHomeBrandPageInfo = testService.select(5);
        smsHomeBrandPageInfo.getResult().forEach(System.out::println);
        return smsHomeBrandPageInfo.toString();
        //CommonPage<SmsHomeBrand> page = CommonPage.restPage(pageInfo);
        //page.getList().forEach(System.out::println);
    }







}
