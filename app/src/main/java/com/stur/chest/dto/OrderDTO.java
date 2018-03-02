package com.stur.chest.dto;

/**
 * Created by Administrator on 2016/3/9.
 */
public class OrderDTO {
    protected int id = 0;
    protected int state = 0;
    protected int dateline = 0;
    protected String telephone = "";
    protected String address = "";
    protected String amount = "";


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setDateline(int dateline) {
        this.dateline = dateline;
    }

    public int getDateline() {
        return dateline;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Order info : [id]").append(id)
                .append("; [state]").append(state)
                .append("; [dateline]").append(dateline)
                .append("; [telephone]").append(telephone)
                .append("; [address]").append(address)
                .append("; [amount]").append(amount);
        return sb.toString();
    }
}
