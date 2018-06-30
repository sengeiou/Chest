package com.stur.lib;

import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;

import com.android.internal.widget.LockPatternUtils;

/**
 * Created by guanxuejin on 2018/6/26.
 */

public class SystemUIUtils {
   // public static final String Settings.Global.REQUIRE_PASSWORD_TO_DECRYPT = "require_password_to_decrypt";

    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.')+1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    public static void sendBroadcastForFlashLight(Context context){
        String isOpen = SystemProperties.get("sys.yulong.flashlight", "0");
        Log.d(getTag(), "sendBroadcastForFlashLight  isOpen = " +isOpen);
        String flashLight = "com.android.intent.action.Close_FlashLight";
        if(isOpen.equals("0")){
            flashLight = "com.android.intent.action.Open_FlashLight";
        }else if(isOpen.equals("1")){
            flashLight = "com.android.intent.action.Close_FlashLight";
        }
        Intent intent = new Intent(flashLight);
        context.sendBroadcast(intent);
    }

    /**
     * 清除Keyguard的锁屏记录
     * 测试发现，只能清除灭屏下的锁屏，无法清除开机锁屏
     * @param context
     */
    public static void setLockNone(Context context) {
        LockPatternUtils lpu =new LockPatternUtils(context);
        lpu.clearEncryptionPassword();
        lpu.clearLock(0);
        lpu.setLockScreenDisabled(true,0);
    }
}
