package com.stur.lib.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class IntentUtils {
    public static BroadcastReceiver sBroadcastReceiver;

    public static class IntentReceivedCallback {
        public void onIntentReceived(Intent intent) {}
    }

    public static void registerIntent(Context context, String intent, final IntentReceivedCallback ircb) {
        sBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                ircb.onIntentReceived(intent);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(intent);
        context.registerReceiver(sBroadcastReceiver, filter);
    }

    /**
     * 一次注册多个intent
     * @param context
     * @param intents
     * @param ircb
     */
    public static void registerIntent(Context context, String[] intents, final IntentReceivedCallback ircb) {
        sBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                ircb.onIntentReceived(intent);
            }
        };
        IntentFilter filter = new IntentFilter();
        for (String intent : intents) {
            filter.addAction(intent);
        }
        context.registerReceiver(sBroadcastReceiver, filter);
    }

    public static void unregisterIntent(Context context) {
        context.unregisterReceiver(sBroadcastReceiver);
    }

    /**
     * 发送 un.intent.action.SHOW_NUM_CHANGED 广播给LauncherL，控制未读角标数量
     */
    public static int sCount = 1;
    public static void sendUnreadedNumIntent(Context context) {
        Intent intent = new Intent("un.intent.action.SHOW_NUM_CHANGED");
        intent.putExtra("packageName", "com.urovo.clouddesktop");  //应用的包名
        intent.putExtra("className", "com.urovo.clouddesktop.ui.home.view.MainActivityNew");   // Launcher上对应的Activity名
        intent.putExtra("showNum", sCount % 4);     //显示的未读角标数量，当数量为0时不显示角标
        sCount++;
        context.sendBroadcast(intent);
    }
}
