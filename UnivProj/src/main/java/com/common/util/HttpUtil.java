package com.common.util;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.*;
/**
 * @Auther: EMMA
 * @Date: 2018/6/23
 * @Description:
 * @Since:
 */

public enum HttpUtil {
        INSTANCE;

        public static final String _UTF_8 = "UTF-8";
        public static final String _CONTENT_TYPE_JSON = "application/json";
        public static final String _CONTENT_TYPE_XML = "application/xml";
        public static final String _CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
        private static final int _TIMEOUT_SOCKET = 10000;
        private static final int _TIMEOUT_CONNECT = 10000;
        private static final int _TIMEOUT_CONNECTION_REQUEST = 10000;
        private static final int _MAX_TOTAL = 50;
        private static final int _DEFAULT_MAX_PER_ROUTE = 25;
        private final Logger log = LoggerFactory.getLogger(HttpUtil.class);
        public final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).setConnectionRequestTimeout(10000).build();
        public final PoolingHttpClientConnectionManager connMgr;
        private CloseableHttpClient httpClient;
        private CloseableHttpClient httpClientSSL;

        HttpUtil() {

            // 设置协议http和https对应的处理socket链接工厂的对象
            SSLContext sslcontext = createIgnoreVerifySSL();
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslcontext))
                    .build();
            this.connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            this.connMgr.setMaxTotal(50);
            this.connMgr.setDefaultMaxPerRoute(25);
        }

        public CloseableHttpClient getHttpClient() {
            if(null == this.httpClient) {
                HttpUtil var1 = INSTANCE;
                synchronized(INSTANCE) {
                    this.httpClient = HttpClients.custom().setConnectionManager(this.connMgr).setDefaultRequestConfig(this.requestConfig).build();
                }
            }

            return this.httpClient;
        }

        public CloseableHttpClient getHttpClientSSL() {
            if(null == this.httpClientSSL) {
                synchronized(INSTANCE) {
                    this.httpClientSSL = HttpClients.custom().setConnectionManager(this.connMgr).setDefaultRequestConfig(this.requestConfig).build();
                }
            }

            return this.httpClientSSL;
        }

        public String doGet(String url) throws IOException {
            return this.doGet(url, new HashMap());
        }

        public String doGet(String url, Map<String, Object> params) {
            StringBuffer param = new StringBuffer();
            int i = 0;

            for(Iterator var6 = params.keySet().iterator(); var6.hasNext(); ++i) {
                String key = (String)var6.next();
                if(i == 0) {
                    param.append("?");
                } else {
                    param.append("&");
                }

                param.append(key).append("=").append(params.get(key));
            }

            String apiUrl = url + param;
            String result = null;
            HttpGet httpPost = new HttpGet(apiUrl);
            HttpResponse response = this.getHttpClient().execute(httpPost);
            HttpEntity entity = response.getEntity();
            if(entity != null) {
                InputStream instream = entity.getContent();
                result = IOUtils.toString(instream, "UTF-8");
            }

            return result;
        }

        public String doPost(String apiUrl) {
            return this.doPost(apiUrl, new HashMap());
        }

        public String doPost(String apiUrl, Map<String, Object> params) {
            String httpStr = null;
            HttpPost httpPost = new HttpPost(apiUrl);
            CloseableHttpResponse response = null;

            try {
                List<NameValuePair> pairList = new ArrayList(params.size());
                Iterator var7 = params.entrySet().iterator();

                while(var7.hasNext()) {
                    Map.Entry<String, Object> entry = (Map.Entry)var7.next();
                    NameValuePair pair = new BasicNameValuePair((String)entry.getKey(), entry.getValue().toString());
                    pairList.add(pair);
                }

                httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
                response = this.getHttpClient().execute(httpPost);
                HttpEntity entity = response.getEntity();
                httpStr = EntityUtils.toString(entity, "UTF-8");
            } catch (IOException var18) {
                this.log.error(var18.getMessage(), var18);
            } finally {
                if(response != null) {
                    try {
                        EntityUtils.consume(response.getEntity());
                    } catch (IOException var17) {
                        this.log.error(var17.getMessage(), var17);
                    }
                }

            }

            return httpStr;
        }

        public String doPost(String apiUrl, Object json) throws Exception {
            HttpPost httpPost = new HttpPost(apiUrl);
            return this.doRequestWithJSON(httpPost, json.toString(), "UTF-8");
        }

        public String doPostWithForm(String apiUrl, String form) throws Exception {
            HttpPost httpPost = new HttpPost(apiUrl);
            return this.doRequestWithForm(httpPost, form, "UTF-8");
        }

        public String doPostWithXML(String apiUrl, String xmlinfo) throws Exception {
            HttpPost httpPost = new HttpPost(apiUrl);
            return this.doRequestWithXML(httpPost, xmlinfo, "UTF-8");
        }

        public String doPut(String apiUrl, Object json) throws Exception {
            HttpPut httpPut = new HttpPut(apiUrl);
            return this.doRequestWithJSON(httpPut, json.toString(), "UTF-8");
        }

        public String doPostSSL(String apiUrl, Map<String, Object> params) throws Exception {
            HttpPost httpPost = new HttpPost(apiUrl);
            List<NameValuePair> pairList = new ArrayList(params.size());
            Iterator var5 = params.entrySet().iterator();

            while(var5.hasNext()) {
                Map.Entry<String, Object> entry = (Map.Entry)var5.next();
                NameValuePair pair = new BasicNameValuePair((String)entry.getKey(), entry.getValue().toString());
                pairList.add(pair);
            }

            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
            CloseableHttpResponse response = this.getHttpClientSSL().execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode != 200) {
                return null;
            } else {
                HttpEntity entity = response.getEntity();
                if(entity == null) {
                    return null;
                } else {
                    String httpStr = EntityUtils.toString(entity, "UTF-8");

                    try {
                        EntityUtils.consume(response.getEntity());
                    } catch (IOException var10) {
                        this.log.error(var10.getMessage(), var10);
                    }

                    return httpStr;
                }
            }
        }

        public String doPostSSL(String apiUrl, Object json) throws Exception {
            HttpPost httpPost = new HttpPost(apiUrl);
            return this.doRequestSSLWithJSON(httpPost, json.toString(), "UTF-8");
        }

        public String doPostSSLWithForm(String apiUrl, String form) throws Exception {
            return this.doPostSSLWithForm(apiUrl, form, "UTF-8");
        }

        public String doPostSSLWithForm(String apiUrl, String form, String encoding) throws Exception {
            HttpPost httpPost = new HttpPost(apiUrl);
            return this.doRequestSSLWithForm(httpPost, form, encoding);
        }

        public String doPostSSLWithXML(String apiUrl, String xmlinfo) throws Exception {
            return this.doPostSSLWithXML(apiUrl, xmlinfo, "UTF-8");
        }

        public String doPostSSLWithXML(String apiUrl, String xmlinfo, String encoding) throws Exception {
            HttpPost httpPost = new HttpPost(apiUrl);
            return this.doRequestSSLWithXML(httpPost, xmlinfo, encoding);
        }

        private String doRequestWithForm(HttpEntityEnclosingRequestBase request, String form, String encoding) throws Exception {
            return this.doRequest(request, form, encoding, "application/x-www-form-urlencoded");
        }

        private String doRequestWithJSON(HttpEntityEnclosingRequestBase request, String json, String encoding) throws Exception {
            return this.doRequest(request, json, encoding, "application/json");
        }

        private String doRequestWithXML(HttpEntityEnclosingRequestBase request, String message, String encoding) throws Exception {
            return this.doRequest(request, message, encoding, "application/xml");
        }

        private String doRequestSSLWithJSON(HttpEntityEnclosingRequestBase request, String json, String encoding) throws Exception {
            return this.doRequestSSL(request, json, encoding, "application/json");
        }

        private String doRequestSSLWithForm(HttpEntityEnclosingRequestBase request, String form, String encoding) throws Exception {
            return this.doRequestSSL(request, form, encoding, "application/x-www-form-urlencoded");
        }

        private String doRequestSSLWithXML(HttpEntityEnclosingRequestBase request, String message, String encoding) throws Exception {
            return this.doRequestSSL(request, message, encoding, "application/xml");
        }

        private String doRequest(HttpEntityEnclosingRequestBase request, String message, String encoding, String contentType) {
            this.log.debug("DO REQUEST: " + message);
            StringEntity stringEntity = new StringEntity(message, encoding);
            stringEntity.setContentEncoding(encoding);
            stringEntity.setContentType(contentType);
            request.setEntity(stringEntity);
            CloseableHttpResponse response = this.getHttpClient().execute(request);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, encoding);

            try {
                EntityUtils.consume(response.getEntity());
            } catch (IOException var10) {
                this.log.error(var10.getMessage(), var10);
            }

            return result;
        }

        private String doRequestSSL(HttpEntityEnclosingRequestBase request, String message, String encoding, String contentType) throws Exception {
            this.log.debug("DO REQUEST WITH SSL: " + message);
            StringEntity stringEntity = new StringEntity(message, encoding);
            stringEntity.setContentEncoding(encoding);
            stringEntity.setContentType(contentType);
            request.setEntity(stringEntity);
            CloseableHttpResponse response = this.getHttpClientSSL().execute(request);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, encoding);

            try {
                EntityUtils.consume(response.getEntity());
            } catch (IOException var10) {
                this.log.error(var10.getMessage(), var10);
            }

            return result;
        }

        private ConnectionSocketFactory createConnSocketFactory() {
            return PlainConnectionSocketFactory.getSocketFactory();
        }

        private ConnectionSocketFactory createSSLConnSocketFactory() {
            SSLConnectionSocketFactory sslsf = null;

            try {
                sslsf = new SSLConnectionSocketFactory(createIgnoreVerifySSL());
            } catch (Exception var3) {
                this.log.error(var3.getMessage(), var3);
            }

            return sslsf;
        }
//    public SSLContext createIgnoreVerifySSL() throws Exception {
//        return (new SSLContextBuilder()).loadTrustMaterial((KeyStore)null, (x509Certificates, s) -> {
//            return true;
//        }).build();
//    }
    /**
     * 绕过验证
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static SSLContext createIgnoreVerifySSL() {
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSLv3");


            // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
            X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                        String paramString) {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                        String paramString) {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sc.init(null, new TrustManager[] { trustManager }, null);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {

        }
        return sc;
    }
}

