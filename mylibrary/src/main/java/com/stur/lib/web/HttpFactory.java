package com.stur.lib.web;

import com.stur.lib.app.ContextBase;
import com.stur.lib.web.base.HttpService;
import com.stur.lib.web.impl.AsyncHttpImpl;


/**
 * Created by Administrator on 2016/3/4.
 */
public class HttpFactory {
    private static ContextBase sContext = null;
    private static HttpService mHttpService = null;
    private static HttpEngine mHttpEngine = HttpEngine.ASYNC_HTTP;

    public static void register(ContextBase context) {
        sContext = context;
    }

    public static void setHttpEngine(HttpEngine httpEngine) {
        mHttpEngine = httpEngine;
        mHttpService = null;
    }

    public static HttpService getHttpService() {
        if (mHttpService == null) {
            // 通过Volley进行实现
            switch (mHttpEngine) {
                case ASYNC_HTTP:
                    mHttpService = new AsyncHttpImpl(sContext);
                    break;
                default:
                    throw new RuntimeException("Unknown engine: " + mHttpEngine);
            }
        }
        return mHttpService;
    }
}
