package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.aspect.OrderRateLimit;
import com.example.demo.model.*;
import com.example.demo.service.ITestService;
import com.example.demo.service.UserService;

import com.example.demo.service.impl.RedisToDbOneImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationContext;

import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;


/**
 * @Description:
 * @Author: RU
 * @Date: 2020/5/6 16:51
 */
@RestController
@RequestMapping("/maySix")
@Slf4j
public class MaySixController {
    @Autowired
    private ITestService iTestService;
    @Autowired
    private RedisToDbOneImpl redisToDbOne;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    UserService userService;

    private static final ThreadLocal<SimpleDateFormat> local =
            ThreadLocal.withInitial( () -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS+0800"));


    public static LongAdder aaa = new LongAdder();

    @GetMapping("/add")
    public  String add() {
        User byId = userService.getById(2);
        if(byId.getAge().startsWith("\"")){
            String a = byId.getAge().substring(1,byId.getAge().length()-1);
            String s = a.replaceAll("\\\\", "");
            return s;
        }
        return "";
    }
    @GetMapping("/del")
    public  String del() {
       iTestService.delete(15);
        return "";
    }

    @GetMapping("/update")
    public  String update() {
        iTestService.update(15);
        return "";
    }
    @GetMapping("/get")
    public  String get() {
        Object jetCache15 = redisTemplate.opsForValue().get("jetCache15");
        return jetCache15.toString();
    }
    @GetMapping("/jetCache")
    public  String jetCache() {
        //List<User> users = iTestService.jetCache(15);
        Integer i = iTestService.jetCache111(15);
        return i+"";
    }

    public static void main(String[] args) {
    }
    @GetMapping("/ratelimit")
    @OrderRateLimit
    public  String gdsgfsg() throws  InterruptedException {
        return "抢到了";
    }
    @GetMapping("/redistest")
    public  String redistest(@RequestParam("file") MultipartFile file) throws IOException {
        //EasyExcel.read(file.getInputStream(), ExportOrderDto.class,new ImportDataListener()).sheet().doRead();
        log.info("orderImport|fileName:{},fileSize:{}",file.getName(),file.getSize());
        List<ExportOrderDto> list = new ArrayList<>();
        long currentTimeMillis = System.currentTimeMillis();
        try(InputStream in = file.getInputStream()){
            //根据指定的文件输入流导入Excel从而产生Workbook对象
//            org.apache.poi.ss.usermodel.Workbook wb0 = new XSSFWorkbook(in);
            XSSFWorkbook wb0 = new XSSFWorkbook(in);
            //获取Excel文档中的sheet表单
//            org.apache.poi.ss.usermodel.Sheet sheetAt = wb0.getSheetAt(0);
            XSSFSheet sheetAt = wb0.getSheetAt(0);
            //对Sheet中的每一行进行迭代
            log.info("orderImport|rows size:{}",sheetAt.getLastRowNum());
            for (Row r : sheetAt) {
                //跳过第一行列名
                if (r.getRowNum() < 1) {
                    continue;
                }
                //每行总列数
                for (int j = 0; j < r.getPhysicalNumberOfCells(); j++) {
                    //设置每列数据类型
                    r.getCell(j).setCellType(1);
                }
                ExportOrderDto dto = new ExportOrderDto();
                if(StrUtil.isBlank(r.getCell(0).getStringCellValue())
                        ||StrUtil.isBlank(r.getCell(17).getStringCellValue())
                        ||StrUtil.isBlank(r.getCell(18).getStringCellValue())){
                    continue;
                }
                dto.setId(r.getCell(0).getStringCellValue());
                dto.setExpressName(r.getCell(17).getStringCellValue());
                dto.setExpressNum(r.getCell(18).getStringCellValue());
                list.add(dto);
            }
            log.info("orderImport|file read end|times:{}ms",System.currentTimeMillis()-currentTimeMillis);
        }
        return JSONObject.toJSONString(list);
    }

    @GetMapping("/tttt")
    public  String tess(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();

        //实例化实现了AnalysisEventListener接口的类
        ImportDataListener listener = new ImportDataListener();
        //传入参数
        ExcelReader excelReader = new ExcelReader(inputStream, ExcelTypeEnum.XLS, null, listener);
        //读取信息
        excelReader.read(new Sheet(1, 0, ExportOrderDto.class));

        //获取数据
        List<ExportOrderDto> list = listener.getDatas();
        int size = list.size();
        System.out.println(size);
        return JSONObject.toJSONString(list);
    }


    @GetMapping("/cache")
    public String cache(){
        User cache = iTestService.cache(40);
        System.out.println("第一次"+cache);
//        User cache1 = iTestService.cache(40);
//        System.out.println("第二次"+cache1);
//        User cache2 = iTestService.cache(49);
//        System.out.println("第三次"+cache2);
//        User cache3 = iTestService.cache(49);
//        System.out.println("第四次"+cache3);
//        User cache4 = iTestService.cache(55);
//        System.out.println("第五次"+cache4);
//        User cache5 = iTestService.cache(55);
//        System.out.println("第六次"+cache5);
//        Set<String> keys = redisTemplate.keys("test_cache::" + "*");
//        Long delete = redisTemplate.delete(keys);
//        System.out.println(delete);
        return "成功";
    }



    @GetMapping("/deleteCache")
    @CacheEvict(value = "test_cache",allEntries = true)
    public String deleteCache(Integer id){
        System.out.println(id);
        return "成功";
    }

    @GetMapping("/getBean")
    public String getBean(){
        Object redisToDbOneImpl = applicationContext.getBean("redisToDbOneImpl");

        return "成功";
    }

    @GetMapping("/redis")
    public String redis(){
       Object obj = null;
       if(Objects.isNull(obj)){
           redisToDbOne.ToDbselect(2);
       }
       return "成功";
    }



    @GetMapping("/async")
    public void te()throws Exception{
        for(int i = 0 ; i < 3 ; i ++){
            iTestService.async();
        }
        Set<String> set = new HashSet<>();
        set.add("ad");
    }
    private static Map<Integer,String> map = new ConcurrentHashMap<>(2,0.75f);



    public static void sort(int[] arr, int low, int high){
        //快速排序
        int i,j,temp;
        if(low>high){
            return;
        }
        i = low;
        j=high;
        temp = arr[low];
        while (i<j){
            while (i<j && temp <= arr[j]){
                j--;
            }
            while (i<j && temp >= arr[i]){
                i++;
            }
            if(i<j){
                int z = arr[i];
                arr[i] = arr[j];
                arr[j] = z;
            }
        }
        arr[low] = arr[i];
        arr[i] = temp;
        sort(arr,low,j-1);
        sort(arr,j+1,high);

    }

    //选择排序
    public void xuanze(){
        int[] arr = new int[]{5,0,-1,78,56,4};
        for (int i = 0; i < arr.length-1; i++) {
            int index = i;
            for (int k = i; k < arr.length; k++) {
                if(arr[k] < arr[index]){
                    index = k ;
                }
            }
            if(i != index){
                int temp = arr[i];
                arr[i] = arr[index];
                arr[index] = temp;
            }
        }
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }



}
