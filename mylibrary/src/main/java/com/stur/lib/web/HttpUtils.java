package com.stur.lib.web;

//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;


import com.stur.lib.Log;

import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;

public class HttpUtils {
    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.')+1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    /**
     * Converts <code>params</code> 使用于URL参数格式化，或者body参数格式化
     */
    public static String encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(dealParamKey(entry.getKey()), paramsEncoding));
                encodedParams.append('=');
                //TODO: edited by wilson
                if (entry.getValue() != null) {
                    encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                }
                encodedParams.append('&');
            }
//            logger.v(encodedParams.toString());
            return encodedParams.toString();
        } catch (Exception uee) {
            Log.e(getTag(), "encodedParams: " + params, uee);
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    /**
     * Converts <code>params</code> into an application/x-www-form-urlencoded encoded string.
     */
    public static byte[] encodeParametersToBytes(Map<String, String> params, String paramsEncoding) {
        try {
            return encodeParameters(params, paramsEncoding).getBytes(paramsEncoding);
        } catch (Exception uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    /**
     * name[0]
     * to
     * name[]
     *
     * @param key
     * @return
     */
    public static String dealParamKey(String key) {
        String content = key;
        String regex = "\\[[0-9]\\d*\\]$";

        Matcher matcher = Pattern.compile(regex).matcher(content);
        if (matcher.find()) {
            content = content.replace(matcher.group(), "[]");
        }

        return content;
    }

    public static void initSSL() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取请求中的头部信息包
     *
     * @return
     */
    public static Header[] toHeaders(Map<String, String> headerMap) {
        Header[] headers = new Header[headerMap.size()];
        int i = 0;
        for (String key : headerMap.keySet()) {
            headers[i] = new BasicHeader(key, headerMap.get(key));
            i++;
        }
        return headers;
    }

    /**
     * 上传数据
     *
     * @param url
     * @param param
     * @return
     */
    /*public static String postData(String url, HashMap<String, String> param) {

        HttpPost httpPost = new HttpPost(url);
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (param != null) {
            Set<Entry<String, String>> set = param.entrySet();
            Iterator<Entry<String, String>> iterator = set.iterator();
            while (iterator.hasNext()) {
                Entry<String, String> tempEntry = iterator.next();
                params.add(new BasicNameValuePair(tempEntry.getKey(), tempEntry.getValue()));
            }
        } else {
            Log.e(null, "post param error");
            return null;
        }
        HttpResponse httpResponse = null;
        try {
            // 设置httpPost请求参数
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpClient httpClient = new DefaultHttpClient();
            // 请求超时
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
            // 读取超时
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
            httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                // 第三步，使用getEntity方法活得返回结果
                String result = EntityUtils.toString(httpResponse.getEntity());
                return result;
            }
        } catch (ClientProtocolException e) {
            Log.e(null, "Exception: " + e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e(null, "Exception:" + e.getMessage());
            return null;
        }
        return null;
    }*/

    /**
     * @return 通过Request方式曝光
     */
    /*public static boolean uploadRequest(String baseUrl, String param) throws ClientProtocolException, IOException {
        HttpGet httpGet = null;
        if (param != null && !"".equals(param)) {
            httpGet = new HttpGet(baseUrl + "?" + param);
        } else {
            httpGet = new HttpGet(baseUrl);
        }
        Log.i(null, "URL:"+httpGet.getURI()+"");
        HttpParams params = httpGet.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 3000);
        HttpConnectionParams.setSoTimeout(params, 3000);
        HttpClient httpClient = new DefaultHttpClient(params);
        HttpResponse response = httpClient.execute(httpGet);
        int statusCode = response.getStatusLine().getStatusCode();
        Log.i(null, "response code:" + statusCode);
        if (statusCode < 300 && statusCode >= 200) {
            return true;
        }
        return false;
    }*/
}
