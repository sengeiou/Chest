package com.stur.lib.os;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.StrictMode;
import android.telephony.TelephonyManager;

import com.stur.lib.Log;
import com.stur.lib.Utils;
import com.stur.lib.constant.StSystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sturmegezhutz on 2018/2/9.
 */

public class OsUtils {
    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.') + 1, clazzName.lastIndexOf('$'));
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
     *
     * @param context
     * @param ServiceName
     * @param maxNum      查询的最大服务个数，需要设大一些，C5出厂开机143个服务，建议设200，极限可设Integer.MAX_VALUE
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
     * 一般情况下会在执行su时抛权限异常
     *
     * @return 应用程序是/否获取Root权限
     */
    public static boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "chmod 777 " + pkgCodePath;
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

    /**
     * 获取手机型号
     *
     * @return
     */
    public static String getTypeInfo() {
        return android.os.Build.MODEL; // 手机型号
    }

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return 语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 返回当前程序的版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        int versionCode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            versionCode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e(getTag(), "getAppVersionName Exception", e);
        }
        return versionName;
    }


    /**
     * 获取手机型号
     *
     * @return 手机型号,比如 i6310
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 从 ro.build.display.id 属性获取版本号
     * 比如：SQ52-userdebug 8.1.0 OPM1.171019.026 8.1.007.SQ52 test-keys
     * @return
     */
    public static String getProductVersion() {
        return android.os.Build.DISPLAY;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     *
     * @return 手机IMEI
     */
    public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            return tm.getDeviceId();
        }
        return null;
    }

    public static int getCallingPid() {
        return Binder.getCallingPid();
    }

    public static boolean writeCpuGovernor(String governor) {
        DataOutputStream os = null;
        String command = "echo " + governor + " > " + StSystem.PATH_CPU_FREQ + "/scaling_governor";
        Log.i(getTag(), "command: " + command);
        try {
            Utils.execCommandAsSu(command);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String readCurCpuGovernor() {
        String governor = null;
        DataInputStream is = null;
        try {
            Process process = Runtime.getRuntime().exec("cat " + StSystem.PATH_CPU_FREQ + "/scaling_governor");
            is = new DataInputStream(process.getInputStream());
            governor = is.readLine();
        } catch (IOException e) {
            Log.i(getTag(), "readCurCpuGovernor: read CPU Governor failed!");
            return null;
        }
        return governor;
    }

    public static List<String> readCpuGovernors() {
        List<String> governors = new ArrayList<String>();
        DataInputStream is = null;
        try {
            Process process = Runtime.getRuntime().exec("cat " + StSystem.PATH_CPU_FREQ + "/scaling_available_governors");
            is = new DataInputStream(process.getInputStream());
            String line = is.readLine();

            String[] strs = line.split(" ");
            for (int i = 0; i < strs.length; i++)
                governors.add(strs[i]);
        } catch (IOException e) {
            Log.i(getTag(), "readCpuGovernors: read CPU Governors failed!");
        }
        return governors;
    }

    /**
     * Android 7.0以后，Content Uri 替换了原本的File Uri，故在targetSdkVersion=24的时候，
     * 部分Uri.fromFile()方法就不适用了，使用后会导致 FileUriExposedException 的crash
     * 可以通过在app的onCreate中添加此函数规避crash问题
     */
    public static void addFileUriSupport() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }
}
