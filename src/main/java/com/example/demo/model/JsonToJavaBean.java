package com.example.demo.model;

/**
 * @author rulingfeng
 * @time 2023/1/4 14:19
 * @desc 通过一串json传可以生成javaBean 插件名GsonFormatPlus
 * 先创建一个类， 然后按 Option + s(Mac), Alt + s (win)  把json串丢到里面生成
 *
 */
public class JsonToJavaBean {
    //以下是模拟的json串
//    {
//        "birthday": "2023-01-04T06:19:19.288Z",
//            "city": 0,
//            "mergeMemberId": 0,
//            "idCard": "string",
//            "channel": [
//        {
//            "modifyBy": "string",
//                "unionId": "string",
//                "openId": "string",
//                "guideId": 0,
//                "binded": 0,
//                "accountNumber": "string",
//                "shopRegister": 0,
//                "cardNo": "string",
//                "version": 0,
//                "createBy": "string",
//                "modifyTime": "2023-01-04T06:19:19.288Z",
//                "createTime": "2023-01-04T06:19:19.288Z",
//                "brandId": 0,
//                "id": 0,
//                "lastchanged": "2023-01-04T06:19:19.288Z",
//                "memberId": 0,
//                "platformRegister": "string",
//                "status": "string"
//        }
//      ],
//        "cardNo": "string",
//            "cellPhoneNo": "string",
//            "modifyTime": "2023-01-04T06:19:19.288Z",
//            "province": 0,
//            "developmentBy": 0,
//            "memberExtend": {
//        "modifyBy": 0,
//                "education": "string",
//                "babyBirthday": "2023-01-04T06:19:19.288Z",
//                "weight": "string",
//                "version": 0,
//                "createBy": 0,
//                "mailBox": "string",
//                "modifyTime": "2023-01-04T06:19:19.288Z",
//                "createTime": "2023-01-04T06:19:19.288Z",
//                "lastchanged": "2023-01-04T06:19:19.288Z",
//                "height": "string",
//                "hobby": "string",
//                "memberId": 0
//    },
//        "timeDeviceRegister": "2023-01-04T06:19:19.288Z",
//            "memberId": 0,
//            "mergeTime": "2023-01-04T06:19:19.288Z",
//            "shopDeviceRegister": 0,
//            "area": 0,
//            "modifyBy": "string",
//            "isEmployee": 0,
//            "address": "string",
//            "consignee": [
//        {
//            "area": 0,
//                "country": 0,
//                "modifyBy": 0,
//                "address": "string",
//                "city": 0,
//                "version": 0,
//                "consigneeName": "string",
//                "createBy": 0,
//                "modifyTime": "2023-01-04T06:19:19.288Z",
//                "province": 0,
//                "createTime": "2023-01-04T06:19:19.288Z",
//                "consigneePhone": "string",
//                "id": 0,
//                "lastchanged": "2023-01-04T06:19:19.288Z",
//                "memberId": 0
//        }
//      ],
//        "town": "string",
//            "accountType": 0,
//            "memberShopGuideVos": [
//        {
//            "shopCode": "string",
//                "createTime": "2023-01-04T06:19:19.288Z",
//                "guideId": 0,
//                "shopName": "string",
//                "shopId": 0,
//                "guideCode": "string",
//                "guideName": "string"
//        }
//      ],
//        "sex": 0,
//            "accountNumber": "string",
//            "version": 0,
//            "realName": "string",
//            "createBy": "string",
//            "latelyConsigneePhone": "string",
//            "platformDeviceRegister": "string",
//            "createTime": "2023-01-04T06:19:19.288Z",
//            "name": "string",
//            "lastchanged": "2023-01-04T06:19:19.288Z",
//            "status": "string"
//    }
}
