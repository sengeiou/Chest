package com.stur.chest.dto;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/27.
 */
public class SubProductList extends ArrayList<SubProductDTO> {
    public String getColorImgUrlByColorName(@NonNull String color) {
        SubProductDTO subProduct;
        for (int i = 0; i < size(); i++) {
            subProduct = get(i);
            if (color.equals(subProduct.getColor())) {
                return subProduct.getImg();
            }
        }
        return "";
    }

    public int getItemPositionBySizeAndColor(@NonNull String color, @NonNull String size) {
        SubProductDTO subProduct;
        for (int i = 0; i < size(); i++) {
            subProduct = get(i);
            if (color.equals(subProduct.getColor()) && size.equals(subProduct.getSize())) {
                return i;
            }
        }
        return -1;
    }

    public int getStockBySizeAndColor(@NonNull String color, @NonNull String size) {
        int position = getItemPositionBySizeAndColor(color, size);
        if (position < 0) {
            return 0;
        } else {
            return get(position).getStock();
        }
    }

    public int getSubProductIdBySizeAndColor(@NonNull String color, @NonNull String size) {
        int position = getItemPositionBySizeAndColor(color, size);
        if (position < 0) {
            return -1;
        } else {
            return get(position).getId();
        }
    }

    public String getPriceBySizeString (@NonNull String size) {
        SubProductDTO subProduct;
        for (int i = 0; i < size(); i++) {
            subProduct = get(i);
            if (size.equals(subProduct.getSize())) {
                return subProduct.getMy_price();
            }
        }
        return "0.00";
    }
}
