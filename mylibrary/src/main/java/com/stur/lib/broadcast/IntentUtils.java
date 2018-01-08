package com.stur.lib.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.stur.lib.Utils;

public class IntentUtils {
    public static BroadcastReceiver sBroadcastReceiver;

    public static class IntentReceivedCallback {
        public void onIntentReceived(Intent intent) {}
    }

    public static void registerIntent(Context context, final IntentReceivedCallback ircb) {
        sBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                ircb.onIntentReceived(intent);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Utils.INTENT_DISPLAY);
        filter.addAction(Utils.INTENT_TEST);
        context.registerReceiver(sBroadcastReceiver, filter);
    }

    public static void unregisterIntent(Context context) {
        context.unregisterReceiver(sBroadcastReceiver);
    }
}
