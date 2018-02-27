package com.stur.lib.web.interceptor;


import com.stur.lib.web.response.HttpError;
import com.stur.lib.web.response.HttpResponse;

/**
 * Created by Tony on 3/4/15.
 */
public interface HttpInterceptor {

    public HttpResponse response(HttpResponse response) throws HttpError;

    public HttpError error(HttpError httpError);

}
