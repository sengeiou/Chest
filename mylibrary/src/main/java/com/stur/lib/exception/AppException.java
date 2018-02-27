package com.stur.lib.exception;

/**
 * Created by Administrator on 2016/3/4.
 */
public class AppException extends Exception {
    public AppException() {
        super();
    }

    public AppException(String detailMessage) {
        super(detailMessage);
    }
}