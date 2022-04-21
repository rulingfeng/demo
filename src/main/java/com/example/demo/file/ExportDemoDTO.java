package com.example.demo.file;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author rulingfeng
 * @since 2022-03-29
 */
@Data
public class ExportDemoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "编号", width = 20, orderNum = "1")
    private Integer id;

    @ApiModelProperty(value = "会议主题")
    @Excel(name = "会议主题", width = 20, orderNum = "2")
    private String meetTheme;


    @ApiModelProperty(value = "内部参与人")
    @Excel(name = "内部参与人", width = 20, orderNum = "3")
    private String internalParticipationNames;

    @ApiModelProperty(value = "客户参与人")
    @Excel(name = "客户参与人", width = 20, orderNum = "4")
    private String customerParticipationNames;

    @ApiModelProperty(value = "会议开始时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "会议开始时间", format = "yyyy-MM-dd HH:mm:ss", width = 25, orderNum = "5")
    private Date startTime;

    @ApiModelProperty(value = "时长中文")
    @Excel(name = "时长", width = 20, orderNum = "6")
    private String durationString;

    @ApiModelProperty(value = "会议内容")
    @Excel(name = "会议内容", width = 20, orderNum = "7")
    private String meetContent;

    @ApiModelProperty(value = "会议照片")
    @Excel(name = "会议照片", width = 30, orderNum = "8")
    private String storePhoto;

    @ApiModelProperty(value = "附件")
    @Excel(name = "附件", width = 30, orderNum = "9")
    private String file;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人", width = 30, orderNum = "10")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", format = "yyyy-MM-dd HH:mm:ss", width = 25, orderNum = "11")
    private Date createTime;




}
