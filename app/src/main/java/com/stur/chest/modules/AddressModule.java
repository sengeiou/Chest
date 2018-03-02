package com.stur.chest.modules;

import com.stur.chest.utils.ApiUtils;
import com.stur.lib.web.HttpFactory;
import com.stur.lib.web.base.HttpListener;
import com.stur.lib.web.request.HttpRequest;


public class AddressModule {
    public static void getCountryList(HttpListener httpListener) {
        HttpRequest httpRequest = ApiUtils.getCountryListApi();
        HttpFactory.getHttpService().sendRequest(httpRequest, httpListener);
    }

    public static void addAddress(String token, String name, String country, String zipcode,
                                  String address, String phone, HttpListener httpListener) {
        HttpRequest httpRequest = ApiUtils.getAddAddrApi();
        httpRequest.addParams(ApiUtils.KEY_TOKEN, token);
        httpRequest.addParams(ApiUtils.KEY_NAME, name);
        httpRequest.addParams(ApiUtils.KEY_COUNTRY, country);
        httpRequest.addParams(ApiUtils.KEY_ZIPCODE, zipcode);
        httpRequest.addParams(ApiUtils.KEY_ADDRESS, address);
        httpRequest.addParams(ApiUtils.KEY_PHONE, phone);
        HttpFactory.getHttpService().sendRequest(httpRequest, httpListener);
    }

    public static void updateAddress(String token, int addr_id, String name, String country, String zipcode,
                                     String address, String phone, HttpListener httpListener) {
        HttpRequest httpRequest = ApiUtils.getUpdateAddrApi();
        httpRequest.addParams(ApiUtils.KEY_TOKEN, token);
        httpRequest.addParams(ApiUtils.KEY_ADDR_ID, String.valueOf(addr_id));
        httpRequest.addParams(ApiUtils.KEY_NAME, name);
        httpRequest.addParams(ApiUtils.KEY_COUNTRY, country);
        httpRequest.addParams(ApiUtils.KEY_ZIPCODE, zipcode);
        httpRequest.addParams(ApiUtils.KEY_ADDRESS, address);
        httpRequest.addParams(ApiUtils.KEY_PHONE, phone);
        HttpFactory.getHttpService().sendRequest(httpRequest, httpListener);
    }

    public static void deleteAddress(String token, int addr_id, HttpListener httpListener) {
        HttpRequest httpRequest = ApiUtils.getDeleteAddrApi();
        httpRequest.addParams(ApiUtils.KEY_TOKEN, token);
        httpRequest.addParams(ApiUtils.KEY_ADDR_ID, String.valueOf(addr_id));
        HttpFactory.getHttpService().sendRequest(httpRequest, httpListener);
    }

    public static void getAddressList(String token, HttpListener httpListener) {
        HttpRequest httpRequest = ApiUtils.getAddrListApi();
        httpRequest.addParams(ApiUtils.KEY_TOKEN, token);
        HttpFactory.getHttpService().sendRequest(httpRequest, httpListener);
    }
}
