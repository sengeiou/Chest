package com.stur.lib;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

//import com.android.internal.widget.LockPatternUtils;

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

    /**
     * 开启闪光灯手电筒
     * @param context
     */
    public static void sendBroadcastForFlashLight(Context context){
        /*String isOpen = SystemProperties.get("sys.yulong.flashlight", "0");
        Log.d(getTag(), "sendBroadcastForFlashLight  isOpen = " +isOpen);
        String flashLight = "com.android.intent.action.Close_FlashLight";
        if(isOpen.equals("0")){
            flashLight = "com.android.intent.action.Open_FlashLight";
        }else if(isOpen.equals("1")){
            flashLight = "com.android.intent.action.Close_FlashLight";
        }
        Intent intent = new Intent(flashLight);
        context.sendBroadcast(intent);*/
    }

    /**
     * 清除Keyguard的锁屏记录
     * 测试发现，只能清除灭屏下的锁屏，无法清除开机锁屏
     * @param context
     */
    public static void setLockNone(Context context) {
        /*LockPatternUtils lpu =new LockPatternUtils(context);
        lpu.clearEncryptionPassword();
        lpu.clearLock(0);
        lpu.setLockScreenDisabled(true,0);*/
    }

    /**
     * 在Keyguard上显示Toast
     * @param context
     * @param text
     * @param duration
     * @return
     */
    public static Toast makeKeyguardToast(Context context, CharSequence text, int duration) {
        Toast toast = Toast.makeText(context, text, duration);
        /*toast.getWindowParams().type = WindowManager.LayoutParams.TYPE_STATUS_BAR_PANEL;
        toast.getWindowParams().privateFlags |= WindowManager.LayoutParams.PRIVATE_FLAG_SHOW_FOR_ALL_USERS;
        toast.getWindowParams().flags |= WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;*/

        // set offset position
        toast.setGravity(Gravity.CENTER, 0, 400);
        return toast;
    }

    /**
     * 锁屏之上显示Dialog
     */
    public class SystemUIDialog extends AlertDialog {

        private final Context mContext;

        public SystemUIDialog(Context context) {
            //super(context, R.style.Theme_SystemUI_Dialog);
            super(context, R.style.AppTheme);
            mContext = context;

            getWindow().setType(WindowManager.LayoutParams.TYPE_STATUS_BAR_PANEL);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.setTitle(getClass().getSimpleName());
            getWindow().setAttributes(attrs);
        }

        public void setShowForAllUsers(boolean show) {
            /*if (show) {
                getWindow().getAttributes().privateFlags |=
                        WindowManager.LayoutParams.PRIVATE_FLAG_SHOW_FOR_ALL_USERS;
            } else {
                getWindow().getAttributes().privateFlags &=
                        ~WindowManager.LayoutParams.PRIVATE_FLAG_SHOW_FOR_ALL_USERS;
            }*/
        }

        public void setMessage(int resId) {
            setMessage(mContext.getString(resId));
        }

        public void setPositiveButton(int resId, OnClickListener onClick) {
            setButton(BUTTON_POSITIVE, mContext.getString(resId), onClick);
        }

        public void setNegativeButton(int resId, OnClickListener onClick) {
            setButton(BUTTON_NEGATIVE, mContext.getString(resId), onClick);
        }
    }

    /**
     * 通知Launcher角标个数，触发Launcher的角标显示
     * 私有接口，非原生接口
     * 未读短信："com.un.coolmessage"  "com.yulong.android.mms.ui.MmsConversationListActivity"
     * 未接来电："com.un.coolmessage"  "com.yulong.android.contacts.dial.DialActivity"
     * @param context
     * @param pkg 应用的包名
     * @param act 触发拉起应用的activity名
     * @param iTotalNum 显示的角标个数
     */
    public static void notifyLauncherUI(Context context, String pkg, String act, int iTotalNum) {
        final String MMS_COUNT_CHANGE_ACTION = "yulong.intent.action.SHOW_NUM_CHANGED";
        final String PACKAGE_NAME = "packageName";
        final String CLASS_NAME = "className";
        final String SHOW_NUM = "showNum";

        // 发送广播通知shell
        Intent myIntent = new Intent(MMS_COUNT_CHANGE_ACTION);
        myIntent.putExtra(PACKAGE_NAME, pkg);
        myIntent.putExtra(CLASS_NAME, act);
        myIntent.putExtra(SHOW_NUM, iTotalNum);
        context.sendBroadcast(myIntent);
    }
}
