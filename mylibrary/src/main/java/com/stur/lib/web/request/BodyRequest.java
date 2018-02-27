package com.stur.lib.web.request;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * 主体请求类
 * <p/>
 * Created by Tony on 10/18/14.
 */
public class BodyRequest extends Request {
    private StringEntity httpEntity;

    public BodyRequest(String url, HttpMethod method) {
        super(url, method);
    }

    public void setContentType(String contentType) {
        httpEntity.setContentType(contentType + "; charset=" + getEncoding());
    }

    public void setBody(String bodyStr) {
        httpEntity = new StringEntity(bodyStr, getEncoding());
        httpEntity.setContentType("application/json; charset=" + getEncoding());
    }

    public void setBody(JSONObject bodyJson) {
        setBody(bodyJson.toString());
    }

    public void setBody(JSONArray bodyJson) {
        setBody(bodyJson.toString());
    }

    @Override
    public HttpEntity getHttpEntity() {
        return httpEntity;
    }
}
