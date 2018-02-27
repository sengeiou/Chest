package com.stur.lib.web.interceptor;


import com.stur.lib.web.response.HttpError;
import com.stur.lib.web.response.HttpResponse;

public abstract class AbstractHttpInterceptor implements HttpInterceptor {

    @Override
    public HttpResponse response(HttpResponse response) {
        return response;
    }

    @Override
    public HttpError error(HttpError httpError) {
        return httpError;
    }
}
