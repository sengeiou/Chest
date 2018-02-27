package com.stur.lib.web.impl;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.TextHttpResponseHandler;
import com.stur.lib.constant.CacheTime;
import com.stur.lib.network.NetworkUtils;
import com.stur.lib.os.OnEventListener;
import com.stur.lib.web.HttpConstants;
import com.stur.lib.web.HttpUtils;
import com.stur.lib.web.base.AbstractHttpService;
import com.stur.lib.web.base.HttpListener;
import com.stur.lib.web.interceptor.HttpInterceptorManager;
import com.stur.lib.web.request.HttpMethod;
import com.stur.lib.web.request.Request;
import com.stur.lib.web.response.HttpError;
import com.stur.lib.web.response.HttpResponse;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/3/4.
 */
public class AsyncHttpImpl extends AbstractHttpService {
    private final Context mContext;
    private AsyncHttpClient mAsyncHttpClient;
    private PersistentCookieStore mPersistentCookieStore;

    public AsyncHttpImpl(Context context) {
        mContext = context;
        // SSL
        mPersistentCookieStore = new PersistentCookieStore(context);
        mAsyncHttpClient = new AsyncHttpClient(true, 80, 443);
        mAsyncHttpClient.setCookieStore(mPersistentCookieStore);
        mAsyncHttpClient.setTimeout(20 * 1000);
//        try {
//            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            // 证书
//            trustStore.load(null, null);
//            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
//            sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//            mAsyncHttpClient.setSSLSocketFactory(sf);
//        } catch (Exception e) {
//            Log.e(TAG, "initSSL fail.", e);
//            mAsyncHttpClient = new AsyncHttpClient(true, 80, 443);
//        }
    }

    /**
     * 发送一个请求
     *
     * @param request
     * @param httpListener
     */
    @Override
    public void sendRequest(final Request request, final HttpListener<?> httpListener) {
//        logger.v("[" + request.getMethod() + "] " + request.getUrl());

        // 网络不可用
        if (!NetworkUtils.isInternetConnected(mContext)) {
            if (httpListener == null) {
                return;
            }
            if (fireCache(request.getUrl(), httpListener)) {
                return;
            } else {
                httpListener.onStart();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HttpError httpError = new HttpError();
                            httpError.setCode(HttpConstants.Error.NETWORK_ERROR.getCode());
                            httpError.setMessage(HttpConstants.Error.NETWORK_ERROR.getMessage());
                            httpListener.onFailure(httpError);
                        } finally {
                            httpListener.onFinish();
                        }
                    }
                }, 1000);
                return;
            }
        }

        executeRequest(request, httpListener);
    }

    private void executeRequest(final Request request, final HttpListener<?> httpListener) {
        ResponseHandlerInterface responseHandler = new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (httpListener != null) httpListener.onStart();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                if (httpListener != null) {
                    try {
                        if (responseString != null) {
                            HttpError httpError = new HttpError();
                            httpError.setCode(statusCode);
                            httpError.setResponse(responseString);
                            httpListener.handleError(httpError);
                        } else {
                            httpListener.handleError(makeError(throwable));
                        }
                    } finally {
                        httpListener.onFinish();
                    }
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                for (Header header : headers) {
//                    logger.d(header);
//                }
//                logger.v(responseString);

                if (httpListener != null) {
                    try {
                        HttpResponse httpResponse = HttpInterceptorManager.getInstance().invoke(new HttpResponse(statusCode, responseString));
                        httpListener.handleResponse(httpResponse);
                    } catch (HttpError httpError) {
                        httpListener.handleError(httpError);
                    } finally {
                        httpListener.onFinish();
                    }
                }

                // put to cache
//                if (request.getMethod() == HttpMethod.GET && request.getCacheTime() > 0) {
//                    putCache(request.getUrl(), responseString, request.getCacheTime());
//                }
                if (request.getMethod() == HttpMethod.GET) {
                    putCache(request.getUrl(), responseString, CacheTime.DAY);
                }
            }

        };
        /* 添加全局 headers */
        request.getHeaders().putAll(getHeaderMap());
        Header[] headers = HttpUtils.toHeaders(request.getHeaders());
        RequestHandle requestHandle = null;
        switch (request.getMethod()) {
            case GET:
                if (request.getCacheTime() > 0) {
                    // 是否已经有缓存了，存在自动返回缓存数据
                    if (fireCache(request.getUrl(), httpListener)) return;
                }
                requestHandle = mAsyncHttpClient.get(mContext, request.getUrl(), headers, null, responseHandler);
                break;
            case TRACE:
                // AsyncHttp not supported
                request.addHeader("X-HTTP-Method-Override", "TRACE");
            case PATCH:
                // AsyncHttp not supported
                request.addHeader("X-HTTP-Method-Override", "PATCH");
            case OPTIONS:
                // AsyncHttp not supported
                request.addHeader("X-HTTP-Method-Override", "OPTIONS");
            case POST:
                //requestHandle = mAsyncHttpClient.post(mContext, request.getUrl(), headers, request.getHttpEntity(), request.getContentType(), responseHandler);
                requestHandle = mAsyncHttpClient.post(mContext, request.getUrl(), request.getParams(), responseHandler);
                break;
            case PUT:
                requestHandle = mAsyncHttpClient.put(mContext, request.getUrl(), headers, request.getHttpEntity(), request.getContentType(), responseHandler);
                break;
            case DELETE:
                requestHandle = mAsyncHttpClient.delete(mContext, request.getUrl(), headers, responseHandler);
                break;
            case HEAD:
                requestHandle = mAsyncHttpClient.head(mContext, request.getUrl(), headers, new RequestParams(), responseHandler);
                break;
            default:
                Log.w("", "HTTP Method not supported!!! " + request.getMethod());
        }
        final RequestHandle finalRequestHandle = requestHandle;
        request.setCancelListener(new OnEventListener<Boolean>() {
            @Override
            public void onEvent(Boolean event) {
                if (finalRequestHandle != null && event) {
                    finalRequestHandle.cancel(event);
                }
            }
        });
    }

    private HttpError makeError(Throwable throwable) {
        HttpError httpError = new HttpError();
        httpError.setCode(HttpConstants.Error.DEFUALT_ERROR.getCode());
        httpError.setResponse(HttpConstants.Error.DEFUALT_ERROR.getMessage());
        return httpError;
    }
}
