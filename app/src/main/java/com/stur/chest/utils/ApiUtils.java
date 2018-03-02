package com.stur.chest.utils;


import com.stur.lib.web.request.HttpMethod;
import com.stur.lib.web.request.HttpRequest;

/**
 * Created by Administrator on 2016/3/12.
 */
public class ApiUtils {
    /**
     * Response keywords.
     */
    public static final String KEY_RET = "ret";
    public static final String KEY_MSG = "msg";
    public static final String KEY_DATA = "data";

    /**
     * Request keywords.
     */
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_RE_PASSWORD = "repassword";
    public static final String KEY_NEW_PASSWORD = "new_password";
    public static final String KEY_OLD_PASSWORD = "old_password";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_ADDR_ID = "addr_id";
    public static final String KEY_COUNTRY = "country";
    public static final String KEY_TEL_NO = "tel_no";
    public static final String KEY_FAX_NO = "fax_no";
    public static final String KEY_ZIPCODE = "zipcode";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PHONE = "phone";

    public static final String KEY_CATEGORY = "cate";
    public static final String KEY_CATEGORY_ID = "cate_id";
    public static final String KEY_GOODS_ID = "goods_id";
    public static final String KEY_ITEM_ID = "item_id";
    public static final String KEY_NUM = "num";
    public static final String KEY_PRICE_ID = "price_id";
    public static final String KEY_CURRENCY = "currency";
    public static final String KEY_ORDER_ID = "order_id";
    public static final String KEY_STATUS = "status";
    public static final String KEY_PAGE = "page";
    public static final String KEY_SIZE = "size";

    public static final String KEY_MESSAGE_ID = "message_id";

    protected static final String SERVER_IP = "http://47.88.84.93";
    protected static final String API_SIGN_UP = "/user/register";
    protected static final String API_SIGN_IN = "/user/login";
    protected static final String API_MODIFY_PASSWORD = "/user/password";
    protected static final String API_LOGOUT = "/user/logout";

    protected static final String API_USER_INFO = "/user/info";
    protected static final String API_GET_COUNTRIS = "/addr/country";
    protected static final String API_ADD_ADDR = "/addr/add";
    protected static final String API_UPDATE_ADDR = "/addr/edit";
    protected static final String API_DELETE_ADDR = "/addr/delete";

    protected static final String API_GET_ADDRLIST = "/addr/list";
    protected static final String API_GET_CATEGORYLIST = "/goods/cate";
    protected static final String API_GET_GOODLIST = "/goods/list";
    protected static final String API_GET_GOODDETAIL = "/goods/detail";
    protected static final String API_CREATE_ORDER = "/order/create";

    protected static final String API_GET_ORDERDETAIL = "/order/detail";
    protected static final String API_DELETE_ORDER = "/order/cancel";

    protected static final String API_GET_ORDERLIST = "/order/list";
    protected static final String API_GET_MSGLIST = "/message/list";
    protected static final String API_GET_MSGDETAIL = "/message/detail";

    public static String getServerIp() {
        return SERVER_IP;
    }

    public static HttpRequest getApiSignUp() {
        return new HttpRequest(SERVER_IP + API_SIGN_UP, HttpMethod.POST);
    }

    public static HttpRequest getApiSignIn() {
        return new HttpRequest(SERVER_IP + API_SIGN_IN, HttpMethod.POST);
    }

    public static HttpRequest getModifyPWApi() {
        return new HttpRequest(SERVER_IP + API_MODIFY_PASSWORD, HttpMethod.POST);
    }

    public static HttpRequest getLogoutApi() {
        return new HttpRequest(SERVER_IP + API_LOGOUT, HttpMethod.POST);
    }

    public static HttpRequest getUserInfoApi() {
        return new HttpRequest(SERVER_IP + API_USER_INFO, HttpMethod.POST);
    }
    public static HttpRequest getCountryListApi() {
        return new HttpRequest(SERVER_IP + API_GET_COUNTRIS, HttpMethod.POST);
    }

    public static HttpRequest getAddAddrApi() {
        return new HttpRequest(SERVER_IP + API_ADD_ADDR, HttpMethod.POST);
    }
    public static HttpRequest getUpdateAddrApi(){
        return new HttpRequest(SERVER_IP + API_UPDATE_ADDR, HttpMethod.POST);
    }

    public static HttpRequest getDeleteAddrApi() {
        return new HttpRequest(SERVER_IP + API_DELETE_ADDR, HttpMethod.POST);
    }

    public static HttpRequest getAddrListApi() {
        return new HttpRequest(SERVER_IP + API_GET_ADDRLIST, HttpMethod.POST);
    }

    public static HttpRequest getCategoryListApi() {
        return new HttpRequest(SERVER_IP + API_GET_CATEGORYLIST, HttpMethod.POST);
    }

    public static HttpRequest getGoodListApi() {
        return new HttpRequest(SERVER_IP + API_GET_GOODLIST, HttpMethod.POST);
    }
    public static HttpRequest getGoodDetailApi() {
        return new HttpRequest(SERVER_IP + API_GET_GOODDETAIL, HttpMethod.POST);
    }
    public static HttpRequest getCreateOrderApi() {
        return new HttpRequest(SERVER_IP + API_CREATE_ORDER, HttpMethod.POST);
    }
    public static HttpRequest getOrderDetailApi() {
        return new HttpRequest(SERVER_IP + API_GET_ORDERDETAIL, HttpMethod.POST);
    }
    public static HttpRequest getCancelOrderApi() {
        return new HttpRequest(SERVER_IP + API_DELETE_ORDER, HttpMethod.POST);
    }
    public static HttpRequest getOrderListApi() {
        return new HttpRequest(SERVER_IP + API_GET_ORDERLIST, HttpMethod.POST);
    }
    public static HttpRequest getMsgListApi() {
        return new HttpRequest(SERVER_IP + API_GET_MSGLIST, HttpMethod.POST);
    }
    public static HttpRequest getMsgDetailApi() {
        return new HttpRequest(SERVER_IP + API_GET_MSGDETAIL, HttpMethod.POST);
    }

}
