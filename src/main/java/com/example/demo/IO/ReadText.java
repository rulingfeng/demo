package com.example.demo.IO;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Maps;
import com.example.demo.common.OkHttpUtil;
import eleme.openapi.sdk.api.entity.order.OGoodsGroup;
import eleme.openapi.sdk.api.entity.order.OGoodsItem;
import eleme.openapi.sdk.api.entity.order.OOrder;
import eleme.openapi.sdk.api.enumeration.order.OOrderDetailGroupType;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author 茹凌丰
 * @Description:
 * @date 2021/1/9-13:27
 */
public class ReadText {
    public static void main(String[] args)throws Exception {
        //readLine();
        //System.out.println(longs.size());
        //readJson();
        sendMsgMEITUANPOSCREATEOrder();


    }

    public static void sendMsg() throws IOException{
        List<String> strings = FileUtils.readLines(new File("src/main/resources/userId.txt"));
        if(CollectionUtil.isEmpty(strings)){
            return;
        }
        System.out.println(strings.size());
        String url = "https://nainmsim.inm.cc/inm-sms-center/public/app/sendMassage";
        Map<String, String> params = Maps.newHashMap();
        params.put("activityName","一鸣充值送现金活动最后2天！");
        params.put("activityDesc","限时充值330元送30元福利");
        params.put("activityTime","2023年4月24日 00:00");
        params.put("activityDeadlineTime","2023年4月26日 24:00");
        params.put("reminder","若已充值，请忽略本消息。");
        params.put("path","packageMy/pages/my-rechargePage/my-rechargePage");
        params.put("msgType","18");

        for (String userId : strings) {
            params.put("userId",userId);
            String post = OkHttpUtil.postJsonParams(url, JSONObject.toJSONString(params));
            System.out.println(userId);
        }
    }

    public static void sendMsgPOSREJECTED() throws IOException{
        List<String> strings = FileUtils.readLines(new File("src/main/resources/userId.txt"));
        if(CollectionUtil.isEmpty(strings)){
            return;
        }
        System.out.println(strings.size());
        String url = "http://zt-pos.inm.cc:8028/JLESServer/POS_API?Type=OMS_CREATE_REJECTED";

        Long a =  1438531L;
        for (String userId : strings) {
            String[] split = userId.split("\t");
            Map<String, Object> params = Maps.newHashMap();
            params.put("ORDER_NUNBER",a);
            params.put("STORE",split[1]);
            params.put("WSDH",split[0]);
//            params.put("userId",userId);
            String post = OkHttpUtil.postJsonParams(url, JSONObject.toJSONString(params));
//            System.out.println(userId);

            System.out.println(split[0]+"+"+split[1]+"+"+post);
            a++;
        }
    }

    public static void sendMsgELEPOSCREATEOrder() throws IOException{
        List<String> strings = FileUtils.readLines(new File("src/main/resources/userId.txt"));
        if(CollectionUtil.isEmpty(strings)){
            return;
        }
        System.out.println(strings.size());
        String url = "http://zt-pos.inm.cc:8028/JLESServer/POS_API?Type=OMS_CREATE_ADVANCEORDER";

        for (String thirdJson : strings) {
            OOrder order = JSONObject.parseObject(thirdJson, OOrder.class);
            //System.out.println(jsonObject);
//
            JSONObject jsonXml = new JSONObject();

           // jsonXml.put("STORE", "9996");   // SAP部门代码
            jsonXml.put("SALES_AMOUNT", order.getTotalPrice());  //销售订单总金额
            jsonXml.put("ZKJE_AMOUNT", 0);             //折扣金额

            jsonXml.put("CARDID", "-1");          //会员卡号
            jsonXml.put("VIPID","-1");   //会员内码
            jsonXml.put("MEMBER_NAME", "-1");    //会员名称(没有传CARDID)
            jsonXml.put("CUSTOMER_NAME", order.getConsignee());  //客户姓名

            jsonXml.put("REMARK", order.getDescription());
            jsonXml.put("ORDER_NUNBER", order.getId()); //预售订单单号
            jsonXml.put("XFLX", "外卖");
            jsonXml.put("QCSJ", order.getPickUpTime());       //取餐时间
            jsonXml.put("THM", "-1");     //提货码
            jsonXml.put("CHANNEL","ele");
            jsonXml.put("XSLB","PT");


            JSONArray skArray = new JSONArray();
            //收款
            JSONObject skJson = new JSONObject();
            skJson.put("SK_CODE", "90");
            skJson.put("SKFS_JE", order.getTotalPrice());
            skArray.add(skJson);
            jsonXml.put("SKLIST", skArray);


            JSONArray spArray = new JSONArray();
            for (OGoodsGroup group : order.getGroups()) {
                if(!group.getType().equals(OOrderDetailGroupType.normal)){
                    continue;
                }
                List<OGoodsItem> items = group.getItems();
                for (OGoodsItem item : items) {
                    String extendCode = item.getExtendCode();
                    BigDecimal multiply = new BigDecimal(item.getPrice()).multiply(new BigDecimal(item.getQuantity()));

                    JSONObject detailJson = new JSONObject();
                    detailJson.put("SPYJ", item.getPrice());    //商品单价
                    detailJson.put("XSJE", multiply.doubleValue());          //	商品销售金额
                    detailJson.put("XSSL", item.getQuantity());             //	商品销售数量
                    detailJson.put("SPNM", extendCode);         //	SAP物料代码
                    detailJson.put("SP_ZKJE", 0);      //商品折扣金额
                    spArray.add(detailJson);
                }
                jsonXml.put("SPLIST", spArray);
            }



            String post = OkHttpUtil.postJsonParams(url, jsonXml.toString());
            System.out.println(post+"+"+jsonXml);
        }
    }


    //TODO 对应门店 和 对应来源 ele，meituan

    public static void sendMsgMEITUANPOSCREATEOrder() throws IOException{
        List<String> strings = FileUtils.readLines(new File("src/main/resources/userId.txt"));
        if(CollectionUtil.isEmpty(strings)){
            return;
        }
        System.out.println(strings.size());
        String url = "http://zt-pos.inm.cc:8028/JLESServer/POS_API?Type=OMS_CREATE_ADVANCEORDER";

        for (String thirdJson : strings) {
            String[] split = thirdJson.split("&");
            JSONObject jsonObject = new JSONObject();
            for (String s : split) {
                String[] split1 = s.split("=");
                if(split1.length>1){
                    jsonObject.put(split1[0],split1[1]);
                }else if(split1.length ==1){
                    jsonObject.put(split1[0],"");
                }

            }
           // System.out.println(jsonObject);
//
            JSONObject jsonXml = new JSONObject();

            //   jsonXml.put("STORE", "9996");   // SAP部门代码
            jsonXml.put("SALES_AMOUNT", jsonObject.get("total"));  //销售订单总金额
            jsonXml.put("ZKJE_AMOUNT", 0);             //折扣金额

            jsonXml.put("CARDID", "-1");          //会员卡号
            jsonXml.put("VIPID","-1");   //会员内码
            jsonXml.put("MEMBER_NAME", "-1");    //会员名称(没有传CARDID)
            jsonXml.put("CUSTOMER_NAME", jsonObject.get("recipient_name"));  //客户姓名

            jsonXml.put("REMARK", jsonObject.get("caution"));
            jsonXml.put("ORDER_NUNBER", jsonObject.get("order_id")); //预售订单单号
            jsonXml.put("XFLX", "外卖");
            jsonXml.put("QCSJ", jsonObject.get("delivery_time"));       //取餐时间
            jsonXml.put("THM", "-1");     //提货码
            jsonXml.put("CHANNEL","meituan");
            jsonXml.put("XSLB","PT");

            JSONArray skArray = new JSONArray();
            //收款
            JSONObject skJson = new JSONObject();
            skJson.put("SK_CODE", "90");
            skJson.put("SKFS_JE", jsonObject.get("total"));
            skArray.add(skJson);
            jsonXml.put("SKLIST", skArray);


            JSONArray spArray = new JSONArray();
            JSONArray detail = jsonObject.getJSONArray("detail");

            for (Object o : detail) {
                JSONObject jsonObject1 = JSONObject.parseObject(o.toString());
                Object sku_id = jsonObject1.get("sku_id");
                BigDecimal multiply = new BigDecimal( jsonObject1.get("price").toString()).multiply(new BigDecimal(jsonObject1.get("quantity").toString()));

                JSONObject detailJson = new JSONObject();
                detailJson.put("SPYJ",  jsonObject1.get("price"));    //商品单价
                detailJson.put("XSJE", multiply.doubleValue());          //	商品销售金额
                detailJson.put("XSSL", jsonObject1.get("quantity"));             //	商品销售数量
                detailJson.put("SPNM", sku_id);         //	SAP物料代码
                detailJson.put("SP_ZKJE", 0);      //商品折扣金额
                spArray.add(detailJson);


            }
            jsonXml.put("SPLIST", spArray);



            String post = OkHttpUtil.postJsonParams(url, jsonXml.toString());
            System.out.println(post+"+"+jsonXml);
        }
    }







    public static void readLine() throws IOException {
        StringBuilder str = new StringBuilder(16);
        //List<String> strings = FileUtils.readLines(new File(ReadText.class.getResource("/goodsData.txt").getPath()));
        List<String> strings = FileUtils.readLines(new File("src/main/resources/goodsData.txt"));
        strings.forEach(i->{
            String[] split = i.split("\t");
            str.append("   {\n" +
                    "            \"mainSupplierId\":"+split[3]+",\n" +
                    "            \"mainSupplierName\":\""+split[5]+"\",\n" +
                    "            \"supplierSubDTOList\":[\n" +
                    "                {\n" +
                    "                    \"memberId\":"+split[0]+",\n" +
                    "                    \"supplierId\":"+split[1]+",\n" +
                    "                    \"supplierName\":\""+split[2]+"\"\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        },");
        });
        System.out.println(str.toString());
    }

    public  static void readJson(){
        String a ="[{\n" +
                "            \"mainSupplierId\":500001000023370,\n" +
                "            \"mainSupplierName\":\"南京市小卒管理咨询有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000032077,\n" +
                "                    \"supplierId\":111128461,\n" +
                "                    \"supplierName\":\"南京市小卒管理咨询有限公司\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000023370,\n" +
                "            \"mainSupplierName\":\"南京市小卒管理咨询有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000032077,\n" +
                "                    \"supplierId\":111128461,\n" +
                "                    \"supplierName\":\"南京市小卒管理咨询有限公司\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000003620,\n" +
                "            \"mainSupplierName\":\"宁夏索米亚生态农业科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000026327,\n" +
                "                    \"supplierId\":111053954,\n" +
                "                    \"supplierName\":\"宁夏索米亚生态农业科技有限公司亚麻籽油\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000003620,\n" +
                "            \"mainSupplierName\":\"宁夏索米亚生态农业科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000026327,\n" +
                "                    \"supplierId\":111053954,\n" +
                "                    \"supplierName\":\"宁夏索米亚生态农业科技有限公司亚麻籽油\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000007942,\n" +
                "            \"mainSupplierName\":\"温州芭芭猴电子商务有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000032591,\n" +
                "                    \"supplierId\":111132069,\n" +
                "                    \"supplierName\":\"温州芭芭猴电子商务有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000001647,\n" +
                "            \"mainSupplierName\":\"科沃斯机器人科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000013936,\n" +
                "                    \"supplierId\":110905838,\n" +
                "                    \"supplierName\":\"科沃斯机器人科技有限公司-寄售2\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000001647,\n" +
                "            \"mainSupplierName\":\"科沃斯机器人科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000013936,\n" +
                "                    \"supplierId\":110905838,\n" +
                "                    \"supplierName\":\"科沃斯机器人科技有限公司-寄售2\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000024136,\n" +
                "            \"mainSupplierName\":\"万魔声学股份有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031981,\n" +
                "                    \"supplierId\":111126628,\n" +
                "                    \"supplierName\":\"万魔声学科技有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000024136,\n" +
                "            \"mainSupplierName\":\"万魔声学股份有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031981,\n" +
                "                    \"supplierId\":111126628,\n" +
                "                    \"supplierName\":\"万魔声学科技有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000017433,\n" +
                "            \"mainSupplierName\":\"上海多妙屋儿童用品有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000028422,\n" +
                "                    \"supplierId\":111085939,\n" +
                "                    \"supplierName\":\"上海多妙屋儿童用品有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000023048,\n" +
                "            \"mainSupplierName\":\"安徽多多宝电子商务有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000030169,\n" +
                "                    \"supplierId\":111105338,\n" +
                "                    \"supplierName\":\"安徽多多宝电子商务有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000023864,\n" +
                "            \"mainSupplierName\":\"耐克商业（中国）有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031751,\n" +
                "                    \"supplierId\":111120760,\n" +
                "                    \"supplierName\":\"耐克商业（中国）有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000017480,\n" +
                "            \"mainSupplierName\":\"上海柏材贸易有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000030765,\n" +
                "                    \"supplierId\":111111060,\n" +
                "                    \"supplierName\":\"上海柏材贸易有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000006206,\n" +
                "            \"mainSupplierName\":\"安克创新科技股份有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000039000,\n" +
                "                    \"supplierId\":111141580,\n" +
                "                    \"supplierName\":\"安克创新科技股份有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000006206,\n" +
                "            \"mainSupplierName\":\"安克创新科技股份有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000039000,\n" +
                "                    \"supplierId\":111141580,\n" +
                "                    \"supplierName\":\"安克创新科技股份有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000023999,\n" +
                "            \"mainSupplierName\":\"北京搜狗科技发展有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000039001,\n" +
                "                    \"supplierId\":111141581,\n" +
                "                    \"supplierName\":\"北京搜狗科技发展有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000023999,\n" +
                "            \"mainSupplierName\":\"北京搜狗科技发展有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000039001,\n" +
                "                    \"supplierId\":111141581,\n" +
                "                    \"supplierName\":\"北京搜狗科技发展有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000024074,\n" +
                "            \"mainSupplierName\":\"北京视果科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031983,\n" +
                "                    \"supplierId\":111126639,\n" +
                "                    \"supplierName\":\"北京视果科技有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000024074,\n" +
                "            \"mainSupplierName\":\"北京视果科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031983,\n" +
                "                    \"supplierId\":111126639,\n" +
                "                    \"supplierName\":\"北京视果科技有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000024134,\n" +
                "            \"mainSupplierName\":\"深圳罗马仕科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000038999,\n" +
                "                    \"supplierId\":111141579,\n" +
                "                    \"supplierName\":\"深圳罗马仕科技有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000024134,\n" +
                "            \"mainSupplierName\":\"深圳罗马仕科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000038999,\n" +
                "                    \"supplierId\":111141579,\n" +
                "                    \"supplierName\":\"深圳罗马仕科技有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000023936,\n" +
                "            \"mainSupplierName\":\"上海振茵贸易有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000039436,\n" +
                "                    \"supplierId\":111145398,\n" +
                "                    \"supplierName\":\"上海振茵贸易有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000023936,\n" +
                "            \"mainSupplierName\":\"上海振茵贸易有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000039436,\n" +
                "                    \"supplierId\":111145398,\n" +
                "                    \"supplierName\":\"上海振茵贸易有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000021649,\n" +
                "            \"mainSupplierName\":\"杭州植木电子商务有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000027729,\n" +
                "                    \"supplierId\":111076448,\n" +
                "                    \"supplierName\":\"杭州植木电子商务有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000004171,\n" +
                "            \"mainSupplierName\":\"长沙赛鲸电子科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000011458,\n" +
                "                    \"supplierId\":110861266,\n" +
                "                    \"supplierName\":\"长沙赛鲸电子科技有限公司【寄售】【3C】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000004171,\n" +
                "            \"mainSupplierName\":\"长沙赛鲸电子科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000011458,\n" +
                "                    \"supplierId\":110861266,\n" +
                "                    \"supplierName\":\"长沙赛鲸电子科技有限公司【寄售】【3C】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000018531,\n" +
                "            \"mainSupplierName\":\"杭州雅行实业有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000040024,\n" +
                "                    \"supplierId\":111147566,\n" +
                "                    \"supplierName\":\"杭州雅行实业有限公司\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000027036,\n" +
                "            \"mainSupplierName\":\"合肥乖米熊科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000039547,\n" +
                "                    \"supplierId\":111146099,\n" +
                "                    \"supplierName\":\"合肥乖米熊科技有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000023669,\n" +
                "            \"mainSupplierName\":\"杭州驰蒂维电子商务有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031727,\n" +
                "                    \"supplierId\":111120533,\n" +
                "                    \"supplierName\":\"杭州驰蒂维电子商务有限公司-寄售\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000023669,\n" +
                "            \"mainSupplierName\":\"杭州驰蒂维电子商务有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031727,\n" +
                "                    \"supplierId\":111120533,\n" +
                "                    \"supplierName\":\"杭州驰蒂维电子商务有限公司-寄售\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000018250,\n" +
                "            \"mainSupplierName\":\"泉州市同班同学商贸有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000028533,\n" +
                "                    \"supplierId\":111086799,\n" +
                "                    \"supplierName\":\"泉州市同班同学商贸有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500000002003459,\n" +
                "            \"mainSupplierName\":\"汕头市金江贸易有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000029915,\n" +
                "                    \"supplierId\":111101427,\n" +
                "                    \"supplierName\":\"会员店--汕头市金江贸易有限公司\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500000002003459,\n" +
                "            \"mainSupplierName\":\"汕头市金江贸易有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000029915,\n" +
                "                    \"supplierId\":111101427,\n" +
                "                    \"supplierName\":\"会员店--汕头市金江贸易有限公司\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000024361,\n" +
                "            \"mainSupplierName\":\"宁波悠伴智能科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000039182,\n" +
                "                    \"supplierId\":111142327,\n" +
                "                    \"supplierName\":\"宁波悠伴智能科技有限公司--寄售\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000024361,\n" +
                "            \"mainSupplierName\":\"宁波悠伴智能科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000039182,\n" +
                "                    \"supplierId\":111142327,\n" +
                "                    \"supplierName\":\"宁波悠伴智能科技有限公司--寄售\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000003144,\n" +
                "            \"mainSupplierName\":\"上海宝尊电子商务有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000017067,\n" +
                "                    \"supplierId\":110966777,\n" +
                "                    \"supplierName\":\"上海宝尊电子商务有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000003144,\n" +
                "            \"mainSupplierName\":\"上海宝尊电子商务有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000017067,\n" +
                "                    \"supplierId\":110966777,\n" +
                "                    \"supplierName\":\"上海宝尊电子商务有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000002527,\n" +
                "            \"mainSupplierName\":\"成都蓝眼电子商务有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000011749,\n" +
                "                    \"supplierId\":110866278,\n" +
                "                    \"supplierName\":\"成都蓝眼电子商务有限公司—厨房电器\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000002838,\n" +
                "            \"mainSupplierName\":\"深圳市博盛景数码科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000009089,\n" +
                "                    \"supplierId\":110790472,\n" +
                "                    \"supplierName\":\"深圳市博盛景数码科技有限公司\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000002838,\n" +
                "            \"mainSupplierName\":\"深圳市博盛景数码科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000009089,\n" +
                "                    \"supplierId\":110790472,\n" +
                "                    \"supplierName\":\"深圳市博盛景数码科技有限公司\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000021154,\n" +
                "            \"mainSupplierName\":\"深圳市云动创想科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000027628,\n" +
                "                    \"supplierId\":111074889,\n" +
                "                    \"supplierName\":\"深圳市云动创想科技有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000021154,\n" +
                "            \"mainSupplierName\":\"深圳市云动创想科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000027628,\n" +
                "                    \"supplierId\":111074889,\n" +
                "                    \"supplierName\":\"深圳市云动创想科技有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000002527,\n" +
                "            \"mainSupplierName\":\"成都蓝眼电子商务有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000008528,\n" +
                "                    \"supplierId\":110568918,\n" +
                "                    \"supplierName\":\"成都蓝眼电子商务有限公司1\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000019590,\n" +
                "            \"mainSupplierName\":\"山西嘉世达机器人技术有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031564,\n" +
                "                    \"supplierId\":111117732,\n" +
                "                    \"supplierName\":\"山西嘉世达机器人技术有限公司-寄售\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000024070,\n" +
                "            \"mainSupplierName\":\"江西省缔杭婴童用品有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031916,\n" +
                "                    \"supplierId\":111124907,\n" +
                "                    \"supplierName\":\"江西省缔杭婴童用品有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500000002011696,\n" +
                "            \"mainSupplierName\":\"杭州日冠服饰有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000028416,\n" +
                "                    \"supplierId\":111085924,\n" +
                "                    \"supplierName\":\"杭州日冠服饰有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000023671,\n" +
                "            \"mainSupplierName\":\"苏州今利旸贸易有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031623,\n" +
                "                    \"supplierId\":111119067,\n" +
                "                    \"supplierName\":\"苏州今利旸贸易有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000023671,\n" +
                "            \"mainSupplierName\":\"苏州今利旸贸易有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031623,\n" +
                "                    \"supplierId\":111119067,\n" +
                "                    \"supplierName\":\"苏州今利旸贸易有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000024028,\n" +
                "            \"mainSupplierName\":\"广东羽博科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031985,\n" +
                "                    \"supplierId\":111126650,\n" +
                "                    \"supplierName\":\"广东羽博科技有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000024028,\n" +
                "            \"mainSupplierName\":\"广东羽博科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031985,\n" +
                "                    \"supplierId\":111126650,\n" +
                "                    \"supplierName\":\"广东羽博科技有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000003852,\n" +
                "            \"mainSupplierName\":\"深圳市绿联科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031986,\n" +
                "                    \"supplierId\":111126655,\n" +
                "                    \"supplierName\":\"深圳市绿联科技有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000003852,\n" +
                "            \"mainSupplierName\":\"深圳市绿联科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031986,\n" +
                "                    \"supplierId\":111126655,\n" +
                "                    \"supplierName\":\"深圳市绿联科技有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000024092,\n" +
                "            \"mainSupplierName\":\"厦门爱立得科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031988,\n" +
                "                    \"supplierId\":111126657,\n" +
                "                    \"supplierName\":\"厦门爱立得科技有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000024092,\n" +
                "            \"mainSupplierName\":\"厦门爱立得科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031988,\n" +
                "                    \"supplierId\":111126657,\n" +
                "                    \"supplierName\":\"厦门爱立得科技有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000017457,\n" +
                "            \"mainSupplierName\":\"佛山市婴爱尚服饰有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000027680,\n" +
                "                    \"supplierId\":111075615,\n" +
                "                    \"supplierName\":\"佛山市婴爱尚服饰有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500000000008145,\n" +
                "            \"mainSupplierName\":\"北京一商美洁商业有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000041205,\n" +
                "                    \"supplierId\":111169105,\n" +
                "                    \"supplierName\":\"北京一商美洁商业有限公司-寄售商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500000000008145,\n" +
                "            \"mainSupplierName\":\"北京一商美洁商业有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000041205,\n" +
                "                    \"supplierId\":111169105,\n" +
                "                    \"supplierName\":\"北京一商美洁商业有限公司-寄售商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000004335,\n" +
                "            \"mainSupplierName\":\"扬州蓝白工艺品有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000042188,\n" +
                "                    \"supplierId\":111183390,\n" +
                "                    \"supplierName\":\"扬州蓝白工艺品有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000001092,\n" +
                "            \"mainSupplierName\":\"博库网络有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000029682,\n" +
                "                    \"supplierId\":111098378,\n" +
                "                    \"supplierName\":\"博库网络有限公司-寄售\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000001092,\n" +
                "            \"mainSupplierName\":\"博库网络有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000029682,\n" +
                "                    \"supplierId\":111098378,\n" +
                "                    \"supplierName\":\"博库网络有限公司-寄售\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000023963,\n" +
                "            \"mainSupplierName\":\"武汉匡臣科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031987,\n" +
                "                    \"supplierId\":111126656,\n" +
                "                    \"supplierName\":\"武汉匡臣科技有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000023963,\n" +
                "            \"mainSupplierName\":\"武汉匡臣科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031987,\n" +
                "                    \"supplierId\":111126656,\n" +
                "                    \"supplierName\":\"武汉匡臣科技有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000024055,\n" +
                "            \"mainSupplierName\":\"杭州嘿鱼电子科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000032076,\n" +
                "                    \"supplierId\":111128429,\n" +
                "                    \"supplierName\":\"杭州嘿鱼电子科技有限公司【Morrorart】【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000024055,\n" +
                "            \"mainSupplierName\":\"杭州嘿鱼电子科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000032076,\n" +
                "                    \"supplierId\":111128429,\n" +
                "                    \"supplierName\":\"杭州嘿鱼电子科技有限公司【Morrorart】【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000023937,\n" +
                "            \"mainSupplierName\":\"合肥悦盟网络科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031980,\n" +
                "                    \"supplierId\":111126626,\n" +
                "                    \"supplierName\":\"合肥悦盟网络科技有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000023937,\n" +
                "            \"mainSupplierName\":\"合肥悦盟网络科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031980,\n" +
                "                    \"supplierId\":111126626,\n" +
                "                    \"supplierName\":\"合肥悦盟网络科技有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500000002004201,\n" +
                "            \"mainSupplierName\":\"上海星茁国际贸易有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000043172,\n" +
                "                    \"supplierId\":111193453,\n" +
                "                    \"supplierName\":\"上海星茁国际贸易有限公司——美妆商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500000002004201,\n" +
                "            \"mainSupplierName\":\"上海星茁国际贸易有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000043172,\n" +
                "                    \"supplierId\":111193453,\n" +
                "                    \"supplierName\":\"上海星茁国际贸易有限公司——美妆商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500000002011456,\n" +
                "            \"mainSupplierName\":\"诸暨市卡拉美拉科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000043194,\n" +
                "                    \"supplierId\":111193661,\n" +
                "                    \"supplierName\":\"诸暨市卡拉美拉科技有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000009494,\n" +
                "            \"mainSupplierName\":\"广州旭威科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000028902,\n" +
                "                    \"supplierId\":111090357,\n" +
                "                    \"supplierName\":\"广州旭威科技有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000003673,\n" +
                "            \"mainSupplierName\":\"南昌良良实业有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000043207,\n" +
                "                    \"supplierId\":111193769,\n" +
                "                    \"supplierName\":\"南昌良良实业有限公司-童装商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000024352,\n" +
                "            \"mainSupplierName\":\"苏州贝昂科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000042245,\n" +
                "                    \"supplierId\":111184474,\n" +
                "                    \"supplierName\":\"苏州贝昂科技有限公司-寄售\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000024352,\n" +
                "            \"mainSupplierName\":\"苏州贝昂科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000042245,\n" +
                "                    \"supplierId\":111184474,\n" +
                "                    \"supplierName\":\"苏州贝昂科技有限公司-寄售\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000023605,\n" +
                "            \"mainSupplierName\":\"深圳帝浦电子有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031170,\n" +
                "                    \"supplierId\":111115558,\n" +
                "                    \"supplierName\":\"深圳帝浦电子有限公司】【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000023605,\n" +
                "            \"mainSupplierName\":\"深圳帝浦电子有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031170,\n" +
                "                    \"supplierId\":111115558,\n" +
                "                    \"supplierName\":\"深圳帝浦电子有限公司】【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000000812,\n" +
                "            \"mainSupplierName\":\"湖南御泥坊化妆品有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000011332,\n" +
                "                    \"supplierId\":110855024,\n" +
                "                    \"supplierName\":\"湖南御泥坊化妆品有限公司-寄售\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000000812,\n" +
                "            \"mainSupplierName\":\"湖南御泥坊化妆品有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000011332,\n" +
                "                    \"supplierId\":110855024,\n" +
                "                    \"supplierName\":\"湖南御泥坊化妆品有限公司-寄售\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000009555,\n" +
                "            \"mainSupplierName\":\"哥登宝母婴用品（上海）有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000043304,\n" +
                "                    \"supplierId\":111195335,\n" +
                "                    \"supplierName\":\"哥登宝母婴用品（上海）有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000029387,\n" +
                "            \"mainSupplierName\":\"江苏三创商贸有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000043138,\n" +
                "                    \"supplierId\":111192975,\n" +
                "                    \"supplierName\":\"江苏三创商贸有限公司--厨电寄售\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000023070,\n" +
                "            \"mainSupplierName\":\"东海县龙广水晶珠宝有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000046951,\n" +
                "                    \"supplierId\":111255119,\n" +
                "                    \"supplierName\":\"东海县龙广水晶珠宝有限公司-鲜花一盘货仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000022873,\n" +
                "            \"mainSupplierName\":\"台州万杰贸易有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000046948,\n" +
                "                    \"supplierId\":111255110,\n" +
                "                    \"supplierName\":\"台州万杰贸易有限公司-鲜花一盘货仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000030853,\n" +
                "            \"mainSupplierName\":\"上海优尚花卉有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000046946,\n" +
                "                    \"supplierId\":111255102,\n" +
                "                    \"supplierName\":\"上海优尚花卉有限公司-鲜花一盘货仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000018142,\n" +
                "            \"mainSupplierName\":\"虹越花卉股份有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000046945,\n" +
                "                    \"supplierId\":111255096,\n" +
                "                    \"supplierName\":\"虹越花卉股份有限公司-鲜花一盘货仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000030698,\n" +
                "            \"mainSupplierName\":\"深圳市米奇兰帝服饰有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000045774,\n" +
                "                    \"supplierId\":111236191,\n" +
                "                    \"supplierName\":\"深圳市米奇兰帝服饰有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000017789,\n" +
                "            \"mainSupplierName\":\"杭州小伙伴儿童用品有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000044890,\n" +
                "                    \"supplierId\":111229848,\n" +
                "                    \"supplierName\":\"杭州小伙伴儿童用品有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000030747,\n" +
                "            \"mainSupplierName\":\"南通古来品牌管理有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000044583,\n" +
                "                    \"supplierId\":111224410,\n" +
                "                    \"supplierName\":\"南通古来品牌管理有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000030559,\n" +
                "            \"mainSupplierName\":\"多点（湖州）网络科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000044536,\n" +
                "                    \"supplierId\":111224042,\n" +
                "                    \"supplierName\":\"多点（湖州）网络科技有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000001871,\n" +
                "            \"mainSupplierName\":\"广东小猪班纳服饰股份有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000044533,\n" +
                "                    \"supplierId\":111223480,\n" +
                "                    \"supplierName\":\"广东小猪班纳服饰股份有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000014595,\n" +
                "            \"mainSupplierName\":\"上海诗嫣贸易有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000044288,\n" +
                "                    \"supplierId\":111208962,\n" +
                "                    \"supplierName\":\"上海诗嫣贸易有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500000002009134,\n" +
                "            \"mainSupplierName\":\"北京三环优品商贸有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000044287,\n" +
                "                    \"supplierId\":111208961,\n" +
                "                    \"supplierName\":\"北京三环优品商贸有限公司-Shadez商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000030475,\n" +
                "            \"mainSupplierName\":\"杭州书礼贸易有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000044284,\n" +
                "                    \"supplierId\":111208955,\n" +
                "                    \"supplierName\":\"杭州书礼贸易有限公司-苪蒽商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000030475,\n" +
                "            \"mainSupplierName\":\"杭州书礼贸易有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000044283,\n" +
                "                    \"supplierId\":111208953,\n" +
                "                    \"supplierName\":\"杭州书礼贸易有限公司-史努比商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000030474,\n" +
                "            \"mainSupplierName\":\"义乌嘉睦服饰有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000044279,\n" +
                "                    \"supplierId\":111208942,\n" +
                "                    \"supplierName\":\"义乌嘉睦服饰有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000030423,\n" +
                "            \"mainSupplierName\":\"石狮猕梵贸易有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000044241,\n" +
                "                    \"supplierId\":111208586,\n" +
                "                    \"supplierName\":\"石狮猕梵贸易有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000030335,\n" +
                "            \"mainSupplierName\":\"杭州景澜电子商务有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000044227,\n" +
                "                    \"supplierId\":111208221,\n" +
                "                    \"supplierName\":\"杭州景澜电子商务有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000016416,\n" +
                "            \"mainSupplierName\":\"上海分尚网络科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000043780,\n" +
                "                    \"supplierId\":111201892,\n" +
                "                    \"supplierName\":\"上海分尚网络科技有限公司-园艺绿植-一盘货仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000023050,\n" +
                "            \"mainSupplierName\":\"泉州白又白文化科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000043777,\n" +
                "                    \"supplierId\":111201886,\n" +
                "                    \"supplierName\":\"泉州白又白文化科技有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000015608,\n" +
                "            \"mainSupplierName\":\"杭州蒂爱母婴用品有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000042154,\n" +
                "                    \"supplierId\":111183171,\n" +
                "                    \"supplierName\":\"杭州蒂爱母婴用品有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000023938,\n" +
                "            \"mainSupplierName\":\"杭州唯道科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000032196,\n" +
                "                    \"supplierId\":111130277,\n" +
                "                    \"supplierName\":\"杭州唯道科技有限公司-园艺-一盘货\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000023720,\n" +
                "            \"mainSupplierName\":\"山东特利电子商务有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031851,\n" +
                "                    \"supplierId\":111122939,\n" +
                "                    \"supplierName\":\"山东特利电子商务有限公司-园艺商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000022835,\n" +
                "            \"mainSupplierName\":\"广州景润园林绿化有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000031850,\n" +
                "                    \"supplierId\":111122938,\n" +
                "                    \"supplierName\":\"广州景润园林绿化有限公司-园艺商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500000002011587,\n" +
                "            \"mainSupplierName\":\"福建海文商贸有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000030779,\n" +
                "                    \"supplierId\":111111352,\n" +
                "                    \"supplierName\":\"福建海文商贸有限公司-北极绒商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500000002011570,\n" +
                "            \"mainSupplierName\":\"广州英氏电子商务有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000030380,\n" +
                "                    \"supplierId\":111106556,\n" +
                "                    \"supplierName\":\"英氏婴童用品有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000023124,\n" +
                "            \"mainSupplierName\":\"河北德沃多肥料有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000030165,\n" +
                "                    \"supplierId\":111105278,\n" +
                "                    \"supplierName\":\"河北德沃多肥料有限公司-一盘货直发\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000022152,\n" +
                "            \"mainSupplierName\":\"上海俊懿网络科技有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000028898,\n" +
                "                    \"supplierId\":111090313,\n" +
                "                    \"supplierName\":\"上海俊懿网络科技有限公司-母婴寄售\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000018332,\n" +
                "            \"mainSupplierName\":\"浦江牧牛商贸有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000025670,\n" +
                "                    \"supplierId\":111043728,\n" +
                "                    \"supplierName\":\"浦江牧牛商贸有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000008059,\n" +
                "            \"mainSupplierName\":\"上海超固日用品有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000047270,\n" +
                "                    \"supplierId\":111262408,\n" +
                "                    \"supplierName\":\"上海超固日用品有限公司-超级飞侠商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000018332,\n" +
                "            \"mainSupplierName\":\"浦江牧牛商贸有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000025670,\n" +
                "                    \"supplierId\":111043728,\n" +
                "                    \"supplierName\":\"浦江牧牛商贸有限公司-商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000009514,\n" +
                "            \"mainSupplierName\":\"好孩子（中国）零售服务有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000046720,\n" +
                "                    \"supplierId\":111251161,\n" +
                "                    \"supplierName\":\"好孩子（中国）零售服务有限公司-好孩子童装商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000009514,\n" +
                "            \"mainSupplierName\":\"好孩子（中国）零售服务有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000046720,\n" +
                "                    \"supplierId\":111251163,\n" +
                "                    \"supplierName\":\"好孩子（中国）零售服务有限公司-好孩子童鞋商家仓\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"mainSupplierId\":500001000015227,\n" +
                "            \"mainSupplierName\":\"网易有道信息技术（北京）有限公司\",\n" +
                "            \"supplierSubDTOList\":[\n" +
                "                {\n" +
                "                    \"memberId\":1000000000023190,\n" +
                "                    \"supplierId\":111002765,\n" +
                "                    \"supplierName\":\"网易有道信息技术（北京）有限公司【3C】【寄售】\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }]";
        List<JsonMain> jsonMains = JSONObject.parseArray(a, JsonMain.class);
        System.out.println(jsonMains.size());

    }
}
