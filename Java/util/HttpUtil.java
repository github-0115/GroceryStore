package uyun.show.server.domain.util;

import org.apache.http.entity.mime.content.StringBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import uyun.show.server.domain.exception.ProcessFailException;

import javax.net.ssl.SSLContext;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public static String get(String url) throws Exception {
        return get(url, createHeader());
    }

    public static String get(String url, Map<String, String> headers) throws Exception {
        return get(url, null, headers);
    }

    public static String get(String url, String cookie, Map<String, String> headers)
            throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        if (url.startsWith("https")) {
            httpClient = createSSLInsecureClient();
        }
        String result = null;
        HttpGet request = new HttpGet(url);

        if (headers != null) {
            for (Entry<String, String> entry : headers.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        }

        if (cookie != null) {
            request.setHeader("Cookie", cookie);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("executing request to " + url);
        }
        HttpResponse httpResponse = httpClient.execute(request);
        HttpEntity entity = httpResponse.getEntity();

        if (entity != null) {
            result = EntityUtils.toString(entity, "utf-8");
        }

        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (logger.isDebugEnabled()) {
            logger.debug("executing request result :{}", result);
        }
        if (200 != statusCode) {
            if (!result.contains("选择的过滤资源不存在")) {
                logger.warn("请求返回CODE：" + statusCode + "返回数据：" + result);
            }
            return "";
        }

        return result;
    }

    public static Map<String, String> getstore(String url) throws Exception {
        return getstore(url, createHeader());
    }

    public static Map<String, String> getstore(String url, Map<String, String> headers) throws Exception {
        return getstore(url, null, headers);
    }

    public static Map<String, String> getstore(String url, String cookie, Map<String, String> headers)
            throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        if (url.startsWith("https")) {
            httpClient = createSSLInsecureClient();
        }
        String result = null;
        HttpGet request = new HttpGet(url);

        if (headers != null) {
            for (Entry<String, String> entry : headers.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        }

        if (cookie != null) {
            request.setHeader("Cookie", cookie);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("executing request to " + url);
        }
        Map<String, String> resultMap = new HashMap<>();

        try {
            HttpResponse httpResponse = httpClient.execute(request);
            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {
                result = EntityUtils.toString(entity, "utf-8");
            }

            resultMap.put("data", result);

            int statusCode = httpResponse.getStatusLine().getStatusCode();
            resultMap.put("statusCode", "" + statusCode);

            if (logger.isDebugEnabled()) {
                logger.debug("executing request result :{}", result);
            }

            if (200 != statusCode) {
                if (!result.contains("选择的过滤资源不存在")) {
                    logger.warn("请求返回CODE：" + statusCode + "返回数据：" + result);
                }
                return resultMap;
            }
        } catch (Exception e) {

        }

        return resultMap;
    }

    public static String post(String url, Map<String, String> headers, String jsonString)
            throws Exception {
        return post(url, null, headers, jsonString);
    }

    public static String post(String url, String jsonString)
            throws Exception {
        return post(url, createHeader(), jsonString);
    }

    private static Map<String, String> createHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json;charset=utf-8");
        headers.put("Content-Type", "application/json;charset=utf-8");
        return headers;
    }

    public static String post(String url, String cookie, Map<String, String> headers, String jsonString)
            throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = null;

        if (url.startsWith("https")) {
            httpClient = createSSLInsecureClient();
        }

        HttpPost postRequest = new HttpPost(url);
        if (headers != null) {
            for (Entry<String, String> entry : headers.entrySet()) {
                postRequest.setHeader(entry.getKey(), entry.getValue());
            }
        }
        if (cookie != null) {
            postRequest.setHeader("Cookie", cookie);
        }

        if (!StringUtils.isBlank(jsonString)) {
            StringEntity entity = new StringEntity(jsonString, "utf-8");
            postRequest.setEntity(entity);
        }

        try {
            HttpResponse httpResponse = httpClient.execute(postRequest);
            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {
                result = EntityUtils.toString(entity);
            }

            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (200 != statusCode) {
                if (!result.contains("选择的过滤资源不存在")) {
                    logger.warn("请求返回CODE：" + statusCode + "返回数据：" + result);
                }
                return result;
            }
        } catch (Exception e) {

        }

        return result;
    }

    public static Map<String, String> poststore(String url, String jsonString)
            throws Exception {
        return poststore(url, createHeader(), jsonString);
    }

    public static Map<String, String> poststore(String url, Map<String, String> headers, String jsonString)
            throws Exception {
        return poststore(url, null, headers, jsonString);
    }

    public static Map<String, String> poststore(String url, String cookie, Map<String, String> headers, String jsonString) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = null;

        Map<String, String> resultMap = new HashMap<>();

        if (url.startsWith("https")) {
            httpClient = createSSLInsecureClient();
        }

        HttpPost postRequest = new HttpPost(url);
        if (headers != null) {
            for (Entry<String, String> entry : headers.entrySet()) {
                postRequest.setHeader(entry.getKey(), entry.getValue());
            }
        }
        if (cookie != null) {
            postRequest.setHeader("Cookie", cookie);
        }

        if (!StringUtils.isBlank(jsonString)) {
            StringEntity entity = new StringEntity(jsonString, "utf-8");
            postRequest.setEntity(entity);
        }

        try {
            HttpResponse httpResponse = httpClient.execute(postRequest);
            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
            resultMap.put("data", result);

            int statusCode = httpResponse.getStatusLine().getStatusCode();
            resultMap.put("statusCode", "" + statusCode);

        } catch (Exception e) {

        }

        return resultMap;
    }

    private static CloseableHttpClient createSSLInsecureClient() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> {
                return true; //信任所有
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (Exception e) {
            logger.warn("CertificateException", e);
        }
        return HttpClients.createDefault();
    }

    public static String upload(String url, String filename, Map<String, String> params) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String result = null;
        try {
//            CloseableHttpClient httpclient = HttpClients.createDefault();

            if (url.startsWith("https")) {
                httpclient = createSSLInsecureClient();
            }

            HttpPost postRequest = new HttpPost(url);
            postRequest.setHeader("apikey", params.get("apikey"));

            FileBody bin = new FileBody(new File(filename));
            HttpEntity reqEntity = MultipartEntityBuilder.create().
                    addPart("file", bin).
                    addPart("tags", new StringBody(params.get("tags"))).
                    addPart("flag", new StringBody(params.get("flag"))).
                    addPart("apikey", new StringBody(params.get("apikey"))).
                    build();
            postRequest.setEntity(reqEntity);

            CloseableHttpResponse response = httpclient.execute(postRequest);
            try {
                System.out.println(response.getStatusLine());
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity);
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
