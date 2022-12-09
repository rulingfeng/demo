package com.example.demo.common;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yingyongzhi
 */
@Slf4j
public final class HttpUtil {

    public static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);

    private static CloseableHttpClient httpclient;
    private static Header DEFAULT_HEADER = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    public final static int CONNECT_TIMEOUT = 30000;

    static {
        SSLContextBuilder builder = new SSLContextBuilder();
        SSLConnectionSocketFactory sslConnectionSocketFactory = null;
        try {
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", new PlainConnectionSocketFactory())
                .register("https", sslConnectionSocketFactory)
                .build();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(40);

        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(CONNECT_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setConnectionRequestTimeout(CONNECT_TIMEOUT).build();
        /**
         * 测出超时重试机制为了防止超时不生效而设置
         *  如果直接放回false,不重试
         *  这里会根据情况进行判断是否重试
         */
        HttpRequestRetryHandler retry = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (executionCount >= 3) {// 如果已经重试了3次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return true;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// ssl握手异常
                    return false;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };
        httpclient = HttpClients.custom()
                // 把请求相关的超时信息设置到连接客户端
                .setDefaultRequestConfig(requestConfig)
                // 把请求重试设置到连接客户端
                .setRetryHandler(retry)
                // 配置连接池管理对象
                .setConnectionManager(cm)
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .setDefaultCookieStore(new BasicCookieStore())
                .build();
    }

    public static String doPostJsonString(String url, String params) {
        return doPostJsonString(url, params, null);
    }

    public static String doPostJsonString(String url, String params, Header[] headers) {
        log.debug("doPostJsonString.url:{},params:{}",url,params);
        CloseableHttpResponse response = null;
        String responseString = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            StringEntity entity = new StringEntity(params, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            httpPost.setHeaders(headers);
            response = httpclient.execute(httpPost);
            log.debug("response.status:{}",response.getStatusLine());
            if (response.getStatusLine().getStatusCode() == 200) {
                responseString = EntityUtils.toString(response.getEntity(), "utf-8");
            } else {
                log.warn("[HttpUtils doPostJsonString] 请求失败 code!=200 {} {} {}", url, params, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.debug("responseString:{}",responseString);
        return responseString;
    }

    public static String post(String url, List<NameValuePair> params) {
        log.debug("post.url:{},params:{}",url,JSON.toJSONString(params));
        String responseString = null;
        HttpPost post = new HttpPost(url.trim());
        try {
            post.addHeader(DEFAULT_HEADER);

            if (params != null && params.size() > 0) {
                post.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
            }
            long s1 = System.currentTimeMillis();
            CloseableHttpResponse response = httpclient.execute(post);
            log.debug("response.status:{}",response.getStatusLine());
            long s2 = System.currentTimeMillis();
            try {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    post.abort();
                    log.warn(
                            "[HttpUtils Post] error, url : {}  , params : {},  status :{}",
                            url, params, statusCode);
                    return "";
                }

                HttpEntity entity = response.getEntity();
                try {
                    if (entity != null) {
                        responseString = EntityUtils.toString(entity, Consts.UTF_8);
                        log.debug(
                                "[HttpUtils Post]Debug response, url : {}  , params : {}, response string : {} ,time : {}",
                                url, params, responseString, s2 - s1);
                        return responseString;
                    }
                } finally {
                    if (entity != null) {
                        entity.getContent().close();
                    }
                }
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        } catch (Exception e) {
            log.warn(
                    "[HttpUtils Post] error, url : {}  , params : {}, response string : {} ,error : {}",
                    url, params, "", e.getMessage(), e);
        } finally {
            post.releaseConnection();
        }
        log.debug("responseString:{}",responseString);
        return responseString;
    }

    public static String get(String url, List<NameValuePair> params) {
        log.debug("get:url:{},params:{}",url,JSON.toJSONString(params));
        String responseString = null;
        StringBuilder sb = new StringBuilder();
        sb.append(url.trim());
        int i = 0;
        if (params != null && params.size() > 0) {
            for (NameValuePair entry : params) {
                if (i == 0 && !url.contains("?")) {
                    sb.append("?");
                } else {
                    sb.append("&");
                }
                sb.append(entry.getName());
                sb.append("=");
                String value = entry.getValue();
                try {
                    sb.append(URLEncoder.encode(value, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    log.warn("encode http get params error, value is {}",
                            value, e);
                    sb.append(URLEncoder.encode(value));
                }
                i++;
            }
        }

        log.debug("[HttpUtils Get] begin invoke:" + sb.toString());
        HttpGet get = new HttpGet(sb.toString());
        get.addHeader(DEFAULT_HEADER);

        try {
            CloseableHttpResponse response = httpclient.execute(get);
            log.debug("response:status:{}",response.getStatusLine());
            try {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    get.abort();
                    log.warn("[HttpUtils Get] 请求失败 code!=200 {} {} {}", url, params, response);
                    return "";
                }
                HttpEntity entity = response.getEntity();
                try {
                    if (entity != null) {
                        responseString = EntityUtils.toString(entity);
                    }
                } finally {
                    if (entity != null) {
                        entity.getContent().close();
                    }
                }
            } catch (Exception e) {
                log.warn("[HttpUtils Get]get response error, url:{}", url, e);
                return responseString;
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        } catch (Exception e) {
            log.warn("[HttpUtils Get]get execute error, url:{}", url, e);
        } finally {
            get.releaseConnection();
        }
        log.debug("responseString:{}",responseString);
        return responseString;
    }

    public static String postJson(String url,JSON params) {
        log.debug("postJson.url:{},params:{}",url,params.toJSONString());
        String responseString = null;
        HttpPost post = new HttpPost(url.trim());
        post.setEntity(new StringEntity(params.toJSONString(),
                ContentType.APPLICATION_JSON));
        post.addHeader(DEFAULT_HEADER);
        try {
            CloseableHttpResponse response = httpclient.execute(post);
            log.debug("response.status:{}",response.getStatusLine());
            try {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    post.abort();
                    return "";
                }

                HttpEntity entity = response.getEntity();
                try {
                    if (entity != null) {
                        responseString = EntityUtils.toString(entity);
                    }
                } finally {
                    if (entity != null) {
                        entity.getContent().close();
                    }
                }
            } catch (Exception e) {
                log.warn("[HttpUtils PostJson]post response error, url:{}", url, e);
                return responseString;
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        } catch (Exception e) {
            log.warn("[HttpUtils PostJson]post execute error, url:{}", url, e);
        } finally {
            post.releaseConnection();
        }
        log.debug("responseString:{}",responseString);
        return responseString;
    }

    private static final RequestConfig CONFIG = RequestConfig.custom().setConnectTimeout(30000).setSocketTimeout(50000)
            .build();

    private static List<Integer> httpOkCode=new ArrayList<>(10);

    static{
        httpOkCode.add( HttpStatus.SC_OK );
        httpOkCode.add( HttpStatus.SC_CREATED );
        httpOkCode.add( HttpStatus.SC_ACCEPTED );
        httpOkCode.add( HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION );
        httpOkCode.add( HttpStatus.SC_NO_CONTENT );
        httpOkCode.add( HttpStatus.SC_RESET_CONTENT );
        httpOkCode.add( HttpStatus.SC_PARTIAL_CONTENT );
        httpOkCode.add( HttpStatus.SC_MULTI_STATUS );
    }


    /**
     * 执行一个HTTP POST请求，返回请求响应的HTML
     *
     * @param url 请求的URL地址
     * @return 返回请求响应的HTML
     * @throws IOException
     */
    public static String doPost(String url, Map<String, Object> params){
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        if (params != null) {
            StringEntity entity = new StringEntity(JSON.toJSONString(params), Charset.forName("UTF-8"));
            httpPost.setEntity(entity);
        }
        httpPost.setHeader("Content-type", "application/json; charset=utf-8");

        try {
            HttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK
                    || httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
                HttpEntity responseEntityentity = httpResponse.getEntity();
                return EntityUtils.toString(responseEntityentity, "utf-8");
            } else {
                LOGGER.info("HTTP接口调用状态码为{},非200、非201，调用接口:{}\n参数:{}\n", httpResponse.getStatusLine().getStatusCode(), url, params);
            }
        }catch (Exception e){}finally {
            if (client != null) {
                try {
                    client.close();
                }catch (Exception e){

                }

            }
        }
        return "";
    }
}
