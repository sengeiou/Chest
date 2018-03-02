package com.stur.chest.modules;


import com.stur.chest.utils.ApiUtils;
import com.stur.lib.web.HttpFactory;
import com.stur.lib.web.base.HttpListener;
import com.stur.lib.web.request.HttpRequest;

/**
 * Created by Administrator on 2016/3/12.
 */
public class UserModule {

    /**
     * 用户登录
     *
     * @param email
     * @param password
     * @param httpListener
     */
    public static void login(String email, String password, HttpListener httpListener) {
        HttpRequest httpRequest = ApiUtils.getApiSignIn();
        httpRequest.addParams(ApiUtils.KEY_EMAIL, email);
        httpRequest.addParams(ApiUtils.KEY_PASSWORD, password);
        HttpFactory.getHttpService().sendRequest(httpRequest, httpListener);
    }

    public static void register(String name, String email, String country, String telNo, String faxNo,
                                String password, HttpListener httpListener) {
        HttpRequest httpRequest = ApiUtils.getApiSignUp();
        httpRequest.addParams(ApiUtils.KEY_NAME, name);
        httpRequest.addParams(ApiUtils.KEY_EMAIL, email);
        httpRequest.addParams(ApiUtils.KEY_COUNTRY, country);
        httpRequest.addParams(ApiUtils.KEY_TEL_NO, telNo);
        httpRequest.addParams(ApiUtils.KEY_FAX_NO, faxNo);
        httpRequest.addParams(ApiUtils.KEY_PASSWORD, password);
        httpRequest.addParams(ApiUtils.KEY_RE_PASSWORD, password);
        HttpFactory.getHttpService().sendRequest(httpRequest, httpListener);
    }

    public static void modifyPassword(String token, String oldPassword, String newPassword, HttpListener httpListener) {
        HttpRequest httpRequest = ApiUtils.getModifyPWApi();
        httpRequest.addParams(ApiUtils.KEY_TOKEN, token);
        httpRequest.addParams(ApiUtils.KEY_OLD_PASSWORD, oldPassword);
        httpRequest.addParams(ApiUtils.KEY_NEW_PASSWORD, newPassword);
        HttpFactory.getHttpService().sendRequest(httpRequest, httpListener);
    }

    public static void logout(String token, HttpListener httpListener) {
        HttpRequest httpRequest = ApiUtils.getLogoutApi();
        httpRequest.addParams(ApiUtils.KEY_TOKEN, token);
        HttpFactory.getHttpService().sendRequest(httpRequest, httpListener);
    }

    public static void getUserInfo(String token, HttpListener httpListener) {
        HttpRequest httpRequest = ApiUtils.getUserInfoApi();
        httpRequest.addParams(ApiUtils.KEY_TOKEN, token);
        HttpFactory.getHttpService().sendRequest(httpRequest, httpListener);
    }

}
