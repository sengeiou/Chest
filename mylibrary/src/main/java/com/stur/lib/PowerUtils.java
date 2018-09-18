package com.stur.lib;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Sturmegezhutz on 2018/5/30.
 */

public class PowerUtils {
    /**
     * 原生的关机广播： Intent.ACTION_REQUEST_SHUTDOWN : "android.intent.action.ACTION_REQUEST_SHUTDOWN"
     * 原生的重启广播： Intent.ACTION_REBOOT : ""android.intent.action.REBOOT""
     */
    public static final String ACTION_REQUEST_SHUTDOWN = "android.intent.action.ACTION_REQUEST_SHUTDOWN";
    public static final String ACTION_REBOOT = "android.intent.action.REBOOT";

    public static void reboot(Context context) {
        Intent intent = new Intent(Intent.ACTION_REBOOT);
        intent.putExtra("nowait", 1);
        intent.putExtra("interval", 1);
        intent.putExtra("window", 0);
        context.sendBroadcast(intent);
    }
}
