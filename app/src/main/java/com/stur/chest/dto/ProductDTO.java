package com.stur.chest.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/3/27.
 */
public class ProductDTO extends AbstractProductDTO implements Serializable {
    public static final String KEY_FABRIC_MATERIAL = "fabric_material";
    public static final String KEY_FABRIC_WEIGHT = "fabric_weight";
    public static final String KEY_FABRIC_CUTTABLE_WIDTH = "fabric_cuttable_width";
    public static final String KEY_FABRIC_MOQ = "fabric_mqq";
    private String unit;
    private int online;
    private String intro;
    private String content;
    private int type;

    private String my_price;
    private String rmb_price;
    private int stock;
    private List<String> color;
    private List<String> size;
    private String weight;
    private String material;
    private SubProductList goods_list;
    private List<String> image_list;

    public int getOnline() {
        return online;
    }

    public int getType() {
        return type;
    }

    public int getStock() {
        return stock;
    }

    public String getUnit() {
        return unit;
    }

    public String getIntro() {
        return intro;
    }

    public String getContent() {
        return content;
    }

    public String getMy_price() {
        return my_price;
    }

    public String getWeight() {
        return weight;
    }

    public String getMaterial() {
        return material;
    }

    public List<String> getColor() {
        return color;
    }

    public List<String> getSize() {
        return size;
    }

    public SubProductList getGoods_list() {
        return goods_list;
    }

    public List<String> getImage_list(){
        return image_list;
    }
}
