package com.example.demo.common;

import com.google.common.net.MediaType;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.Iterator;
import java.util.Map;

/**
 * @Description:
 * @Author: RU
 * @Date: 2020/4/29 17:26
 */
@Slf4j
public class RlfHttpUtils {

    public static String get(String url, Map<String, Object> queries) {
        String responseBody = "";
        StringBuffer sb = new StringBuffer(url);
        if (queries != null && queries.keySet().size() > 0) {
            boolean firstFlag = true;
            Iterator iterator = queries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry<String, String>) iterator.next();
                if (firstFlag) {
                    sb.append("?" + entry.getKey() + "=" + entry.getValue());
                    firstFlag = false;
                } else {
                    sb.append("&" + entry.getKey() + "=" + entry.getValue());
                }
            }
        }
        Request request = new Request.Builder().url(sb.toString()).build();
        Response response = null;
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            response = okHttpClient.newCall(request).execute();
            int status = response.code();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            log.error("okhttp3 put error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return responseBody;
    }

    public static Response getResponse(String url, Map<String, Object> queries) {
        Response responseBody = null;
        StringBuffer sb = new StringBuffer(url);
        if (queries != null && queries.keySet().size() > 0) {
            boolean firstFlag = true;
            Iterator iterator = queries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry<String, String>) iterator.next();
                if (firstFlag) {
                    sb.append("?" + entry.getKey() + "=" + entry.getValue());
                    firstFlag = false;
                } else {
                    sb.append("&" + entry.getKey() + "=" + entry.getValue());
                }
            }
        }
        Request request = new Request.Builder().url(sb.toString()).build();
        Response response = null;
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            response = okHttpClient.newCall(request).execute();
            int status = response.code();
            if (response.isSuccessful()) {
                return response;
            }
        } catch (Exception e) {
            log.error("okhttp3 put error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return responseBody;
    }

    /**
     * POST
     *
     * @param url
     * @param params
     * @param keyHeader   请求头键
     * @param valueHeader 请求头值
     * @return
     */
    public static String post(String url, Map<String, String> params, String keyHeader, String valueHeader) {
        String responseBody = "";
        FormBody.Builder builder = new FormBody.Builder();
        // 添加参数
        if (params != null && params.keySet().size() > 0) {
            for (String key : params.keySet()) {
                builder.add(key, params.get(key));
            }
        }
        Request request = new Request.Builder().addHeader(keyHeader, valueHeader).url(url).post(builder.build())
                .build();
        Response response = null;
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            log.error("okhttp3 post error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return responseBody;
    }

    /**
     * GET
     *
     * @param url
     * @param params
     * @param keyHeader   请求头键
     * @param valueHeader 请求头值
     * @return
     */
    public static String getForHeader(String url, Map<String, Object> params, String keyHeader, String valueHeader) {
        String responseBody = "";
        StringBuffer sb = new StringBuffer(url);
        if (params != null && params.keySet().size() > 0) {
            boolean firstFlag = true;
            Iterator iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry<String, String>) iterator.next();
                if (firstFlag) {
                    sb.append("?" + entry.getKey() + "=" + entry.getValue());
                    firstFlag = false;
                } else {
                    sb.append("&" + entry.getKey() + "=" + entry.getValue());
                }
            }
        }
        Request request = new Request.Builder().addHeader(keyHeader, valueHeader).url(sb.toString()).build();
        Response response = null;
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            log.error("okhttp3 put error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return responseBody;
    }

    /**
     * Post请求发送JSON数据....{"name":"zhangsan","pwd":"123456"}
     * 参数一：请求Url
     * 参数二：请求的JSON
     * 参数三：请求回调
     */
    public static String postJsonParams(String url, String jsonParams) {
        String responseBody = "";
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonParams);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = null;
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            response = okHttpClient.newCall(request).execute();
            int status = response.code();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            log.error("okhttp3 post error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return responseBody;
    }

    /**
     * POST发送JSON数据
     *
     * @param url
     * @param jsonParams
     * @param keyHeader   请求头键
     * @param valueHeader 请求头值
     * @return
     */
    public static String postJsonParams(String url, String jsonParams, String keyHeader, String valueHeader) {
        String responseBody = "";
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonParams);
        Request request = new Request.Builder().url(url).addHeader(keyHeader, valueHeader).post(requestBody).build();
        Response response = null;
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            log.error("okhttp3 post error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return responseBody;
    }

    private static FormBody getFormBody(Map<String, String> jsonParams) {
        String address = jsonParams.get("address");
        if (StringUtils.isEmpty(address)) {
            return new FormBody.Builder().add("dorm_code", (String) jsonParams.get("dorm_code"))
                    .add("action", (String) jsonParams.get("action")).build();
        } else {
            return new FormBody.Builder().add("dorm_code", (String) jsonParams.get("dorm_code"))
                    .add("action", (String) jsonParams.get("action")).add("address", (String) jsonParams.get("address"))
                    .build();
        }
    }

    public static Response postJsonParamsResponse(String url, Map<String, String> jsonParams) {
        Response responseBody = null;
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/x-www-form-urlencoded"),
                jsonParams.toString());
        FormBody formBody = getFormBody(jsonParams);
        Request request = new Request.Builder().url(url).post(formBody).build();
        Response response = null;
        try {
            OkHttpClient okHttpClient = new OkHttpClient();

            okHttpClient.newBuilder().followRedirects(false).followSslRedirects(false).build();

            response = okHttpClient.newCall(request).execute();
            int status = response.code();
            if (response.isSuccessful()) {
                return response;
            }
        } catch (Exception e) {
            log.error("okhttp3 post error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return responseBody;
    }

    public static String postJsonParamsForIpCast(String url, String jsonParams) {
        String responseBody = "";
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonParams);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Response response = null;
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            response = okHttpClient.newCall(request).execute();
            int status = response.code();
            if (response.isSuccessful()) {
                byte[] b = response.body().bytes(); // 获取数据的bytes
                String info = new String(b, "GB2312"); // 然后将其转为gb2312
                return info;
                // return response.body().string();
            }
        } catch (Exception e) {
            log.error("okhttp3 post error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return responseBody;
    }

    /**
     * Post请求发送xml数据.... 参数一：请求Url 参数二：请求的xmlString 参数三：请求回调
     */
    public static String postXmlParams(String url, String xml) {
        String responseBody = "";
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/xml; charset=utf-8"), xml);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Response response = null;
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            response = okHttpClient.newCall(request).execute();
            int status = response.code();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            log.error("okhttp3 post error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return responseBody;
    }

    /**
     * DELETE
     *
     * @param url
     * @param keyHeader   请求头键
     * @param valueHeader 请求头值
     * @return
     */
    public static String delete(String url, String keyHeader, String valueHeader) {
        String responseBody = "";
        Request request = new Request.Builder().url(url).addHeader(keyHeader, valueHeader).delete().build();
        Response response = null;
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            log.error("okhttp3 put error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return responseBody;
    }
}
