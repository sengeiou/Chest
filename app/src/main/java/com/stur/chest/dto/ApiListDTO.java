package com.stur.chest.dto;

import com.stur.lib.IntegerUtil;
import com.stur.lib.exception.AppOperactionException;
import com.stur.lib.exception.ParameterException;
import com.stur.lib.exception.UnLoginException;

import java.io.Serializable;
import java.util.List;



/**
 * Created by Administrator on 2016/3/20.
 */
public class ApiListDTO<T> implements Serializable {
    // TODO: 2016/3/9 需要按接口修改成员变量和方法
    private int code;
    private String msg;
    private List<T> data;
    private PageInfos page;

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

    public List<T> getData() throws AppOperactionException, UnLoginException {
        if (code > 0) {
            throw new AppOperactionException("Error Request");
        } else if (code < 0) {
            throw new UnLoginException("Not login!");
        }
        return data;
    }

    public PageInfos getPage() {
        return page;
    }

    public void setData(List<T> data) {
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

    public class PageInfos implements Serializable {
        private int page_size;
        private int page_count;
        private int item_count;
        private int current_page;

        public void setPage_size(String page_size) throws ParameterException {
            this.page_size = IntegerUtil.getInt(page_size);
        }

        public int getPage_size() {
            return this.page_size;
        }

        public void setPage_count(String page_count) throws ParameterException {
            this.page_count = IntegerUtil.getInt(page_count);
        }

        public int getPage_count() {
            return this.page_count;
        }

        public void setItem_count(String item_count) throws ParameterException {
            this.item_count = IntegerUtil.getInt(item_count);
        }

        public int getItem_count() {
            return this.item_count;
        }

        public void setCurrent_page(String current_page) throws ParameterException {
            this.current_page = IntegerUtil.getInt(current_page);
        }

        public int getCurrent_page() {
            return this.current_page;
        }
    }
}

