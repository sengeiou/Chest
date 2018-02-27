package com.stur.lib.activity;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.stur.lib.constant.StConstant;
import com.stur.lib.R;
import com.stur.lib.Log;
import com.stur.lib.network.SendDateToServer;
import com.stur.lib.network.WiFiAdmin;
import com.stur.lib.network.WiFiP2PAdmin;

import java.util.List;

public class WiFiActivity extends Activity {
    /** Called when the activity is first created. */
    private TextView allNetWork;
    private Button scan;
    private Button start;
    private Button stop;
    private Button check;
    private Button mP2PGOSetting;
    private Button mP2PGOConnector;
    private Button mP2PSearch;
    private Button mP2PDisconn;
    private WiFiAdmin mWifiAdmin;
    private WiFiP2PAdmin mWifiP2PAdmin;
    private int mWiFiMode = StConstant.WIFI_RM_UNKNOWN;

    private MyListener mListener;
    // 扫描结果列表
    private List<ScanResult> list;
    private ScanResult mScanResult;
    private StringBuffer sb = new StringBuffer();
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SendDateToServer.SEND_SUCCESS:
                    Toast.makeText(WiFiActivity.this, "发送成功!", Toast.LENGTH_SHORT).show();
                    break;
                case SendDateToServer.SEND_FAIL:
                    Toast.makeText(WiFiActivity.this, "发送失败!", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        };
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        mWifiAdmin = new WiFiAdmin(WiFiActivity.this);
        mListener = new MyListener();
        init();
    }

    public void init() {
        allNetWork = (TextView) findViewById(R.id.allNetWork);
        scan = (Button) findViewById(R.id.scan);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        check = (Button) findViewById(R.id.check);
        mP2PGOSetting = (Button) findViewById(R.id.bt_wifi_p2p_go);
        mP2PGOConnector = (Button) findViewById(R.id.bt_wifi_p2p_conn);
        mP2PDisconn = (Button) findViewById(R.id.bt_wifi_p2p_disconn);
        mP2PSearch = (Button) findViewById(R.id.bt_wifi_p2p_search);

        scan.setOnClickListener(mListener);
        start.setOnClickListener(mListener);
        stop.setOnClickListener(mListener);
        check.setOnClickListener(mListener);
        mP2PGOSetting.setOnClickListener(mListener);
        mP2PGOConnector.setOnClickListener(mListener);
        mP2PSearch.setOnClickListener(mListener);
        mP2PDisconn.setOnClickListener(mListener);
    }

    private class MyListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v.getId() == R.id.scan) { // 扫描网络
                //setWifiRunMode(Constant.WIFI_RM_STA);
                getAllNetWorkList();
            }  else if (v.getId() == R.id.start) {  // 打开Wifi
                //mWifiAdmin.openWifi();
                //Toast.makeText(WiFiActivity.this, "当前wifi状态为：" + mWifiAdmin.checkState(), 1).show();
                mWifiAdmin.connectAP();
            } else if (v.getId() == R.id.stop) {    //关闭Wifi
                mWifiAdmin.closeWifi();
                Toast.makeText(WiFiActivity.this, "当前wifi状态为：" + mWifiAdmin.checkState(), Toast.LENGTH_SHORT).show();
            }  else if (v.getId() == R.id.check) {    // Wifi状态
                Toast.makeText(WiFiActivity.this, "当前wifi状态为：" + mWifiAdmin.checkState(), Toast.LENGTH_SHORT).show();
            }  else if (v.getId() == R.id.bt_wifi_p2p_go) {
                String id = Settings.Secure.getString(WiFiActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                mWifiP2PAdmin.setDeviceName(StConstant.WIFI_P2P_PREFIX_DEVICE_NAME + id.substring(0,4));
                mWifiP2PAdmin.startAutoGO();
            }  else if (v.getId() == R.id.bt_wifi_p2p_search) {
                mWifiP2PAdmin.startP2PSearch();
            }  else if (v.getId() == R.id.bt_wifi_p2p_conn) {
                mWifiP2PAdmin.connectToPeer(new WifiP2pDevice());  //just for compile
            }  else if (v.getId() == R.id.bt_wifi_p2p_disconn) {
                mWifiP2PAdmin.disconnect();
            }
        }
    }

    public void getAllNetWorkList() {
        // 每次点击扫描之前清空上一次的扫描结果
        if (sb != null) {
            sb = new StringBuffer();
        }
        // 开始扫描网络
        mWifiAdmin.startScan();
        list = mWifiAdmin.getWifiList();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                // 得到扫描结果
                mScanResult = list.get(i);
                String macaddress = new String();
                macaddress = mScanResult.BSSID;
                new SendDateToServer(mHandler).SendDataToServer(macaddress);// 发送到服务器，写入数据库
                sb = sb.append(mScanResult.BSSID + "  ").append(mScanResult.SSID + "   ")
                        .append(mScanResult.capabilities + "   ").append(mScanResult.frequency + "   ")
                        .append(mScanResult.level + "\n\n");
            }
            allNetWork.setText("扫描到的wifi网络：\n" + sb.toString());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mWifiAdmin.stopAutoGO();
        //mWifiAdmin.stopPeerDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mWifiP2PAdmin != null) {
            mWifiP2PAdmin.unRegister();
        }
    }

    public synchronized void setWifiRunMode(int mode) {
        Log.d(this, "setWifiRunMode: " + mode);
        mWiFiMode = mode;
    }

    public synchronized int getWifiRunMode() {
        Log.d(this, "getWifiRunMode: " + mWiFiMode);
        return mWiFiMode;
    }
}
