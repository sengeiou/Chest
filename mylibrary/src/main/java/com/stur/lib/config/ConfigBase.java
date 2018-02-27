package com.stur.lib.config;

/*
 * 用于配置LOG_TAG, LOG_LEVEL，欢迎页面停留时间等
 * 考虑使用子类可以复写一些配置项，但是aar中引用的地方无法同步引用，所以没有成功
 * 临时措施：需要更改配置的地方直接将整个文件复制到app模块中替换aar中的class,但有时也会替换失败
 */
public class ConfigBase {
    protected static ConfigBase sInstance = null;
    //android:sharedUserId="android.uid.system"   configed for app
    //android:process="com.android.phone"      configed for activity
    public static final String APP_NAME = "Chest";
    public static final String LOG_TAG = "Chest";
    public static final boolean FORCE_DEBUG = true;

    //Splash View
    public static final boolean SPLASH_ONLY_ONCE = false;
    public static final boolean SPLASH_ENTRANCE_SHOW_DEFAULT = true;

    //Bugly switch
    public static boolean BUGLY_ENABLED = false;
    public static boolean getBuglyEnabled() {
        return BUGLY_ENABLED;
    }
    public static void setBuglyEnabled(boolean enabled) {
        //TODO: this switch should be perserved with sp
    }
}
