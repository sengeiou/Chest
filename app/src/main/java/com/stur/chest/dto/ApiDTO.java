package com.stur.chest.dto;

import com.stur.lib.IntegerUtil;
import com.stur.lib.exception.AppOperactionException;
import com.stur.lib.exception.ParameterException;
import com.stur.lib.exception.UnLoginException;

import java.io.Serializable;



/**
 * Created by Administrator on 2016/3/9.
 */
public class ApiDTO<T> implements Serializable {
    // TODO: 2016/3/9 需要按接口修改成员变量和方法
    private int code;
    private String msg;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(String code) throws ParameterException {
        this.code = IntegerUtil.getInt(code, "Response code");
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() throws AppOperactionException, UnLoginException {
        if (code > 0) {
            throw new AppOperactionException("Error Request");
        } else if (code < 0) {
            throw new UnLoginException("Not login!");
        }
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApiDTO{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

}

