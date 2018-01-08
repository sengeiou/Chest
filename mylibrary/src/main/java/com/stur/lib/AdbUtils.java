package com.stur.lib;

import android.content.Context;

import java.io.IOException;

public class AdbUtils {
    public static final String WIFI_ADB_DEFAULT_PORT = "5555";
    public static final String WIFI_ADB_PORT_PROP = "service.adb.tcp.port";

    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.')+1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    /*
    * enable adb under wifi connection
    * PC use adb connect a.b.c.d:port to connect adbd
    * PC use adb connect a.b.c.d:-1 to close wifiadb
    * if usbadb cannot used anymore after these steps, restart usb debug will be resumed
    */
    public static void enableWifiAdb(Context context, String port) throws IOException {
        Log.d(getTag(), "enableWifiAdb at port: " + port);
        //SystemProperties.set(WIFI_ADB_PORT_PROP, port != null ? port : WIFI_ADB_DEFAULT_PORT);
        SystemPropertiesProxy.set(context, WIFI_ADB_PORT_PROP, port != null ? port : WIFI_ADB_DEFAULT_PORT);
        //Utils.execCommand("setprop service.adb.tcp.port 5555");
        //we can not stop adbd here for Operation not permitted
        //Utils.execCommand("stop adbd");
        //Utils.execCommand("start adbd");
        if (WIFI_ADB_DEFAULT_PORT.equals(SystemPropertiesProxy.get(context, AdbUtils.WIFI_ADB_PORT_PROP))) {
        	Utils.startActivity(context, "com.android.settings", "com.android.settings.DevelopmentSettings", false);
        	Utils.makeToast(context, "plz reenable usb debug");
		} else {
			Utils.makeToast(context, AdbUtils.WIFI_ADB_PORT_PROP + ": " + SystemPropertiesProxy.get(context, AdbUtils.WIFI_ADB_PORT_PROP));
		}
    }
}
