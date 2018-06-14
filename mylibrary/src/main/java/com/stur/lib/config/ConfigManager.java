package com.stur.lib.config;

import android.content.Context;

import com.stur.lib.Log;
import com.stur.lib.R;
import com.stur.lib.SharedPreferenceUtils;
import com.stur.lib.UIHelper;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by guanxuejin on 2018/2/28.
 */

public class ConfigManager {
    private static ConfigManager sInstance;
    private Context mContext;

    public ConfigManager() {
        if (sInstance == null) {
            sInstance = this;
        } else {
            Log.e(this, "ConfigManager should not be init multiple times!  sInstance = " + sInstance);
        }
    }

    public static ConfigManager getInstance() {
        if (sInstance == null) {
            sInstance = new ConfigManager();
        }
        return sInstance;
    }

    //该初始化函数使用所有配置默认值进行初始化
    public void init(Context context) {
        mContext = context;
    }

    /**
     * 这里只配置appName、LogTag、forceDebug参数，其他的参数需要init之后单独配置
     * @param context
     * @param appName
     * @param logTag
     * @param forceDebug 启用后，打印log时无视log.tag.Chest等属性控制，一律打印
     */
    public void init(Context context, String appName, String logTag,
                     boolean forceDebug) {
        mContext = context;

        setAppName(appName);

        setLogTag(logTag);
        setForceDebug(forceDebug);
        Log.update(getLogTag(), getForceDebug());
    }

    //如果app需要使用Bugly，则在初始化ConfigManager时需要传入自己的appId，否则默认使用Chest的AppId
    public void init(Context context, String appName, String logTag,
                     boolean forceDebug, boolean buglyEnable, String appId) {
        mContext = context;
        setAppName(appName);

        setLogTag(logTag);
        setForceDebug(forceDebug);
        Log.update(getLogTag(), getForceDebug());

        if (buglyEnable) {
            initBugly(appId);
        }
    }

    private void setAppName(String name) {
        ConfigBase.sAppName = name;
    }

    public String getAppName() {
        return ConfigBase.sAppName;
    }

    private void setLogTag(String tag) {
        ConfigBase.sLogTag = tag;
    }

    public String getLogTag() {
        return ConfigBase.sLogTag;
    }

    private void setForceDebug(boolean enable) {
        ConfigBase.sForceDebug = enable;
    }

    public boolean getForceDebug() {
        return ConfigBase.sForceDebug;
    }

    private void initBugly(String appId) {
        if (getBuglyEnabled()) {
            String id = ConfigBase.APP_ID_BUGLY;
            if (appId != null && appId.length() > 0) {
                id = appId;
            }
            //集成bugly的异常统计功能4-4
            //第三个参数为SDK调试模式开关，打开后输出详细Bugly SDK的Log，每一条Crash都会被立即上报，自定义日志将会在Logcat中输出
            //建议在测试阶段设置为true，发布时设置为false
            CrashReport.initCrashReport(mContext, id, true);
        }
    }

    //用户设置的开关值会保存在sp中，如果没有，则读取ConfigBase中的默认值
    public boolean getBuglyEnabled() {
        return SharedPreferenceUtils.getBoolean(mContext, ConfigBase.SP_FILE_CONFIG_BASE, ConfigBase.SP_KEY_BUGLY_ENABLED, ConfigBase.sBuglyEnabled);
    }

    public void setBuglyEnabled(boolean enabled, String appId) {
        if (enabled != getBuglyEnabled()) {
            SharedPreferenceUtils.putBoolean(mContext, ConfigBase.SP_FILE_CONFIG_BASE, ConfigBase.SP_KEY_BUGLY_ENABLED, enabled);
            Log.d(this, "setBuglyEnabled " + enabled + ", appId = " + appId);
            if (enabled) {
                initBugly(appId);
            }
        }
    }

    public boolean getUmengEnabled() {
        return SharedPreferenceUtils.getBoolean(mContext, ConfigBase.SP_FILE_CONFIG_BASE, ConfigBase.SP_KEY_UMENG_ENABLED, ConfigBase.sUmengEnabled);
    }

    public void setUmengEnabled(boolean enabled) {
        if (enabled != getUmengEnabled()) {
            SharedPreferenceUtils.putBoolean(mContext, ConfigBase.SP_FILE_CONFIG_BASE, ConfigBase.SP_KEY_UMENG_ENABLED, enabled);
            UIHelper.toastMessage(mContext, mContext.getString(R.string.restart_for_take_effect));
        }
    }
}
