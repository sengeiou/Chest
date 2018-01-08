package com.stur.lib.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

import com.stur.lib.Log;

public class CMUtils {
    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.') + 1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    ConnectivityManager mConn;
    NetworkCallback mNetworkCallback;
    Context mContext;
    BroadcastReceiver mDataReceiver;

    public CMUtils(Context context) {
        // TODO Auto-generated constructor stub
        mContext = context;
        mNetworkCallback = new NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                Log.d(this, "NetworkCallbackChild onAvailable");
                // do something
            }

            @Override
            public void onLost(Network network) {
                Log.d(this, "NetworkCallbackChild onLost");
            }
        };

        /*mConn = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        int requestId = -1;
        NetworkCapabilities nc = new NetworkCapabilities();
        nc.addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED);
        NetworkRequest nr = new NetworkRequest(nc, ConnectivityManager.TYPE_MOBILE, requestId,
                NetworkRequest.Type.LISTEN);
        mConn.registerNetworkCallback(nr, mNetworkCallback);

        mDataReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.d(this, "onReceive: " + action);
            }
        };*/
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mDataReceiver, filter);// 注册Broadcast Receiver
    }

    /*
    * <uses-permission
    * android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
    * return ssid if wifi connected, or preferapn if cellular data enabled
    */
    public static String getSsidOrPreferApn(Context context) {
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = conManager.getActiveNetworkInfo();
        String apn = ni.getExtraInfo();// 获取网络接入点，这里一般为cmwap和cmnet
        return apn;
    }

    /**
     * Checking for all possible internet providers
     **/
    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
            return false;
        } else {
            if (connectivityManager != null) {
                // noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            Log.d(getTag(), "NETWORKNAME: " + anInfo.getTypeName());
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    public static String getPreferApn(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = cm.getAllNetworks();
        NetworkInfo ni;
        for (Network nw : networks) {
            ni = cm.getNetworkInfo(nw);
            if (ni.getType() == ConnectivityManager.TYPE_MOBILE && ni.getState().equals(NetworkInfo.State.CONNECTED)) {
                return ni.getExtraInfo();
            }
        }
        return null;
    }

}
