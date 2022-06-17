package com.example.demo.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author: 茹凌丰
 * @date: 2022/6/17
 * @description:
 */
public class Base64Utils {

    /**
     * base64解码
     * @param args
     * @throws UnsupportedEncodingException
     * @throws DocumentException
     */
    public static void main(String[] args) throws UnsupportedEncodingException, DocumentException {
        String a  = "PE1TRz48TWVzc2FnZT48VHJ4UmVzcG9uc2U%2BPFJldHVybkNvZGU%2BMDAwMDwvUmV0dXJuQ29kZT48RXJyb3JNZXNzYWdlPlN1Y2Nlc3M8L0Vycm9yTWVzc2FnZT48RUNNZXJjaGFudFR5cGU%2BRUJVUzwvRUNNZXJjaGFudFR5cGU%2BPE1lcmNoYW50SUQ%2BMTAzODgxOTI5OTk0MDM2PC9NZXJjaGFudElEPjxUcnhUeXBlPlJlY3ZRUlBheVJlc3VsdDwvVHJ4VHlwZT48T3JkZXJObz43NDQyMTUzNTY0Njk0MTE4NDE8L09yZGVyTm8%2BPEFtb3VudD4wLjAxPC9BbW91bnQ%2BPEJhdGNoTm8%2BMDAwMDE2PC9CYXRjaE5vPjxWb3VjaGVyTm8%2BMDAxNTQ0PC9Wb3VjaGVyTm8%2BPEhvc3REYXRlPjIwMjIvMDYvMTY8L0hvc3REYXRlPjxIb3N0VGltZT4xNToyNjoxNzwvSG9zdFRpbWU%2BPFBheVR5cGU%2BRVAxMzk8L1BheVR5cGU%2BPE5vdGlmeVR5cGU%2BMTwvTm90aWZ5VHlwZT48UGF5SVA%2BMTIzLjE1OS4xOTcuODI8L1BheUlQPjxpUnNwUmVmPjZHRUNFUDAxMTUyMDA0MzU1OTE0PC9pUnNwUmVmPjxBY2NEYXRlPjIwMjIwNjE2PC9BY2NEYXRlPjxPcGVuSUQ%2Bb3RETm90MlMwMURaT1Q4ZjBRZEtjcWJydXRIODwvT3BlbklEPjxiYW5rX3R5cGU%2BT1RIRVJTPC9iYW5rX3R5cGU%2BPFRoaXJkT3JkZXJObz4xMDM4ODE5Mjk5OTQwMzY1MzY0MzY5NzY0OTA2OTczOTwvVGhpcmRPcmRlck5vPjwvVHJ4UmVzcG9uc2U%2BPC9NZXNzYWdlPjxTaWduYXR1cmUtQWxnb3JpdGhtPlNIQTF3aXRoUlNBPC9TaWduYXR1cmUtQWxnb3JpdGhtPjxTaWduYXR1cmU%2BNU05VEljbHNMV0pQc1I5T21tdUd3eXlRbHRTS3g4T3dNRTREUWFNeGc0bkw3ZERUNmhlMUgyZ0JFeEEvUm9zN0VyM2xPV2JXTEZyWUhxOS9RT1JnVktWMWlLQ1RXeEJza0ZSQ3R4U2g5MVNqSm1xQjBBVGo1V2NGQWdzeExZYnBoNDFDQUhrSnA0WXI1L2tvcjNIdm5oTE0xVCtIZUdaU0dNekNTaTQyUmxjPTwvU2lnbmF0dXJlPjwvTVNHPg%3D%3D";
        //因为有%2B代表+ %3D代表=  要写把这些转成+或者=,在转
        String res = java.net.URLDecoder.decode(a, "UTF-8");
        //base64转xml
        String fromBASE64 = getFromBASE64(res);

        JSONObject json = new JSONObject();
        //xml转jsonObject
        Document doc = DocumentHelper.parseText(fromBASE64);
        dom4j2Json(doc.getRootElement(), json);
        System.out.println(json);
    }

    public static String getFromBASE64(String s) {
        if (s == null)
            return null;
        sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
        try {
            byte[] b = decoder.decodeBuffer(s);
            return new String(b);
        } catch (Exception e) {
            return null;
        }
    }

    public static void dom4j2Json(Element element, JSONObject json) {
        // 如果是属性
        for (Object o : element.attributes()) {
            Attribute attr = (Attribute) o;
            if (StringUtils.isNotEmpty(attr.getValue())) {
                json.put("@" + attr.getName(), attr.getValue());
            }
        }
        List<Element> chdEl = element.elements();
        if (chdEl.isEmpty() && StringUtils.isNotEmpty(element.getText())) {// 如果没有子元素,只有一个值
            json.put(element.getName(), element.getText());
        }

        for (Element e : chdEl) {// 有子元素
            if (!e.elements().isEmpty()) {// 子元素也有子元素
                JSONObject chdjson = new JSONObject();
                dom4j2Json(e, chdjson);
                Object o = json.get(e.getName());
                if (o != null) {
                    JSONArray jsona = null;
                    if (o instanceof JSONObject) {// 如果此元素已存在,则转为jsonArray
                        JSONObject jsono = (JSONObject) o;
                        json.remove(e.getName());
                        jsona = new JSONArray();
                        jsona.add(jsono);
                        jsona.add(chdjson);
                    }
                    if (o instanceof JSONArray) {
                        jsona = (JSONArray) o;
                        jsona.add(chdjson);
                    }
                    json.put(e.getName(), jsona);
                } else {
                    if (!chdjson.isEmpty()) {
                        json.put(e.getName(), chdjson);
                    }
                }

            } else {// 子元素没有子元素
                for (Object o : element.attributes()) {
                    Attribute attr = (Attribute) o;
                    if (StringUtils.isNotEmpty(attr.getValue())) {
                        json.put("@" + attr.getName(), attr.getValue());
                    }
                }
                if (!e.getText().isEmpty()) {
                    json.put(e.getName(), e.getText());
                }
            }
        }
    }
}
