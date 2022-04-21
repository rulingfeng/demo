package com.example.demo.file;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.example.demo.model.User;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author: 茹凌丰
 * @date: 2022/4/21
 * @description:
 */
@RestController
@RequestMapping("/file")
public class FileController {




    //导入
    @PostMapping("/importExcel")
    public String importExcel(@RequestParam("file") MultipartFile file) {
        //解析excel数据
        ImportParams params = new ImportParams();
        params.setHeadRows(1);
        //读取出来的内容
        List<ImportDemoDto> customerImportDtoList;
        try {
            customerImportDtoList = ExcelImportUtil.importExcel(file.getInputStream(), ImportDemoDto.class,params);
        } catch (Exception e) {
            //log.error("读取客户导入文件失败：{}", e);
            return "读取客户导入文件失败";
        }

        return "导入成功";
    }

    //导出
    @PostMapping("/export")
    public String exportExcel(HttpServletResponse response, @Valid @RequestBody User user) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = sdf.format(new Date());
        String fileName = "会议信息导出_"+ dateString;
        try {
            //导出的数据
            List<ExportDemoDTO> exportVOList = Lists.newArrayList();

            //导出
            ExportParams params = new ExportParams(null, fileName);
            params.setType(ExcelType.XSSF);
            Workbook workbook = ExcelExportUtil.exportExcel(params, ExportDemoDTO.class, exportVOList);
            response.setHeader("Content-Type", "application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode(fileName,"UTF-8")+".xlsx");
            response.setCharacterEncoding("UTF-8");
            try {
                workbook.write(response.getOutputStream());
                workbook.close();
            } catch (IOException e) {
                //log.error("会议信息下载异常:{}",e);
                response.reset();
                return "会议信息下载异常";
            }
        } catch (Exception e) {
            //log.error("会议信息导出失败{}", e);
            response.reset();
            return "会议信息导出失败";
        }
        return "导出成功";
    }



}
