package com.stur.chest.dto;

/**
 * Created by Administrator on 2016/3/18.
 */
public class CountryDTO {
    protected int id;
    protected String name;
    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
