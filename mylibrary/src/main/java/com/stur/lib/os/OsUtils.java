package com.stur.lib.os;

import android.app.ActivityManager;
import android.content.Context;

import com.stur.lib.Log;

import java.io.DataOutputStream;
import java.util.ArrayList;

/**
 * Created by guanxuejin on 2018/2/9.
 */

public class OsUtils {
    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.')+1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    public static void stopProcess() {
        //2种可以用于完全关闭进程的方式: android.os.Process.killProcess 以及 System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());

        //System.exit(0);
    }

    /**
     * ActivityManager还有一方法getRecentTasks(maxNum, flags)可判断最近的任务
     * ActivityManager不能获取运行中的BroadcastReceiver和ContentProvider
     * @param context
     * @param ServiceName
     * @param maxNum 查询的最大服务个数，需要设大一些，C5出厂开机143个服务，建议设200，极限可设Integer.MAX_VALUE
     * @return
     */
    public static boolean isServiceRunning(Context context, String ServiceName, int maxNum) {
        boolean ret = false;
        if (("").equals(ServiceName) || ServiceName == null) {
            ret = false;
        } else {
            ActivityManager am = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) am
                    .getRunningServices(maxNum);
            for (int i = 0; i < runningService.size(); i++) {
                if (runningService.get(i).service.getClassName().toString()
                        .equals(ServiceName)) {
                    ret = true;
                    break;
                }
            }
        }
        Log.v(getTag(), "isServiceRunning: " + ret);
        return ret;
    }

    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     * @return 应用程序是/否获取Root权限
     */
    public static boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd="chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    public static boolean isHardWareVendorQualcomm() {
        String hardware = android.os.Build.HARDWARE;
        if (hardware.matches("qcom")) {
            Log.d(getTag(), "Qualcomm platform");
            return true;
        }
        return false;
    }

    public static boolean isHardWareVendorMediaTek() {
        String hardware = android.os.Build.HARDWARE;
        if (hardware.matches("mt[0-9]*")) {
            Log.d(getTag(), "MediaTek platform");
            return true;
        }
        return false;
    }
}
