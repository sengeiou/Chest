package com.stur.lib;

import com.stur.lib.config.ConfigBase;

/**
 * Manages logging for the entire class.
 */
public class Log {
    /*
     * Define log level:
     * V: setprop log.tag.Chest V
     * D: setprop log.tag.Chest D
     * I: setprop log.tag.Chest I
     */
    public static final String[] LOG_LEVEL_ARR = {"V", "D", "I", "W", "E", "A"};

    // Generic tag for all In Call logging
    public static String TAG = ConfigBase.sLogTag;

    public static boolean FORCE_DEBUG = ConfigBase.sForceDebug;
    public static boolean DEBUG = FORCE_DEBUG || android.util.Log.isLoggable(TAG, android.util.Log.DEBUG);
    public static boolean VERBOSE = FORCE_DEBUG || android.util.Log.isLoggable(TAG, android.util.Log.VERBOSE);
    public static String TAG_DELIMETER = " : ";

    public static void update(String tag, boolean forceDebug) {
        TAG = tag;
        FORCE_DEBUG = forceDebug;
        DEBUG = FORCE_DEBUG || android.util.Log.isLoggable(TAG, android.util.Log.DEBUG);
        VERBOSE = FORCE_DEBUG || android.util.Log.isLoggable(TAG, android.util.Log.VERBOSE);
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            android.util.Log.d(TAG, delimit(tag) + msg);
        }
    }

    public static void d(Object obj, String msg) {
        if (DEBUG) {
            android.util.Log.d(TAG, getPrefix(obj) + msg);
        }
    }

    public static void d(Object obj, String str1, Object str2) {
        if (DEBUG) {
            android.util.Log.d(TAG, getPrefix(obj) + str1 + str2);
        }
    }

    public static void v(Object obj, String msg) {
        if (VERBOSE) {
            android.util.Log.v(TAG, getPrefix(obj) + msg);
        }
    }

    public static void v(Object obj, String str1, Object str2) {
        if (VERBOSE) {
            android.util.Log.d(TAG, getPrefix(obj) + str1 + str2);
        }
    }

    public static void e(String tag, String msg, Exception e) {
        android.util.Log.e(TAG, delimit(tag) + msg, e);
    }

    public static void e(String tag, String msg) {
        android.util.Log.e(TAG, delimit(tag) + msg);
    }

    public static void e(Object obj, String msg, Exception e) {
        android.util.Log.e(TAG, getPrefix(obj) + msg, e);
    }

    public static void e(Object obj, String msg) {
        android.util.Log.e(TAG, getPrefix(obj) + msg);
    }

    public static void i(String tag, String msg) {
        android.util.Log.i(TAG, delimit(tag) + msg);
    }

    public static void i(Object obj, String msg) {
        android.util.Log.i(TAG, getPrefix(obj) + msg);
    }

    public static void w(Object obj, String msg) {
        android.util.Log.w(TAG, getPrefix(obj) + msg);
    }

    public static void wtf(Object obj, String msg) {
        android.util.Log.wtf(TAG, getPrefix(obj) + msg);
    }

    private static String getPrefix(Object obj) {
        return (obj == null ? "" : (obj.getClass().getSimpleName() + TAG_DELIMETER));
    }

    private static String delimit(String tag) {
        return tag + TAG_DELIMETER;
    }
}
