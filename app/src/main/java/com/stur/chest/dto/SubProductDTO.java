package com.stur.chest.dto;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/27.
 */
public class SubProductDTO implements Serializable{
    private int id;
    private int goods_id;
    private int stock;
    private int online;
    private String color;
    private String size;
    private String rmb_price;
    private String my_price;
    private String img;

    public int getId(){
        return id;
    }

    public int getGoods_id(){
        return goods_id;
    }

    public int getStock(){
     return stock;
    }
    public int getOnline(){
        return online;
    }
    public String getColor(){
        return color;
    }
    public String getSize(){
        return size;
    }
    public String getMy_price(){
        return my_price;
    }
    public String getImg(){
        return img;
    }
}
