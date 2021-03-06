package com.stur.chest;

import android.content.SharedPreferences;

import com.stur.lib.Log;
import com.stur.lib.app.ContextBase;
import com.stur.lib.config.ConfigManager;
import com.stur.lib.os.OsUtils;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;

/**
 * Created by Sturmegezhutz on 2018/2/28.
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
        ConfigManager.getInstance().init(this, Constant.APP_NAME, Constant.LOG_TAG, Constant.FORCE_DEBUG);
        ConfigManager.getInstance().setBuglyEnabled(Constant.BUGLY_ENABLE, Constant.APP_ID_BUGLY);
        ConfigManager.getInstance().setUmengEnabled(true);

        //ChestController.getInstance().init(this);

        OsUtils.addFileUriSupport();

        Log.i(this, "onCreate E: UE version = " + OsUtils.getProductVersion() +
                ", App version = " + OsUtils.getAppVersionName(getApplicationContext()));
    }

    //如果添加方法数超标后需要做dex分包处理3-3
    /*@Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }*/

    /* added by stur 20181015 for serialport begin */
    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
    private SerialPort mSerialPort = null;

    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
			/* Read serial port parameters */
            SharedPreferences sp = getSharedPreferences("com.stur.chest_preferences", MODE_PRIVATE);
            String path = sp.getString("DEVICE", "");
            int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));

			/* Check parameters */
            if ( (path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }

			/* Open the serial port */
            mSerialPort = new SerialPort(new File(path), baudrate, 0);
        }
        return mSerialPort;
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }
    /* added by stur 20181015 for serialport end */
}
