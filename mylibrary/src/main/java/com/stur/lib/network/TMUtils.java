package com.stur.lib.network;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.stur.lib.Log;

import java.lang.reflect.Method;

import static com.stur.lib.constant.StConstant.SIM_ID_1;
import static com.stur.lib.constant.StConstant.SIM_ID_2;

public class TMUtils {
    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.') + 1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    public static boolean isVtSwitchEnabled(Context context) {
        boolean card1ImsSupport = false;
        boolean card2ImsSupport = false;
        boolean ret = false;
        try {
            Class<?> classTelephoneManager = Class.forName("android.telephony.TelephonyManager");
            Method getDefaultMethod = classTelephoneManager.getMethod("getDefault");
            Method isCommFeatureEnabledMethod = classTelephoneManager.getMethod("isCommFeatureEnabled", int.class);
            Object instanceTelephoneManager = getDefaultMethod.invoke(classTelephoneManager);
            card1ImsSupport = (Boolean) isCommFeatureEnabledMethod.invoke(instanceTelephoneManager, 0x20);
            card1ImsSupport = card1ImsSupport && isActiveSlot(context, SIM_ID_1);
        } catch (Exception e) {
            Log.e(getTag(), "ClassNotFoundException: " + e.toString());
        }
        try {
            Class<?> classTelephoneManager = Class.forName("android.telephony.TelephonyManager");
            Method getDefaultMethod = classTelephoneManager.getMethod("getDefault");
            Method isCommFeatureEnabledMethod = classTelephoneManager.getMethod("isCommFeatureEnabled", int.class);
            Object instanceTelephoneManager = getDefaultMethod.invoke(classTelephoneManager);
            card2ImsSupport = (Boolean) isCommFeatureEnabledMethod.invoke(instanceTelephoneManager, 0x400);
            card2ImsSupport = card2ImsSupport && isActiveSlot(context, SIM_ID_2);
        } catch (Exception e) {
            Log.e(getTag(), "ClassNotFoundException: " + e.toString());
        }
        Log.d(getTag(), "card1ImsSupport =" + card1ImsSupport);
        Log.d(getTag(), "card2ImsSupport =" + card2ImsSupport);
        if (card1ImsSupport || card2ImsSupport) {
            ret = true;
        } else {
            ret = false;
        }
        Log.d(getTag(), "isVtSwitchEnabled = " + ret);
        return ret;
    }

    public static boolean isActiveSlot(Context context, int slotId) {
        /*int[] subIds = SubscriptionManager.getSubId(slotId);
        SubscriptionManager sm = SubscriptionManager.from(context);
        if (subIds != null && sm != null && sm.isActiveSubId(subIds[0])) {
            return true;
        } else {
            return false;
        }*/
        return true;
    }

    public static String getImeiInfo(Context context) {
        try {
            TelephonyManager mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return mTm.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getImsiInfo(Context context) {
        try {
            String imsi = "";
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                imsi = telephonyManager.getSubscriberId();
            }
            if (TextUtils.isEmpty(imsi)) {
                imsi = "UNKNOWN";
            }
            return imsi;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 试图获取MEID
     * 原生是没有提供单独获取MEID的方法的，getDeviceId是根据当前插卡情况来动态返回IMEI还是MEID的
     * 所以这里分别按照原生的方法获取两张卡的deviceId，然后根据长度判断是否MEID
     * 这样也不一定能获取到，比如插两张G网卡的情况下就获取不到了
     * @param context
     * @return
     */
    public static String getMeidInfo(Context context) {
        String deviceId = "";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = telephonyManager.getDeviceId(0);
        Log.d(getTag(), "getMeidInfo for slot0 " +  deviceId);
        if (deviceId != null && !TextUtils.isEmpty(deviceId) && deviceId.length() == 14) {
            return deviceId;
        }
        deviceId = telephonyManager.getDeviceId(1);
        Log.d(getTag(), "getMeidInfo for slot1 " +  deviceId);
        if (deviceId != null && !TextUtils.isEmpty(deviceId) && deviceId.length() == 14) {
            return deviceId;
        }
        return "";
    }
}
