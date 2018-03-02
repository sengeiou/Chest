package com.stur.chest.modules;

import com.stur.chest.utils.ApiUtils;
import com.stur.lib.web.HttpFactory;
import com.stur.lib.web.base.HttpListener;
import com.stur.lib.web.request.HttpRequest;

public class MessageModule {
    public static void getMessageList(int page, int size, HttpListener httpListener) {
        HttpRequest httpRequest = ApiUtils.getMsgListApi();
        httpRequest.addParams(ApiUtils.KEY_PAGE, String.valueOf(page));
        httpRequest.addParams(ApiUtils.KEY_SIZE, String.valueOf(size));
        HttpFactory.getHttpService().sendRequest(httpRequest, httpListener);
    }
    public static void getMessageDetail(int messageId, HttpListener httpListener) {
        HttpRequest httpRequest = ApiUtils.getMsgDetailApi();
        httpRequest.addParams(ApiUtils.KEY_MESSAGE_ID, String.valueOf(messageId));
        HttpFactory.getHttpService().sendRequest(httpRequest, httpListener);
    }
}
