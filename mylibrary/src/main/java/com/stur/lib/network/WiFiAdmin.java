package com.stur.lib.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

import com.stur.lib.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import android.os.Registrant;
//import android.os.RegistrantList;

public class WiFiAdmin {
    private static WiFiAdmin sWiFiAdmin;

    //protected RegistrantList mAPConnectedRegistrants = new RegistrantList();
    //protected RegistrantList mAPDisconnectedRegistrants = new RegistrantList();

    // 定义一个WifiManager对象
    private WifiManager mWifiManager;
    // 定义一个WifiInfo对象
    private WifiInfo mWifiInfo;
    // 扫描出的网络连接列表
    private List<ScanResult> mScanRstList;
    // 网络连接列表
    private List<WifiConfiguration> mWifiConfigurations;
    WifiLock mWifiLock;

    private Context mContext;
    public  List<Map<String,String>>mlist = new ArrayList<Map<String,String>>();
    /**
     * 当搜索到可用wifi时，将结果封装到mlist中
     */
    private final BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                mScanRstList = mWifiManager.getScanResults();
                Log.d(this, "onReceive: mScanRstList = " + mScanRstList);
                sortByLevel(mScanRstList);
                for (ScanResult scanResult : mScanRstList) {
                    Map<String,String>map = new HashMap<String, String>();
                    map.put("wifi_name",scanResult.SSID);
                    map.put("wifi_bssid",scanResult.BSSID);
                    mlist.add(map);
                }
            }

        }
    };

    public WiFiAdmin(Context context) {
        mContext = context;
        // 取得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
        context.registerReceiver(mWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public static WiFiAdmin init(Context context) {
        sWiFiAdmin = new WiFiAdmin(context);
        return sWiFiAdmin;
    }

    public static WiFiAdmin getInstance() {
        if (sWiFiAdmin == null) {
            throw new RuntimeException(
                    "WiFiAdmin.getInstance can't be called before inited()");
        }
        return sWiFiAdmin;
    }

    // 打开wifi
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    // 关闭wifi
    public void closeWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    // 检查当前wifi状态
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    // 锁定wifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    // 解锁wifiLock
    public void releaseWifiLock() {
        // 判断是否锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    // 创建一个wifiLock
    public void createWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("test");
    }

    // 得到配置好的网络
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfigurations;
    }

    //连接到一个指定的AP
    public void connectAP() {
        //这里建议使用bssid作为标识，因为ssid可鞥需要转义
        final String ssid = "\"Coolpad\"";
        Log.d(this, "connectAP: " + ssid);
        mWifiConfigurations = mWifiManager.getConfiguredNetworks();
        for(WifiConfiguration wc : mWifiConfigurations) {
            Log.d(this, "connectAP " + wc.SSID);
            if (wc.SSID.equals(ssid)) {
                /*mWifiManager.connect(wc, new WifiManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        Log.d(this, "connectAP success");
                    }

                    @Override
                    public void onFailure(int reason) {
                        // TODO Auto-generated method stub
                        Log.d(this, "connectAP failed: " + reason);
                    }
                });*/
            }
        }
    }

    // 指定配置好的网络进行连接
    public void connetionConfiguration(int index) {
        if (index > mWifiConfigurations.size()) {
            return;
        }
        // 连接配置好指定ID的网络
        mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId, true);
    }

    public void startScan() {
        mWifiManager.startScan();
        // 得到扫描结果
        //mScanRstList = mWifiManager.getScanResults();
        // 得到配置好的网络连接
        //mWifiConfigurations = mWifiManager.getConfiguredNetworks();
    }

    // 得到网络列表
    public List<ScanResult> getWifiList() {
        return mScanRstList;
    }

    // 查看扫描结果
    public StringBuffer lookUpScan() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mScanRstList.size(); i++) {
            sb.append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            sb.append((mScanRstList.get(i)).toString()).append("\n");
        }
        return sb;
    }

    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    public int getIpAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    // 得到连接的ID
    public int getNetWordId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    // 得到wifiInfo的所有信息
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    // 添加一个网络并连接
    public void addNetWork(WifiConfiguration configuration) {
        int wcgId = mWifiManager.addNetwork(configuration);
        mWifiManager.enableNetwork(wcgId, true);
    }

    // 断开指定ID的网络
    public void disconnectionWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    //将搜索到的wifi根据信号强度从强到弱进行排序
    private void sortByLevel(List<ScanResult> resultList) {
        for(int i=0;i<resultList.size();i++)
            for(int j=1;j<resultList.size();j++)
            {
                if(resultList.get(i).level<resultList.get(j).level)    //level属性即为强度
                {
                    ScanResult temp = null;
                    temp = resultList.get(i);
                    resultList.set(i, resultList.get(j));
                    resultList.set(j, temp);
                }
            }
    }

    /*public void registerForAPConnected(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        mAPConnectedRegistrants.add(r);
    }

    public void unregisterForAPConnected(Handler h) {
        mAPConnectedRegistrants.remove(h);
    }

    public void registerForAPDisConnected(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        mAPDisconnectedRegistrants.add(r);
    }
    public void unregisterForAPDisConnected(Handler h) {
        mAPDisconnectedRegistrants.remove(h);
    }*/
}
