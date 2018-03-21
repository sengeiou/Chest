package com.stur.lib;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.TimeZone;

import static android.R.attr.process;
import static com.tencent.bugly.crashreport.crash.c.e;

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
    * 执行shell命令
    */
    public static String execCommand(String command) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process proc = runtime.exec(command);
            Log.i(getTag(), "execCommand: pid = " + proc);
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

    /**
     * 以root用户执行command指令
     * 在Android较高版本中已经失效
     * @param command
     * @return
     * @throws IOException
     */
    public static String execCommandAsSu(String command) throws IOException {
        DataOutputStream os = null;
        try {
            Process process = Runtime.getRuntime().exec("su");
            Log.i(getTag(), "execCommandAsSu: pid = " + process);
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            if (process.waitFor() != 0) { // block until subprocess exit
                /*
                * "OS error code   1:  Operation not permitted"
                * "OS error code   2:  No such file or directory"
                * "OS error code   3:  No such process"
                * "OS error code   4:  Interrupted system call"
                * "OS error code   5:  Input/output error" ......
                */
                Log.d(getTag(), "exit value = " + process.exitValue());
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
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
        } catch (IOException e) {
            return e.toString();
        }catch (Exception e) {
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
