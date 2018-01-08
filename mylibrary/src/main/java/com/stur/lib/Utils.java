package com.stur.lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.TimeZone;

import com.stur.lib.Constant;
import com.stur.lib.Log;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Binder;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

public class Utils {
    /* Broadcast Definition */
    public static final String INTENT_DISPLAY = "com.stur.chest.ui.display";
    public static final String INTENT_DISPLAY_EXTRA = "display";
    public static final String INTENT_TEST = "com.stur.chest.test";

    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.') + 1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    /*
    * @param context the caller context
    *
    * @param pkt the whole packet path name
    *
    * @param cls the whole class path name
    */
    public static void startActivity(Context context, String pkt, String cls, boolean isMainAcivity) {
        Intent intent;
        if (isMainAcivity) {
            intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
        } else {
            intent = new Intent();
        }
        ComponentName cn = new ComponentName(pkt, cls);
        intent.setComponent(cn);
        context.startActivity(intent);
    }

    /*
    * 执行shell命令
    */
    public static String execCommand(String command) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process proc = runtime.exec(command);
            if (proc.waitFor() != 0) { // block until subprocess exit
                /*
                * "OS error code   1:  Operation not permitted"
                * "OS error code   2:  No such file or directory"
                * "OS error code   3:  No such process"
                * "OS error code   4:  Interrupted system call"
                * "OS error code   5:  Input/output error" ......
                */
                Log.d(getTag(), "exit value = " + proc.exitValue());
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while ((line = in.readLine()) != null) {
                stringBuffer.append(line + "-");
            }

            Log.d(getTag(), stringBuffer.toString());
            return stringBuffer.toString();

        } catch (InterruptedException e) {
            Log.d(getTag(), e.toString());
            return null;
        } catch (Exception e) {
            Log.d(getTag(), e.toString());
            return e.toString();
        }
    }

    public static void display(Context context, String str) {
        Intent it = new Intent();
        it.setAction(INTENT_DISPLAY);
        it.putExtra(INTENT_DISPLAY_EXTRA, str);
        context.sendBroadcast(it);
    }

    public static void makeToast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static String getImeiInfo(Context context) {
        try {
            TelephonyManager mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return mTm.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public static String getImsiInfo(Context context) {
        try {
            String imsi = "";
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                imsi = telephonyManager.getSubscriberId();
            }
            if (TextUtils.isEmpty(imsi)) {
                imsi = "UNKNOWN";
            }
            return imsi;
        } catch (Exception e) {
            return "";
        }
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
     * 获取手机系统版本
     *
     * @return
     */
    public static String getOsVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static int getAppVersionCode(Context context) {
        if (context != null) {
            PackageManager pm = context.getPackageManager();
            if (pm != null) {
                PackageInfo pi;
                try {
                    pi = pm.getPackageInfo(context.getPackageName(), 0);
                    if (pi != null) {
                        return pi.versionCode;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }

    public static Point getScreenRealSize(Context ctx) {
        Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getRealSize(point);
        Log.d(getTag(), "[Display size]" + point.x + " * " + point.y);
        return point;
    }

    /*
    * read class name from a static method, just for reference and ony can be
    * called internal static class
    */
    public static String getStaticClassName() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(0, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    public static int getCallingPid() {
        return Binder.getCallingPid();
    }

    /**
     * java method
     * get date of system
     * @return
     */
    public static String getDate() {
        Calendar ca = Calendar.getInstance();
        ca.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        int year = ca.get(Calendar.YEAR); // 获取年份
        int month = ca.get(Calendar.MONTH) + 1; // 获取月份
        int day = ca.get(Calendar.DATE); // 获取日


        int dd = ca.get(Calendar.HOUR_OF_DAY);
        int hour = 0; // 小时
        if (ca.get(Calendar.AM_PM) == 0){
            hour = ca.get(Calendar.HOUR);
        } else {
            hour = ca.get(Calendar.HOUR)+12;
        }

        int minute = ca.get(Calendar.MINUTE); // 分
        int second = ca.get(Calendar.SECOND); // 秒


        String date = "" + year + month + day + hour + minute + second;
        Log.d(getTag(), "date:" + date);
        return date;
    }

    /*
     * android method
     * get date format 24 or 12
     */
    public static String getDateFormat(Context context) {
        ContentResolver cv = context.getContentResolver();
        String strTimeFormat = android.provider.Settings.System.getString(cv,android.provider.Settings.System.TIME_12_24);
        return strTimeFormat;
    }
}
