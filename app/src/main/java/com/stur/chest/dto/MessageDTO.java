package com.stur.chest.dto;

import com.stur.lib.IntegerUtil;
import com.stur.lib.exception.ParameterException;

import java.io.Serializable;



/**
 * Created by Administrator on 2016/3/8.
 */
public class MessageDTO implements Serializable{
    protected int id;
    protected int user_id;
    protected String user_name;
    protected String title;
    protected String summary;
    protected String content;
    protected long create_at;
    protected int order;
    protected int is_open;

    public void setId(String id) throws ParameterException {
        this.id = IntegerUtil.getInt(id, "MessageID");
    }

    public int getId() {
        return this.id;
    }

    public void setUser_id(String user_id) throws ParameterException {
        this.user_id = IntegerUtil.getInt(user_id, "UserID");
    }

    public int getUser_id() {
        return this.user_id;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_name() {
        return this.user_name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    public void setCreate_at(String create_at) throws ParameterException {
        this.create_at = IntegerUtil.getLong(create_at, "Create time");
    }

    public long getCreate_at() {
        return this.create_at;
    }

    public void setOrder(String order) throws ParameterException {
        this.order = IntegerUtil.getInt(order, "Order");
    }

    public int getOrder() {
        return this.order;
    }

    public void setIs_open(String is_open) throws ParameterException {
        this.is_open = IntegerUtil.getInt(is_open, "Flag of open");
    }

    public int getIs_open() {
        return this.is_open;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Message info : [id]").append(id)
                .append("; [user_name]").append(user_name)
                .append("; [user_id]").append(user_id)
                .append("; [title]").append(title)
                .append("; [summary]").append(summary)
                .append("; [content]").append(content)
                .append("; [create_at]").append(create_at)
                .append("; [order]").append(order)
                .append("; [is_open]").append(is_open);
        return sb.toString();
    }
}
