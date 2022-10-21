package com.example.demo.email;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.core.collection.CollectionUtil;
import com.example.demo.file.ExportDemoDTO;
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
