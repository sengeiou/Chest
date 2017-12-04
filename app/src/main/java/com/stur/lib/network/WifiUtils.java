package com.stur.lib.network;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.stur.lib.Log;

public class WifiUtils {
    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.')+1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    /*单独获取wifi的IP地址*/
    public static String getIp(Context c) {
        WifiManager wifiManager = (WifiManager) c
                .getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = NetworkUtils.intToIp(ipAddress);
        Log.d(getTag(), "ip = " + ip);
        return ip;
    }

    /**
     * 获取wifi mac 用这种方式获取WIFI MAC地址在没有开启过WIFI的情况下是获取不到的
     * Manifest.permission.LOCAL_MAC_ADDRESS must configed, or default mac(02:00:00:00:00:00) returned
     */
    public static String getWifiMac(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress() == null ? "" : info.getMacAddress();
    }

    public String getWifiIP(Context context) {
        // 在wifi未开启状态下，仍然可以获取MAC地址，但是IP地址必须在已连接状态下否则为0
        String ip = null;
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
        if (null != info) {
            ip = NetworkUtils.int2ip(info.getIpAddress());
        }
        Log.d(getTag(), "getWifiIP:" + ip);
        return ip;
    }


    /**
     * 获取路由器的SSID
     *
     * @return
     */
    public static String getRouteSSID(Context context) {
        try {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wm.getConnectionInfo();
            if(info.getSSID().contains("<")){
                return "";
            }else{
                return info.getSSID().replace("\"","") + "";
            }
        } catch (Exception e) {
            Log.e(getTag(), "Exception: " + e.getMessage() + ", get ssid failed");
            return "";
        }
    }

    /**
     * 获取路由器的Mac地址
     *
     * @return
     */
    public static String getRouteMac(Context context) {
        try {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wm.getConnectionInfo();
            if(info.getBSSID() == null){
                return "";
            }else{
                return info.getBSSID() + "";
            }
        } catch (Exception e) {
            Log.e(getTag(), "Exception: " + e.getMessage() + ", get router mac failed");
            return "";
        }
    }
}
