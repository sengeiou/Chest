package com.stur.chest.dto;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/9.
 */
public class AddressDTO implements Serializable {
    protected int id;
    protected int user_id;
    protected String name;
    protected String phone;
    protected String country;
    protected String address;
    protected String zipcode;
    protected int disabled;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getZipcode() {
        return zipcode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Address info : [id]").append(id)
                .append("; [name]").append(name)
                .append("; [phone]").append(phone)
                .append("; [country]").append(country)
                .append("; [zipcode]").append(zipcode)
                .append("; [address]").append(address);
        return sb.toString();
    }
}
