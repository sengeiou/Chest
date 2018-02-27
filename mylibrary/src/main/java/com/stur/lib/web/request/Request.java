package com.stur.lib.web.request;

import android.util.Log;

import com.loopj.android.http.RequestParams;
import com.stur.lib.os.OnEventListener;
import com.stur.lib.web.HttpConstants;
import com.stur.lib.web.URLBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;


/**
 * Created by Administrator on 2016/3/4.
 */
public abstract class Request {
    /**
     * URL
     */
    protected String url;

    /**
     * Method
     */
    protected HttpMethod method;

    /**
     * 请求的头部
     */
    protected Map<String, String> headers = new HashMap<String, String>();

    /**
     * 缓存时间
     */
    protected int cacheTime = 0;

    /**
     * 超时时间
     */
    protected int timeout = HttpConstants.REQUEST_TIMEOUT_MS;

    protected String encoding = HttpConstants.DEFAULT_PARAMS_ENCODING;

    /**
     * HttpEntity
     */
    protected MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();

    protected OnEventListener<Boolean> onEventListener;

    public Request(String url, HttpMethod method) {
        this.url = url;
        this.method = method;
    }

    public Request(URLBuilder builder, HttpMethod method) {
        this(builder.build(), method);
    }

    /**
     * HttpEntity builder
     * <p/>
     * addTextBody()
     * addBinaryBody();
     *
     * @return entityBuilder
     */
    public MultipartEntityBuilder getEntityBuilder() {
        return entityBuilder;
    }

    /**
     * HTTP BODY
     * 处理HTTP中的主体内容
     * 包括POST和PUT参数，以及一些主体内容
     *
     * @return httpEntity
     */
    public HttpEntity getHttpEntity() {
        return entityBuilder.build();
    }

    public String getContentType() {
        return getHttpEntity().getContentType().getValue();
    }

    /**
     * HTTP BODY
     * 处理HTTP中的主体内容
     * 包括POST和PUT参数，以及一些主体内容
     *
     * @return body
     */
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            getHttpEntity().writeTo(bos);
        } catch (IOException e) {
            Log.e("HTTP", "IOException writing to ByteArrayOutputStream", e);
        }
        return bos.toByteArray();
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public int getCacheTime() {
        return cacheTime;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setCacheTime(int cacheTime) {
        this.cacheTime = cacheTime;
    }

    public void setCancelListener(OnEventListener<Boolean> onEventListener) {
        this.onEventListener = onEventListener;
    }

    public void cancel() {
        if (onEventListener != null) {
            onEventListener.onEvent(true);
        }
    }

    /**
     * Add for the problem that cannot post form-data correctly.
     */
    protected RequestParams mParams;

    public RequestParams getParams(){
        return mParams;
    }
}
