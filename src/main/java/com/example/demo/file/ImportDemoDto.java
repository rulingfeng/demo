package com.example.demo.file;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author fan.wang
 */
@Data
@ApiModel(value="CustomerImportDto", description="客户导入")
public class ImportDemoDto implements Serializable {

    private static final long serialVersionUID = 1L;



    @ApiModelProperty(value = "客户名称")
    @Excel(name = "客户名称")
    private String name;

    @ApiModelProperty(value = "级别 1:Ka,2:A,3:B,4:C,5:D,...")
    @Excel(name = "客户级别", replace = {"ka_1","A_2","B_3","C_4","D_5"})
    private Integer level;

    @ApiModelProperty(value = "负责业务员")
    @Excel(name = "负责业务员账号")
    private String username;

    @ApiModelProperty(value = "负责人id")
    private Integer responsibleUserId;

    @ApiModelProperty(value = "协同人id集合")
    private List<Integer> cooperateUserIdList;

    @ApiModelProperty(value = "合作关系 0:待定,1:已合作,2:未合作")
    @Excel(name = "合作关系", replace = {"待定_0","已合作_1","未合作_2"})
    private Integer partnership = 0;

    @ApiModelProperty(value = "手机")
    @Excel(name = "手机")
    private String mobile;

    @ApiModelProperty(value = "行业")
    @Excel(name = "行业")
    private String industry;

    @ApiModelProperty(value = "国家")
    @Excel(name = "国家")
    private String country;

    @ApiModelProperty(value = "省")
    @Excel(name = "省")
    private String province;

    @ApiModelProperty(value = "市")
    @Excel(name = "市")
    private String city;

    @ApiModelProperty(value = "区")
    @Excel(name = "区")
    private String district;

    @ApiModelProperty(value = "详细地址")
    @Excel(name = "详细地址")
    private String address;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String memo;

    @ApiModelProperty(value = "电话")
    @Excel(name = "电话")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    @Excel(name = "邮箱")
    private String mail;

    @ApiModelProperty(value = "传真")
    @Excel(name = "传真")
    private String fax;

    @ApiModelProperty(value = "属性")
    @Excel(name = "客户属性")
    private String attribute;

    @ApiModelProperty(value = "合作年限")
    @Excel(name = "合作年限")
    private Integer cooperationYears;

    @ApiModelProperty(value = "终端数量")
    @Excel(name = "终端数量")
    private Integer terminalNums;

    @ApiModelProperty(value = "送奶车辆")
    @Excel(name = "送奶车辆")
    private Integer vehicleNums;

    @ApiModelProperty(value = "是否携带非我司产品 0:否,1:是")
    @Excel(name = "携带非我司产品", replace = {"否_0","是_1"})
    private Integer isCarryOther = 0;

    @ApiModelProperty(value = "协同人账号1")
    @Excel(name = "协同人账号1")
    private String username1;

    @ApiModelProperty(value = "协同人账号2")
    @Excel(name = "协同人账号2")
    private String username2;

    @ApiModelProperty(value = "协同人账号3")
    @Excel(name = "协同人账号3")
    private String username3;

    @ApiModelProperty(value = "协同人账号4")
    @Excel(name = "协同人账号4")
    private String username4;

    @ApiModelProperty(value = "协同人账号5")
    @Excel(name = "协同人账号5")
    private String username5;
}
