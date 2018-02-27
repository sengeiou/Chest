package com.stur.lib.web.response;

import android.content.Context;

import com.stur.lib.StringUtils;
import com.stur.lib.UIHelper;
import com.stur.lib.exception.AppException;


/**
 * Created by Administrator on 2016/3/4.
 */
public class HttpError extends AppException {
    private int code;
    private String message;
    private String response;

    public HttpError() {

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 产生一个toast
     *
     * @param context
     */
    public void makeToast(Context context) {
        if (context != null && StringUtils.isNotEmpty(getMessage())) {
            UIHelper.toastMessageMiddle(context, getMessage());
        }
    }

    @Override
    public String toString() {
        return "HttpError{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}
