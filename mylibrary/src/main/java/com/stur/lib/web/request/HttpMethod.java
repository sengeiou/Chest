package com.stur.lib.web.request;

/**
 * Created by Administrator on 2016/3/4.
 */
public enum HttpMethod {

    /**
     * 通过请求URI得到资源
     */
    GET(0, "GET"),

    /**
     * 用于提交新的内容 login
     */
    POST(1, "POST"),

    /**
     * 用于修改某个内容 json
     */
    PUT(2, "PUT"),

    /**
     * 删除某个内容
     */
    DELETE(3, "DELETE"),

    /**
     * 类似于GET, 但是不返回body信息，用于检查对象是否存在，以及得到对象的元数据
     */
    HEAD(4, "HEAD"),

    /**
     * 询问可以执行哪些方法
     */
    OPTIONS(5, "OPTIONS"),

    /**
     * 用于远程诊断服务器
     */
    TRACE(6, "TRACE"),

    /**
     * 部分文档更改
     */
    PATCH(7, "PATCH");

    private int id;
    private String name;

    HttpMethod(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

}
