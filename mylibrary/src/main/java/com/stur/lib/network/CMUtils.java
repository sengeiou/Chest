package com.stur.lib.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;

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
}
