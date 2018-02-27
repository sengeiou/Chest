package com.stur.lib.web.base;


import com.stur.lib.web.request.Request;

/**
 * Created by Administrator on 2016/3/4.
 */
public interface HttpService {
    /**
     * 发送一个请求
     *
     * @param request
     * @param httpListener
     */
    public void sendRequest(Request request, HttpListener<?> httpListener);

    /**
     * 添加请求头信息
     *
     * @param key
     * @param value
     */
    public void addHeader(String key, String value);
}
