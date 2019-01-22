package com.stur.lib;

import android.content.Context;


public class Features {
    public static final int PLATFORM_MTK = 1;
	public static final int PLATFORM_QC  = 2;

    // 客户定制相关配置，决定录音存储的目录，客户定制选项间互斥
    public static boolean YDSD = false;
    public static boolean JD = false;
    public static boolean UROVO = true;

    public static int PLATFORM = PLATFORM_QC;
    public static boolean FEATURE_REMOVE_LESS_SECURE_LOCK_TYPE = false;

    public static void initFeature(Context context) {
        /**
         * TODO: init product config here
         */

        if (JD) {
            PLATFORM = PLATFORM_QC;
            FEATURE_REMOVE_LESS_SECURE_LOCK_TYPE       = true;
        } else if (YDSD) {
            PLATFORM = PLATFORM_QC;
            FEATURE_REMOVE_LESS_SECURE_LOCK_TYPE       = true;
        } else if (UROVO) {
            PLATFORM = PLATFORM_QC;
            FEATURE_REMOVE_LESS_SECURE_LOCK_TYPE       = true;
        }
    }
}
