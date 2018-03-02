package com.stur.chest.dto;

import com.stur.lib.IntegerUtil;
import com.stur.lib.exception.ParameterException;

import java.io.Serializable;

public class UserAccountDTO implements Serializable {

    private int id;
    private String name;
    private String email;
    private int address_id;
    private int status;
    private String login_ip;
    private String create_at;
    private String token;
    private String country;
    private String tel_no;
    private String fax_no;

    public int getId() {
        return id;
    }

    public void setId(String id) throws ParameterException {
        this.id = IntegerUtil.getInt(id, "User'd identifier");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) throws ParameterException {
        try {
            this.address_id = IntegerUtil.getInt(address_id);
        } catch (ParameterException e) {
            this.address_id = -1;
            throw new ParameterException("Address identifier");
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(String status) throws ParameterException {
        this.status = IntegerUtil.getInt(status, "user status");
    }

    public String getLogin_ip() {
        return login_ip;
    }

    public void setLogin_ip(String login_ip) {
        this.login_ip = login_ip;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCountry() {
        return this.country;
    }

    public String getTel_no() {
        return this.tel_no;
    }

    public String getFax_no() {
        return this.fax_no;
    }
}
