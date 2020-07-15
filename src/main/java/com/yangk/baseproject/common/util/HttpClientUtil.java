package com.yangk.baseproject.common.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description 通过httpclient调用接口工具类
 * @Author yangkun
 * @Date 2020/3/5
 * @Version 1.0
 * @blame yangkun
 */
@Slf4j
public final class HttpClientUtil {

    private static final String DEFAULT_CHARSET = "UTF-8";
    public static final String CONTENT_TYPE_URLENCODED = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_JSON = "application/json";
    /**
     * setConnectTimeout：设置连接超时时间，单位毫秒
     * setConnectionRequestTimeout：设置从connect Manager获取Connection 超时时间，单位毫秒
     * setSocketTimeout：请求获取数据的超时时间，单位毫秒
     */
    private static RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(1000)
            .setConnectionRequestTimeout(12000).build();

    private HttpClientUtil() {
    }

    /**
     * @Description 以x-www-form-urlencoded的方式进行post请求
     * @Author yangkun
     * @Date 2020/4/3
     * @Param [url, maps]
     * @Return
     **/
    public static String sendPostByUrlencoded(String url, Map<String, Object> maps) {
        String result = "";
        try {
            //装填参数
            List<NameValuePair> nvps = new ArrayList<>();
            if (maps != null) {
                for (Map.Entry<String, Object> entry : maps.entrySet()) {
                    nvps.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
                }
            }
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, DEFAULT_CHARSET));
            httpPost.setHeader("Content-type", CONTENT_TYPE_URLENCODED);
            // httpPost.setHeader("UserMapper-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            httpPost.setConfig(requestConfig);
            result = sendPost(httpPost, JSON.toJSONString(maps));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @Description 以json方式进行post请求
     * @Author yangkun
     * @Date 2020/4/3
     * @Param [url, json]
     * @Return
     **/
    public static String sendPostByJson(String url, String json) {
        StringEntity requestEntity = new StringEntity(json, DEFAULT_CHARSET);
        requestEntity.setContentEncoding(DEFAULT_CHARSET);
        requestEntity.setContentType(CONTENT_TYPE_JSON);

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(requestEntity);
        httpPost.setConfig(requestConfig);
        return sendPost(httpPost, json);
    }

    private static String sendPost(HttpPost httpPost, String json) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        String result = "";
        try {
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity, DEFAULT_CHARSET);
            }
        } catch (Exception e) {
            log.error("sendPostByJson execute fail,url:{}, params: {}",httpPost.getURI(), json, e);
        } finally {
            closeResouce(httpClient, response);
        }
        return result;
    }

    /**
     * @Description 发送get请求
     * @Author yangkun
     * @Date 2020/4/3
     * @Param [httpUrl]
     * @Return
     **/
    public static String sendGet(String httpUrl) {
        HttpGet httpGet = new HttpGet(httpUrl);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String result = null;
        try {
            httpClient = HttpClients.createDefault();
            httpGet.setConfig(requestConfig);

            response = httpClient.execute(httpGet);
            entity = response.getEntity();
            result = EntityUtils.toString(entity, DEFAULT_CHARSET);
        } catch (IOException e) {
            log.error("sendHttpGet execute fail url:{}", httpGet.getURI(), e);
        } finally {
            closeResouce(httpClient, response);
        }
        return result;
    }

    private static void closeResouce(CloseableHttpClient httpClient, CloseableHttpResponse response) {
        try {
            if (response != null) {
                response.close();
            }
            if (httpClient != null) {
                httpClient.close();
            }
        } catch (IOException e) {
            log.error("closeResouce fail! {}", e);
        }
    }

    public static String postFormSession(String url, String sessionId, Map<String, String> maps) {
        CloseableHttpClient httpClient =  null;
        CloseableHttpResponse res = null;
        try {
            if (maps != null && !maps.isEmpty()) {
                int index = 0;
                for (String key : maps.keySet()) {
                    url = url+(index == 0 && !url.contains("?")?"?":"&")+key+"="+maps.get(key);
                    index++;
                }
            }
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "x-www-form-urlencoded; charset=utf-8");

            BasicClientCookie cookie = new BasicClientCookie("SESSION", sessionId);
            cookie.setVersion(0);
            String domain = url.substring(url.indexOf("/")+2);
            domain = domain.substring(0,domain.indexOf("/"));
            cookie.setDomain(domain);
            BasicCookieStore cookieStore = new BasicCookieStore();
            cookieStore.addCookie(cookie);
            httpClient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();

            res = httpClient.execute(httpPost);
            int statuts_codes = res.getStatusLine().getStatusCode();
            if (statuts_codes == HttpStatus.SC_OK) {//请求成功
                return EntityUtils.toString(res.getEntity(), "utf-8");//返回值
            }
            log.error("Http请求失败，响应码={}，url={}", statuts_codes, url);
        } catch (IOException e) {
            log.error("Http请求失败："+url, e);
        } finally {
            closeResouce(httpClient,res);
        }
        return null;
    }
    public static String postJSONSession(String url, String sessionId, String jsonParam) {
        CloseableHttpClient httpClient =  null;
        CloseableHttpResponse res = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json; charset=utf-8");
            if (StringUtils.isNotEmpty(jsonParam)) {
                httpPost.setEntity(new StringEntity(jsonParam, Charset.forName(DEFAULT_CHARSET)));
            }

            BasicClientCookie cookie = new BasicClientCookie("SESSION", sessionId);
            cookie.setVersion(0);
            String domain = url.substring(url.indexOf("/")+2);
            domain = domain.substring(0,domain.indexOf("/"));
            cookie.setDomain(domain);
            BasicCookieStore cookieStore = new BasicCookieStore();
            cookieStore.addCookie(cookie);
            httpClient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();

            res = httpClient.execute(httpPost);
            int statusCode = res.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                return EntityUtils.toString(res.getEntity(), DEFAULT_CHARSET);
            }
            log.error("Http请求失败，响应码={}，url={}", statusCode, url);
        } catch (IOException e) {
            log.error("Http请求失败："+url, e);
        } finally {
            closeResouce(httpClient,res);
        }
        return null;
    }

    public static void main(String[] args) {
        String s = sendPostByJson("http://ussfoa-gds-uat.us.i4px.com/app/parcel/unShelf", "{\n" +
                "    \"fpxTrackingNo\": \"200001913100\",\n" +
                "    \"operatorId\": \"S16003\",\n" +
                "    \"operatorName\": \"yangk\",\n" +
                "    \"requestTime\": 1570868308313,\n" +
                "    \"warehouseCode\": \"AUPERA\"\n" +
                "}");
        System.out.println(s);
    }

}
