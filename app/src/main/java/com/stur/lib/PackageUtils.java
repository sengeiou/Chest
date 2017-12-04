package com.stur.lib;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class PackageUtils {
    public static final int AID_ROOT        = 0;  /* traditional unix root user */
    public static final int  AID_SYSTEM     = 1000;  /* system server */
    public static final int  AID_RADIO      = 1001;  /* telephony subsystem, RIL */
    public static final int AID_BLUETOOTH   = 1002;  /* bluetooth subsystem */
    public static final int AID_GRAPHICS    = 1003;  /* graphics devices */

    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.') + 1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    public static int getAppUid(Context context, String pkt) throws NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo ai = pm.getApplicationInfo(pkt, PackageManager.GET_ACTIVITIES);
        Log.d(getTag(), "getAppUid: " + ai.uid);
        return ai.uid;
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    public static String getAppInfo(Context context) {
        try {
            String pkName = context.getPackageName();
            String versionName = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            int versionCode = context.getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
            return pkName + "   " + versionName + "  " + versionCode;
        } catch (Exception e) {
        }
        return null;
    }

    public static int getMyPid() {
        return android.os.Process.myPid();
    }

    public static int getMyTid() {
        return android.os.Process.myTid();
    }

    public static int getMyUid() {
        return android.os.Process.myUid();
    }

    public static String getMyUA() {
        return System.getProperty("http.agent");
    }

    /**
     * 是否是系统软件或者是系统软件的更新软件
     * @return
     */
    public boolean isSystemApp(PackageInfo pInfo) {
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public boolean isSystemUpdateApp(PackageInfo pInfo) {
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
    }

    public boolean isUserApp(PackageInfo pInfo) {
        return (!isSystemApp(pInfo) && !isSystemUpdateApp(pInfo));
    }
}
