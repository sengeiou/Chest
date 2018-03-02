package com.stur.chest.dto;

/**
 * Created by Administrator on 2016/3/28.
 */
public class SizePriceInfo {
    private String size;
    private String price;

    public SizePriceInfo(String size, String price) {
        this.size = size;
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public String getPrice() {
        return price;
    }
}
