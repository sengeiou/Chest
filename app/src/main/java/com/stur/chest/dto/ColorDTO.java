package com.stur.chest.dto;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/9.
 */
public class ColorDTO implements Serializable{
    protected int id;
    protected int inventory;
    protected String name;
    protected String imgUrl;

    public int getId() {
        return id;
    }
}
