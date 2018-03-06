package com.stur.lib.network;

/**
 * Created by guanxuejin on 2018/1/22.
 */

public interface OnTCPListener {
    void onConnectStatusChange(int status);
    void onMsgReceived(String str);
    void onMsgSended(String str);
    void onTextViewPrint(String str);
}
