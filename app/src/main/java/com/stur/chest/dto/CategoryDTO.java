package com.stur.chest.dto;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/9.
 */
public class CategoryDTO implements Serializable {

    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String PARENT_ID = "parent_id";
    public static final String IMG = "img";

    private int id;
    private String name;
    private int parent_id;
    private String img;

    public CategoryDTO(int id, String name, int parent_id, String img) {
        this.id = id;
        this.name = name;
        this.parent_id = parent_id;
        this.img = img;
    }

    public CategoryDTO(){
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public int getParentId(){
        return parent_id;
    }

    public String getImg(){
        return img;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setParentId(int paraId){
        this.parent_id = paraId;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "CategoryInfo: id = " + id + ", name = " + name
                + ", parentId = " + parent_id + ", img = " + img;
    }
}
