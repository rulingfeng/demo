package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author rulingfeng
 * @time 2023/1/4 14:26
 * @desc 插件名Java Bean to Json  用法： 类名右键 ConvertToJson
 */
@NoArgsConstructor
@Data
public class JavaBeanToJson {
    @JsonProperty("birthday")
    private String birthday;
    @JsonProperty("city")
    private Integer city;
    @JsonProperty("mergeMemberId")
    private Integer mergeMemberId;
    @JsonProperty("idCard")
    private String idCard;
    @JsonProperty("channel")
    private List<ChannelDTO> channel;
    @JsonProperty("cardNo")
    private String cardNo;
    @JsonProperty("cellPhoneNo")
    private String cellPhoneNo;
    @JsonProperty("modifyTime")
    private String modifyTime;
    @JsonProperty("province")
    private Integer province;
    @JsonProperty("developmentBy")
    private Integer developmentBy;
    @JsonProperty("memberExtend")
    private MemberExtendDTO memberExtend;
    @JsonProperty("timeDeviceRegister")
    private String timeDeviceRegister;
    @JsonProperty("memberId")
    private Integer memberId;
    @JsonProperty("mergeTime")
    private String mergeTime;
    @JsonProperty("shopDeviceRegister")
    private Integer shopDeviceRegister;
    @JsonProperty("area")
    private Integer area;
    @JsonProperty("modifyBy")
    private String modifyBy;
    @JsonProperty("isEmployee")
    private Integer isEmployee;
    @JsonProperty("address")
    private String address;
    @JsonProperty("consignee")
    private List<ConsigneeDTO> consignee;
    @JsonProperty("town")
    private String town;
    @JsonProperty("accountType")
    private Integer accountType;
    @JsonProperty("memberShopGuideVos")
    private List<MemberShopGuideVosDTO> memberShopGuideVos;
    @JsonProperty("sex")
    private Integer sex;
    @JsonProperty("accountNumber")
    private String accountNumber;
    @JsonProperty("version")
    private Integer version;
    @JsonProperty("realName")
    private String realName;
    @JsonProperty("createBy")
    private String createBy;
    @JsonProperty("latelyConsigneePhone")
    private String latelyConsigneePhone;
    @JsonProperty("platformDeviceRegister")
    private String platformDeviceRegister;
    @JsonProperty("createTime")
    private String createTime;
    @JsonProperty("name")
    private String name;
    @JsonProperty("lastchanged")
    private String lastchanged;
    @JsonProperty("status")
    private String status;

    @NoArgsConstructor
    @Data
    public static class MemberExtendDTO {
        @JsonProperty("modifyBy")
        private Integer modifyBy;
        @JsonProperty("education")
        private String education;
        @JsonProperty("babyBirthday")
        private String babyBirthday;
        @JsonProperty("weight")
        private String weight;
        @JsonProperty("version")
        private Integer version;
        @JsonProperty("createBy")
        private Integer createBy;
        @JsonProperty("mailBox")
        private String mailBox;
        @JsonProperty("modifyTime")
        private String modifyTime;
        @JsonProperty("createTime")
        private String createTime;
        @JsonProperty("lastchanged")
        private String lastchanged;
        @JsonProperty("height")
        private String height;
        @JsonProperty("hobby")
        private String hobby;
        @JsonProperty("memberId")
        private Integer memberId;
    }

    @NoArgsConstructor
    @Data
    public static class ChannelDTO {
        @JsonProperty("modifyBy")
        private String modifyBy;
        @JsonProperty("unionId")
        private String unionId;
        @JsonProperty("openId")
        private String openId;
        @JsonProperty("guideId")
        private Integer guideId;
        @JsonProperty("binded")
        private Integer binded;
        @JsonProperty("accountNumber")
        private String accountNumber;
        @JsonProperty("shopRegister")
        private Integer shopRegister;
        @JsonProperty("cardNo")
        private String cardNo;
        @JsonProperty("version")
        private Integer version;
        @JsonProperty("createBy")
        private String createBy;
        @JsonProperty("modifyTime")
        private String modifyTime;
        @JsonProperty("createTime")
        private String createTime;
        @JsonProperty("brandId")
        private Integer brandId;
        @JsonProperty("id")
        private Integer id;
        @JsonProperty("lastchanged")
        private String lastchanged;
        @JsonProperty("memberId")
        private Integer memberId;
        @JsonProperty("platformRegister")
        private String platformRegister;
        @JsonProperty("status")
        private String status;
    }

    @NoArgsConstructor
    @Data
    public static class ConsigneeDTO {
        @JsonProperty("area")
        private Integer area;
        @JsonProperty("country")
        private Integer country;
        @JsonProperty("modifyBy")
        private Integer modifyBy;
        @JsonProperty("address")
        private String address;
        @JsonProperty("city")
        private Integer city;
        @JsonProperty("version")
        private Integer version;
        @JsonProperty("consigneeName")
        private String consigneeName;
        @JsonProperty("createBy")
        private Integer createBy;
        @JsonProperty("modifyTime")
        private String modifyTime;
        @JsonProperty("province")
        private Integer province;
        @JsonProperty("createTime")
        private String createTime;
        @JsonProperty("consigneePhone")
        private String consigneePhone;
        @JsonProperty("id")
        private Integer id;
        @JsonProperty("lastchanged")
        private String lastchanged;
        @JsonProperty("memberId")
        private Integer memberId;
    }

    @NoArgsConstructor
    @Data
    public static class MemberShopGuideVosDTO {
        @JsonProperty("shopCode")
        private String shopCode;
        @JsonProperty("createTime")
        private String createTime;
        @JsonProperty("guideId")
        private Integer guideId;
        @JsonProperty("shopName")
        private String shopName;
        @JsonProperty("shopId")
        private Integer shopId;
        @JsonProperty("guideCode")
        private String guideCode;
        @JsonProperty("guideName")
        private String guideName;
    }
}
