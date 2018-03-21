package com.stur.lib;

import android.content.Context;

import com.stur.lib.os.PackageUtils;

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
    * if usbadb cannot used anymore after these steps, it will be resumed after restarting usb debug
    * Noticed: SystemPropertiesProxy.set is only granted for system/root uid, or you can write it persistently to system/build.prop
    * if write system/build.prop failed, dont forget "adb disable-verity" with the latest adb version
    */
    public static void enableWifiAdb(Context context, String port) throws IOException {
        port = port != null ? port : WIFI_ADB_DEFAULT_PORT;
        Log.d(getTag(), "enableWifiAdb at port: " + port);
        //SystemProperties.set(WIFI_ADB_PORT_PROP, port);
        SystemPropertiesProxy.set(context, WIFI_ADB_PORT_PROP, port);
        //Utils.execCommand("setprop service.adb.tcp.port 5555");
        //we can not stop adbd here for Operation not permitted
        //Utils.execCommand("stop adbd");
        //Utils.execCommand("start adbd");
        if (WIFI_ADB_DEFAULT_PORT.equals(SystemPropertiesProxy.get(context, AdbUtils.WIFI_ADB_PORT_PROP))) {
        	PackageUtils.startActivity(context, "com.android.settings", "com.android.settings.DevelopmentSettings", false);
        	UIHelper.toastMessageMiddle(context, "plz reenable usb debug");
		} else {
            UIHelper.toastMessageMiddle(context, AdbUtils.WIFI_ADB_PORT_PROP + ": " + SystemPropertiesProxy.get(context, AdbUtils.WIFI_ADB_PORT_PROP));
		}
    }
}
