package com.stur.lib.config;

/*
 * Created by Sturmegezhutz on 2018/2/28.
 * 用于配置LOG_TAG, LOG_LEVEL，欢迎页面停留时间等
 * 考虑使用子类可以复写一些配置项，但是aar中引用的地方无法同步引用，所以没有成功
 * 本文件的配置为默认值，如需更改可通过ConfigManager操作
 */

public class ConfigBase {
    //android:sharedUserId="android.uid.system"   configed for app
    //android:process="com.android.phone"      configed for activity

    /************************************文件路径等常量值**********************************/
    public static final String SP_FILE_CONFIG_BASE = "config_base";
    public static final String SP_KEY_BUGLY_ENABLED = "bugly_enabled";
    public static final String SP_KEY_UMENG_ENABLED = "umeng_enabled";
    /**************************************************************************************/



    /***这些设置需要在第一个界面之前就配置完成，所以放到**App.java的onCreate中通过参数赋值***/
    public static String sAppName = "Chest";  //静态常量保存
    public static String sLogTag = "Chest";  //静态常量保存
    public static boolean sForceDebug = true;  //静态常量保存
    public static boolean sBuglyEnabled = false;  //Bugly enabled，SP保存
    public static boolean sUmengEnabled = false;    //友盟的移动统计功能，SP保存
    //腾讯bugly上为Chest工程注册的appid，用于异常统计，后续可能还会接入其它的PaaS
    public static String APP_ID_BUGLY = "a9e665bcce";
    //友盟上为Chest工程注册的appId
    public static String APP_ID_UMENG = "5afe88f4a40fa37693000092";
    /**************************************************************************************/



    /***************这些设置可以在ConfigManager初始化完成后通过接口调用赋值***************/
    public static boolean sSplashOnlyOnce = false;  //Splash View，SP保存
    public static final boolean sSplashEntranceShowDefault = true;  //SP保存
    /**************************************************************************************/



}
