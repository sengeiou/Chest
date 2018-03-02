package com.stur.chest.modules;


import com.stur.chest.utils.ApiUtils;
import com.stur.lib.Log;
import com.stur.lib.web.HttpFactory;
import com.stur.lib.web.base.HttpListener;
import com.stur.lib.web.request.HttpRequest;

/**
 * Created by Administrator on 2016/3/15.
 */
public class OrderModule {
    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.')+1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    public static void getOrderList(String token, int page, int size, HttpListener httpListener) {
        HttpRequest httpRequest = ApiUtils.getOrderListApi();
        httpRequest.addParams(ApiUtils.KEY_TOKEN, token);
        httpRequest.addParams(ApiUtils.KEY_PAGE, String.valueOf(page));
        httpRequest.addParams(ApiUtils.KEY_SIZE, String.valueOf(size));
        HttpFactory.getHttpService().sendRequest(httpRequest, httpListener);
    }
    public static void getOrderList(String token, int status, int page, int size, HttpListener httpListener) {
        HttpRequest httpRequest = ApiUtils.getOrderListApi();
        httpRequest.addParams(ApiUtils.KEY_TOKEN, token);
        httpRequest.addParams(ApiUtils.KEY_STATUS, String.valueOf(status));
        httpRequest.addParams(ApiUtils.KEY_PAGE, String.valueOf(page));
        httpRequest.addParams(ApiUtils.KEY_SIZE, String.valueOf(size));
        HttpFactory.getHttpService().sendRequest(httpRequest, httpListener);
    }

    public static void getOrderDetail(String token, int orderId, HttpListener httpListener) {
        HttpRequest httpRequest = ApiUtils.getOrderDetailApi();
        httpRequest.addParams(ApiUtils.KEY_TOKEN, token);
        httpRequest.addParams(ApiUtils.KEY_ORDER_ID, String.valueOf(orderId));
        HttpFactory.getHttpService().sendRequest(httpRequest, httpListener);
    }

    public static void createOrder(String token, int addrId, int itemId, int count,
                                   HttpListener httpListener) {
        Log.d(getTag(), "[Params]token:" + token + ", addId:" + addrId + ", itemId:" + itemId + ", count:"
                +count);
        HttpRequest httpRequest = ApiUtils.getCreateOrderApi();
        httpRequest.addParams(ApiUtils.KEY_TOKEN, token);
        httpRequest.addParams(ApiUtils.KEY_ADDR_ID, String.valueOf(addrId));
        httpRequest.addParams(ApiUtils.KEY_ITEM_ID, String.valueOf(itemId));
        httpRequest.addParams(ApiUtils.KEY_NUM, String.valueOf(count));
        httpRequest.addParams(ApiUtils.KEY_CURRENCY, "USD");
        HttpFactory.getHttpService().sendRequest(httpRequest, httpListener);
    }

    public static void cancelOrder(String token, int orderId, HttpListener httpListener) {
        HttpRequest httpRequest = ApiUtils.getCancelOrderApi();
        httpRequest.addParams(ApiUtils.KEY_TOKEN, token);
        httpRequest.addParams(ApiUtils.KEY_ORDER_ID, String.valueOf(orderId));
        HttpFactory.getHttpService().sendRequest(httpRequest, httpListener);
    }
}
