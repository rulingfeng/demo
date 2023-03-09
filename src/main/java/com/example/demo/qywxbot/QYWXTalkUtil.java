package com.example.demo.qywxbot;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author ru
 * 企业微信机器人相关接口实现
 */
@Slf4j
@Component
public class QYWXTalkUtil {

    /** 企业微信群上传文件url */
    public static final String UPLOAD_FILE_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/upload_media";
    /** 发送群消息url */
    public static final String SEND_MESSAGE_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send";

    /**
     * 企业微信创建机器人后给的key
     */
    @Value("${qywx.offShelfBotKey:c2090132-3eae-4302-88b8-8d0524076111}")
    private String offShelfBotKey;


    /**
     * 导出excel到企业微信机器人
     * @param data
     */
    public void exportExcelToQWBot(List<OffShelfSkuInfo> data){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = sdf.format(new Date());
        String fileName = "饿了么美团下架记录导出_"+ dateString;
        String attachmentFileName = fileName + ".csv";

        File file = new File(attachmentFileName);

        //EasyPoi导出大文件
        ExportParams params = new ExportParams(null, fileName);
        params.setType(ExcelType.XSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(params, OffShelfSkuInfo.class, data);
        //workbook写入文件

        try(OutputStream os = Files.newOutputStream(file.toPath())) {
            workbook.write(os);
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try{
            //上传文件
            String urlString = UPLOAD_FILE_URL + "?key=" + offShelfBotKey + "&type=file";
            HashMap<String, Object> sendMap = new HashMap<>();
            sendMap.put("file", file);
            String result = HttpUtil.post(urlString, sendMap);
            JSONObject jsonObject = JSON.parseObject(result);
            Integer errcode = Integer.valueOf(jsonObject.get("errcode").toString());
            if (errcode.equals(0)) {
                // 推送消息
                String mediaid = (String) jsonObject.get("media_id");
                String sendUrl = SEND_MESSAGE_URL + "?key=" + offShelfBotKey;
                Map<String,Object> mediaMap = new HashMap<>();
                mediaMap.put("media_id",mediaid);
                Map<String,Object> msgMap = new HashMap<>();
                msgMap.put("msgtype","file");
                msgMap.put("file",mediaMap);
                HttpUtil.post(sendUrl, JSON.toJSONString(msgMap));
            } else {
                log.info("饿了么美团下架信息上传企微bot结果:{}",result);
                throw new ServiceException("企业微信群上传文件失败");
            }
        }catch (Exception e){
            log.error("饿了么美团下架记录导出_:{}",e.getMessage());
        }finally {
            if(file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 发送企业微信机器人消息
     * @param env
     * @param serverName
     * @param e
     */
    public void sendMsgToBot(String env,String serverName,Exception e){
        String sendUrl = SEND_MESSAGE_URL + "?key=" + offShelfBotKey;
        Map<String, Object> body = new HashMap<>();
        Map<String, String> markdown = new HashMap<>();
        String content = "**服务报警信息，请相关同事注意处理。%s环境**><font color=\"warning\">警报标识：</font><font color=\"warning\">%s</font><font color=\"warning\">警报服务：</font><font color=\"warning\">%s</font>><font color=\"warning\">警报日志：</font>%s";
        markdown.put("content", String.format(content, env, "traceId....", serverName, e));
        body.put("msgtype", "markdown");
        body.put("markdown", markdown);
        HttpUtil.post(sendUrl, JSON.toJSONString(body));
    }




}
