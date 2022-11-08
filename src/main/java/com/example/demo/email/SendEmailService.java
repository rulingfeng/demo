package com.example.demo.email;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.handler.inter.IExcelExportServer;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.example.demo.file.ExportDemoDTO;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author rulingfeng
 * @time 2022/10/21 13:36
 * @desc //pom引包
 */
@Service
@Slf4j
public class SendEmailService {

    @Value("${spring.mail.from}")
    private String mailFrom;

    @Resource
    private JavaMailSenderImpl mailSender;

    public PageInfo<MailExportVO> search(Integer pageNum,Integer pageSize){
        return null;
    }

    /**
     * 导出一百万行数据
     * @return
     */
    public String sendMessageByBigFile(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = sdf.format(new Date());
        String fileName = "记录导出_"+ dateString;
        String attachmentFileName = fileName + ".xlsx";
        Integer pageNum = 1;
        Integer pageSize = 1000;

        //异步处理导出数据及发送邮件
        ThreadUtil.execAsync(() -> {
            String exportError = "";
            int totalPage = 0;

            PageInfo<MailExportVO> pageInfoResult = search(pageNum,pageSize);
            if(pageInfoResult != null){
                totalPage = pageInfoResult.getPages();
                if(pageInfoResult.getTotal() > 1000000L){
                    exportError = "数据量超过100万条，暂不支持导出！";
                }
            }else{
                exportError = "查询有误";
            }

            File file = new File(fileName);
            if(StrUtil.isBlank(exportError)){
                try{
                    //EasyPoi导出大文件
                    ExportParams params = new ExportParams(null, fileName);
                    params.setType(ExcelType.XSSF);
                    Workbook workbook = null;
                    workbook = ExcelExportUtil.exportBigExcel(params, MailExportVO.class, new IExcelExportServer(){
                        /**
                         * @param obj   总页数
                         * @param page  当前页数
                         * @return
                         */
                        @Override
                        public List<Object> selectListForExcelExport(Object obj, int page) {
                            //page从1开始递增，当大于obj的值（总页数）时返回空，代码结束
                            if (((int) obj) < page) {
                                return null;
                            }
                            List<Object> list = new ArrayList<>();
                            //业务分页查询
                            PageInfo<MailExportVO> pageInfoResult = search(page,pageSize);
                            if(pageInfoResult != null){
                                list.addAll(pageInfoResult.getList());
                            }
                            return list;
                        }
                    }, totalPage);
                    //workbook写入文件
                    OutputStream os = new FileOutputStream(file);
                    workbook.write(os);
                    os.close();
                    workbook.close();
                }catch (Exception e){
                    exportError = e.getMessage();
                    log.error("记录导出异常:{}", e.getMessage());
                }
            }

            try{
                //发送邮件
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true,"UTF-8");
                messageHelper.setFrom(mailFrom);
                messageHelper.setTo("512387589@qq.com");
                messageHelper.setSubject(fileName);
                if(StrUtil.isBlank(exportError)){
                    messageHelper.setText("记录已导出，请查收附件！");
                    messageHelper.addAttachment(MimeUtility.encodeWord(attachmentFileName,"utf-8","B"), file);
                }else{
                    messageHelper.setText("记录导出错误：" + exportError);
                }
                mailSender.send(mimeMessage);
            }catch (Exception e){
                log.error("记录导出邮件发送失败:{}",e.getMessage());
            }finally {
                if(file.exists()) {
                    file.delete();
                }
            }
        });

        return "导出任务处理中，请稍后查收邮件！";
    }



    public void sendMessage(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = sdf.format(new Date());
        String fileName = "会议信息导出_"+ dateString;
        String attachmentFileName = fileName + ".xlsx";
        List<ExportDemoDTO> exportVOList = Lists.newArrayList();
        ExportDemoDTO exportDemoDTO = new ExportDemoDTO();
        exportDemoDTO.setId(88);
        exportDemoDTO.setMeetTheme("会议主题");
        exportDemoDTO.setCustomerParticipationNames("125161352");
        exportDemoDTO.setMeetContent("准时参加");
        exportVOList.add(exportDemoDTO);
        File file = new File(fileName);

        try{
            //生成导出文件
            ExportParams params = new ExportParams(null, fileName);
            params.setType(ExcelType.XSSF);
            Workbook workbook = ExcelExportUtil.exportExcel(params, ExportDemoDTO.class, exportVOList);
            //workbook写入文件
            OutputStream os = new FileOutputStream(file);
            workbook.write(os);
            os.close();
            workbook.close();
        }catch (Exception e){
            log.error("Excel记录导出异常:{}", e.getMessage());
            return;
        }


        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true,"UTF-8");
            //发送方邮箱
            messageHelper.setFrom(mailFrom);
            //要发送的邮箱
            messageHelper.setTo("512387589@qq.com");
            //主题
            messageHelper.setSubject(fileName);
            //内容
            messageHelper.setText("请查收附件！");
            //附件excel
//            messageHelper.addAttachment(MimeUtility.encodeWord(attachmentFileName,"utf-8","B"), file);
//            messageHelper.addAttachment(MimeUtility.encodeWord(attachmentFileName,"utf-8","B"), file);
            //附件 通过流发送图片
            FileInputStream fileInputStream = FileUtils.openInputStream(new File("src/main/resources/pic.png"));
            messageHelper.addAttachment(MimeUtility.encodeWord("图片名称","utf-8","B"),
                    new ByteArrayResource(IOUtils.toByteArray(fileInputStream)));


            mailSender.send(mimeMessage);
        }catch (Exception e){
            log.error("导出邮件发送失败:{}",e.getMessage());
            return;
        }finally {
            if(file.exists()) {
                file.delete();
            }
        }

    }
}
