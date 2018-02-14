package com.stur.lib.os;

/**
 * Created by guanxuejin on 2018/2/9.
 */

public class OsUtils {
    public static void stopProcess() {
        //2种可以用于完全关闭进程的方式: android.os.Process.killProcess 以及 System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());

        //System.exit(0);
    }
}
