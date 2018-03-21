package com.stur.lib.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.widget.Toast;

import com.stur.lib.Log;
import com.stur.lib.network.NetworkUtils;
import com.stur.lib.network.TMUtils;
import com.stur.lib.network.WifiUtils;
import com.stur.lib.web.HttpServer;

//import org.apache.http.client.ClientProtocolException;

public class SocketService extends Service {
    /*注册的url*/
    public static final String registerUrl = "http://192.168.1.110:8080/HttpServer/RegisterServlets";

    /*备用端口*/
    public static int[] portAry = new int[]{23021,10034,48990};

    /*默认端口*/
    public static int defaultPort = 23021;

    private int port = defaultPort;

    //网络开关状态改变的监听
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                //注册接口
                new Thread(){
                    @Override
                    public void run(){
                        //开始注册
                        register();
                    }
                }.start();
            }
        }
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(this, "Complete");

        //开启监听端口
        HttpServer hs = new HttpServer(this);
        try{
            for(int i=0;i<portAry.length;i++){
                if(NetworkUtils.checkPort(portAry[i])){
                    port = portAry[i];
                    break;
                }
            }
            hs.execute(port);
            register();
        }catch(Exception e){
        }

        //注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
        Toast.makeText(this,"Web Server Started", Toast.LENGTH_SHORT).show();
    }

    /**
     * 注册ip地址
     * @return
     */
    public boolean register(){
        //拼接参数
        StringBuilder param = new StringBuilder();
        param.append("imei=");
        param.append(TMUtils.getImeiInfo(this));
        param.append("&hs_ip=");
        param.append(NetworkUtils.getLocalIpAddress()+":"+port);
        param.append("&route_mac=");
        param.append(WifiUtils.getRouteMac(this));
        param.append("&route_ssid=");
        param.append(WifiUtils.getRouteSSID(this));
        param.append("&timetag=");
        param.append(System.currentTimeMillis()+"");

        boolean flag = false;

        //上报数据
        /*if(NetworkUtils.checkNetWorkState(this)){
            try {
                flag = HttpUtils.uploadRequest(registerUrl, param.toString());
                if(flag){
                    Log.d(this, "register success");
                }else{
                    Log.d(this, "register failed");
                }
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            } catch(Exception e){
            }
        }*/
        return flag;
    }

}
