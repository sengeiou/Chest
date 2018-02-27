package com.stur.lib.web.request;


import android.text.TextUtils;

import com.loopj.android.http.RequestParams;
import com.stur.lib.web.URLBuilder;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.entity.ContentType;



/**
 * HTTP请求
 * url, method
 * headerMap,  postParams
 * <p/>
 */
public class HttpRequest extends Request {

    public HttpRequest(String url, HttpMethod method) {
        super(url, method);
    }

    public HttpRequest(URLBuilder builder, HttpMethod method) {
        super(builder.build(), method);
    }

    /**
     * name=value
     * if array
     * name[]=value
     * name[]=value
     *
     * @param key
     * @param value
     */
    public void addTextBody(String key, String value) {
        if (value == null) value = "";

        getEntityBuilder().addTextBody(key, value, ContentType.create("text/plain", getEncoding()));
    }

    /**
     * name[]=value1
     * name[]=value2
     *
     * @param key
     * @param values
     */
    public void addTextBody(String key, String... values) {
        for (String value : values) {
            addTextBody(key, value);
        }
    }

    /**
     * 文件
     *
     * @param key
     * @param file
     */
    public void addBinaryBody(String key, File file) {
        getEntityBuilder().addBinaryBody(key, file);
    }

    public void addParams(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (value == null) value = "";
        if (mParams == null) {
            mParams = new RequestParams();
        }
        mParams.add(key, value);
    }

    public void addParams(String key, File file) throws FileNotFoundException {
        if (TextUtils.isEmpty(key) || file == null) {
            return;
        }
        if (mParams == null) {
            mParams = new RequestParams();
        }
        mParams.put(key, file);
    }
}
