package com.stur.chest.dto;

/**
 * Created by Administrator on 2016/3/9.
 */
public class AbstractProductDTO{

    public static final String KEY_PRODUCT_NAME = "product_name";
    public static final String KEY_PRODUCT_ID = "product_id";
    public static final String KEY_PRODUCT_IMGS = "product_imgs";
    public static final String KEY_PRODUCT_PRICE_RANGE = "product_price_range";
    protected int id; // product id
    protected String name;  // product name
    private int cate_id; // category id
    private String cate;    // category name
    private String img;     // product img
    private String range_price;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCate_id(int cate_id) {
        this.cate_id = cate_id;
    }

    public int getCate_id() {
        return cate_id;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }

    public String getCate() {
        return cate;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImg() {
        return img;
    }

    public void setRange_price(String range_price) {
        this.range_price = range_price;
    }

    public String getRange_price() {
        return range_price;
    }
}
