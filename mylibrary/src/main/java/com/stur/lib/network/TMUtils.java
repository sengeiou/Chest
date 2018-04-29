package com.stur.lib.network;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.stur.lib.Log;
import com.stur.lib.ReflectUtil;

import java.lang.reflect.Method;
import java.util.List;

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
     * ��ͼ��ȡMEID
     * ԭ����û���ṩ������ȡMEID�ķ����ģ�getDeviceId�Ǹ��ݵ�ǰ�忨�������̬����IMEI����MEID��
     * ��������ֱ���ԭ���ķ�����ȡ���ſ���deviceId��Ȼ����ݳ����ж��Ƿ�MEID
     * ����Ҳ��һ���ܻ�ȡ�������������G����������¾ͻ�ȡ������
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

    /**
     * ָ��sim������绰
     *
     * @param phoneNumber
     * @param slotId      0:��1  1:��2
     */
    public static void callPhone(Context context, String phoneNumber, int slotId) {
        PhoneAccountHandle phoneAccountHandle = getPhoneAccountHandle(context, slotId);
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        intent.putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * ��һ�����Ȼ�ȡ�ֻ�������sim�� PhoneAccountHandle
     * ÿһ�� PhoneAccountHandle ��ʾһ��sim��,
     * Ȼ����� slotId �ж���ָ����sim�������ش� PhoneAccountHandle (����5.1 �� 6.0��Ҫ���ֶԴ�)
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static PhoneAccountHandle getPhoneAccountHandle(Context context, int slotId) {
        TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        //PhoneAccountHandle api>5.1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            List<PhoneAccountHandle> handles = (List<PhoneAccountHandle>) ReflectUtil.invoke(tm, "getCallCapablePhoneAccounts");
            SubscriptionManager sm = SubscriptionManager.from(context);
            if (handles != null) {
                for (PhoneAccountHandle handle : handles) {
                    SubscriptionInfo info = sm.getActiveSubscriptionInfoForSimSlotIndex(slotId);
                    if (info != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                            if (TextUtils.equals(info.getSubscriptionId() + "", handle.getId())) {
                                Log.d(getTag(), "getPhoneAccountHandle for slot" + slotId + " " + handle);
                                return handle;
                            }
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (TextUtils.equals(info.getIccId(), handle.getId())) {
                                Log.d(getTag(), "getPhoneAccountHandle for slot" + slotId + " " + handle);
                                return handle;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
