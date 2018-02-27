package com.stur.lib.web.request;


import java.io.File;

/**
 * 文件请求类
 * <p/>
 * Created by Tony on 1/5/15.
 */
public class FileRequest extends HttpRequest {

    public FileRequest(String url, HttpMethod method) {
        super(url, method);
    }

    public void addBinaryBody(String key, File file) {
        getEntityBuilder().addBinaryBody(key, file);
    }

}
