package com.stur.lib.web.base;


import com.stur.lib.web.response.HttpError;
import com.stur.lib.web.response.HttpResponse;

/**
 * Created by Administrator on 2016/3/4.
 */
public interface HttpListener<T> {
    public void handleResponse(HttpResponse response);

    public void handleError(HttpError httpError);

    public void onStart();

    public void onSuccess(T response);

    public void onFailure(HttpError error);

    public void onFinish();
}