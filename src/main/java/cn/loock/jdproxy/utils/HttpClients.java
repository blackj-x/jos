package cn.loock.jdproxy.utils;

import cn.loock.jdproxy.exception.HttpClientException;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class HttpClients implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClients.class);
    private CloseableHttpClient basicClient = null;
    private RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();

    public String get(String uri) {
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setConfig(requestConfig);
        try {
            return basicClient.execute(httpGet, getResponseHandler());
        } catch (IOException e) {
            LOGGER.error("", e);
            throw new HttpClientException(uri, e);
        }
    }

    public String get(String uri, Map<String, Object> parameters) {

        StringBuilder paramStr = new StringBuilder();
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            paramStr.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        String url = uri + "?" + paramStr.toString();
        return get(url);
    }

    public String post(String uri, String postParams, Map<String, Object> headers) {
        try {
            HttpPost httpPost = new HttpPost(uri);
            if (headers != null) {
                for (String key : headers.keySet()) {
                    httpPost.setHeader(key, headers.get(key).toString());
                }
            }
            if (!TextUtils.isEmpty(postParams)) {
                StringEntity entity = new StringEntity(postParams, Consts.UTF_8);
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
            }
            httpPost.setConfig(requestConfig);
            return basicClient.execute(httpPost, getResponseHandler());
        } catch (IOException e) {
            LOGGER.error("", e);
            throw new HttpClientException(uri, e);
        }
    }

    public String post(String uri, Map<String, Object> parameters) {
        return post(uri, parameters, null);
    }

    public String post(String uri, Map<String, Object> parameters, Map<String, Object> headers) {
        HttpPost httpPost = new HttpPost(uri);
        List<NameValuePair> formParams = new ArrayList<>();
        if (parameters != null) {
            for (String key : parameters.keySet()) {
                formParams.add(new BasicNameValuePair(key, parameters.get(key).toString()));
            }
        }
        if (headers != null) {
            for (String key : headers.keySet()) {
                httpPost.setHeader(key, headers.get(key).toString());

            }
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
        httpPost.setEntity(entity);
        httpPost.setConfig(requestConfig);
        try {
            return basicClient.execute(httpPost, getResponseHandler());
        } catch (IOException e) {
            LOGGER.error("", e);
            throw new HttpClientException(uri, e);
        }
    }

    public String postByFileInputStream(String uri, InputStream is, Map<String, String> paramsMap, String fileName, String name, int timeout) {
        RequestConfig config = timeout < 1 ? requestConfig : RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).build();

        HttpPost httpPost = new HttpPost(uri);
        httpPost.setConfig(config);
        MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create();
        ContentType contentType;
        if (fileName == null) {
            contentType = ContentType.TEXT_HTML;
            fileName = "resume.html";
        } else {
            contentType = ContentType.DEFAULT_BINARY;
        }
        mEntityBuilder.addBinaryBody(name, is, contentType, fileName);
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {

            mEntityBuilder.addTextBody(entry.getKey(), entry.getValue());
        }
        httpPost.setEntity(mEntityBuilder.build());
        try {
            return basicClient.execute(httpPost, getResponseHandler());
        } catch (IOException e) {
            LOGGER.error("", e);
            throw new HttpClientException(uri, e);
        }

    }

    public String postByFileInputStream(String uri, InputStream instream, Map<String, String> paramsMap, String fileName, String name) {
        return postByFileInputStream(uri, instream, paramsMap, fileName, name, 0);
    }

    private void createHttpClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                .build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
        ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
        Registry<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .register("http", plainSF)
                .build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(r);
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);
        basicClient = org.apache.http.impl.client.HttpClients.custom()
                .disableCookieManagement()
                .setRedirectStrategy(new DefaultRedirectStrategy())
                .setConnectionManager(cm)
                .build();
    }

    private ResponseHandler<String> getResponseHandler() {
        return response -> {
            int status = response.getStatusLine().getStatusCode();
            HttpEntity httpEntity = response.getEntity();
            String entity = httpEntity == null ? "" : EntityUtils.toString(httpEntity, "utf-8");
            if (status >= 200 && status < 300) {
                return entity;
            } else if (status >= 400 && status < 500) {
                return entity;
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status + "; entity: " + entity);
            }
        };
    }

    @Override
    public void afterPropertiesSet() {
        try {
            this.createHttpClient();
        } catch (KeyStoreException | NoSuchAlgorithmException | KeyManagementException e) {
            LOGGER.error("init", e);
        }
    }
}