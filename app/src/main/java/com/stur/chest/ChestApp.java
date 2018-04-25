package com.stur.chest;

import com.stur.lib.app.ContextBase;
import com.stur.lib.config.ConfigManager;

/**
 * Created by guanxuejin on 2018/2/28.
 */

public class ChestApp extends ContextBase {

    /**
     * 本应用相关的一些初始化在这里处理
     * 与context相关的且定义在mylibrary中的一些初始化在ContextBase.onCreate中处理
     */
    @Override
    public void onCreate() {
        super.onCreate();

        /*
         * 这里的初始化如果不传参数，则使用默认配置，建议新应用在这里把相关参数填完整，涉及到log_tag等功能
         * 如果app需要使用Bugly，则在初始化ConfigManager时需要传入自己的appId，否则默认使用Chest的AppId
         * 因为APP_NAME是在自己的应用里面配置，所以这个init不能放到mylibrary下的ContextBase中处理
         */
        //ConfigManager.getInstance().init(this);
        ConfigManager.getInstance().init(this, Constant.APP_NAME, Constant.LOG_TAG, Constant.FORCE_DEBUG,
                Constant.BUGLY_ENABLE, Constant.APP_ID_BUGLY);

        //ChestController.getInstance().init(this);
    }
}
