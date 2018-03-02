package com.stur.chest.dto;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/27.
 */
public class AbstractOrder implements Serializable {
    private int id;
    private int num;
    private String img;
    private String name;
    private String color;
    private String size;
    private String totalPrice;
    private AddressDTO addressDTO;

    public AbstractOrder(int id, int num, String img, String name, String color, String size, String totalPrice) {
        this.id = id;
        this.num = num;
        this.img = img;
        this.name = name;
        this.color = color;
        this.size = size;
        this.totalPrice = totalPrice;
    }

    public int getId() {
        return id;
    }

    public int getNum() {
        return num;
    }

    public String getImg() {
        return img;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getSize() {
        return size;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public AddressDTO getAddressDTO() {
        return addressDTO;
    }

    public void setAddressDTO(AddressDTO addressDTO) {
        this.addressDTO = addressDTO;
    }
}
