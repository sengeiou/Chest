package com.stur.lib.web.base;

import com.stur.lib.app.ContextBase;
import com.stur.lib.web.response.HttpError;

/**
 * Created by Tony on 3/2/15.
 */
public abstract class AbstractHttpListener<T> implements HttpListener<T> {

    /**
     * 解析错误
     *
     * @param httpError
     */
    @Override
    public void handleError(HttpError httpError) {
        onFailure(httpError);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onFailure(HttpError error) {
        // print toString()

        error.makeToast(ContextBase.getInstance());
    }

}
