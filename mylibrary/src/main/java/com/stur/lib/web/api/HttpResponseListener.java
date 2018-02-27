package com.stur.lib.web.api;

import com.stur.lib.StringUtils;
import com.stur.lib.web.HttpConstants;
import com.stur.lib.web.base.AbstractHttpListener;
import com.stur.lib.web.response.HttpError;
import com.stur.lib.web.response.HttpResponse;
import com.stur.lib.web.HttpConstants.Error;

/**
 */
public abstract class HttpResponseListener extends AbstractHttpListener<HttpResponse> {

    @Override
    public void handleResponse(HttpResponse response) {
        if (StringUtils.isEmpty(response.getResponse())) {
            HttpError httpError = new HttpError();
            httpError.setCode(Error.RESPONSE_NULL.getCode());
            httpError.setMessage(Error.RESPONSE_NULL.getMessage());
            onFailure(httpError);
            return;
        }
        try {
            onSuccess(response);
        } catch (Exception e) {

            HttpError httpError = new HttpError();
            httpError.setCode(HttpConstants.Error.RESPONSE_ERROR.getCode());
            httpError.setMessage(HttpConstants.Error.RESPONSE_ERROR.getMessage());
        }
    }

}
