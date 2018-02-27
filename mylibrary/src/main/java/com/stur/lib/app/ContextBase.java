package com.stur.lib.app;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.stur.lib.Log;
import com.stur.lib.db.CacheManager;
import com.stur.lib.time.TimerUtils;
import com.stur.lib.web.GImageLoader;
import com.stur.lib.web.HttpFactory;

import java.util.Locale;

public class ContextBase extends Application {

    private Locale language = Locale.getDefault();
    protected static ContextBase instance = null;

    public ContextBase() {
        instance = this;
    }

    /**
     * 获取Application
     *
     * @return instance
     */
    public static <T extends ContextBase> T getInstance() {
        return (T) instance;
    }

    /**
     * 应用启动
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(this, "onCreate");
        instance = this;

        //ConfigBase.register(this);
        HttpFactory.register(this);
        CacheManager.register(this);
        GImageLoader.getInstance().init(this);

    }

    /**
     * 应用退出
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        onExit();
    }

    /**
     * 应用内存不足
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /**
     * 应用退出，由AppManager回调
     */
    public void onExit() {
        TimerUtils.killAll();
    }

    /**
     * 获取当前语言环境
     *
     * @return language
     */
    public Locale getLanguage() {
        return language;
    }

    /**
     * 设置语言环境
     *
     * @param locale
     */
    public void setLanguage(Locale locale) {
        this.language = locale;

        // 设置res的语言环境
        Resources res = getResources();
        Configuration config = res.getConfiguration();
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());

    }

}
