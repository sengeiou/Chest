package com.stur.lib.network;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
     * 试图获取MEID
     * 原生是没有提供单独获取MEID的方法的，getDeviceId是根据当前插卡情况来动态返回IMEI还是MEID的
     * 所以这里分别按照原生的方法获取两张卡的deviceId，然后根据长度判断是否MEID
     * 这样也不一定能获取到，比如插两张G网卡的情况下就获取不到了
     *
     * @param context
     * @return
     */
    public static String getMeidInfo(Context context) {
        String deviceId = "";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = telephonyManager.getDeviceId(0);
        Log.d(getTag(), "getMeidInfo for slot0 " + deviceId);
        if (deviceId != null && !TextUtils.isEmpty(deviceId) && deviceId.length() == 14) {
            return deviceId;
        }
        deviceId = telephonyManager.getDeviceId(1);
        Log.d(getTag(), "getMeidInfo for slot1 " + deviceId);
        if (deviceId != null && !TextUtils.isEmpty(deviceId) && deviceId.length() == 14) {
            return deviceId;
        }
        return "";
    }

    /**
     * 指定sim卡拨打电话
     *
     * @param phoneNumber
     * @param slotId      0:卡1  1:卡2
     */
    public static void callPhone(Context context, String phoneNumber, int slotId) {
        PhoneAccountHandle phoneAccountHandle = getPhoneAccountHandle(context, slotId);
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        intent.putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 这一块首先获取手机中所有sim卡 PhoneAccountHandle
     * 每一个 PhoneAccountHandle 表示一个sim卡,
     * 然后根据 slotId 判断所指定的sim卡并返回此 PhoneAccountHandle (这里5.1 和 6.0需要区分对待)
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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {    //6.0使用iccid作为PhoneAccountHandle的ID
                            if (TextUtils.equals(info.getIccId(), handle.getId())) {
                                Log.d(getTag(), "getPhoneAccountHandle for slot" + slotId + " " + handle);
                                return handle;
                            }
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {  //5.1使用subId作为PhoneAccountHandle的ID
                            if (TextUtils.equals(info.getSubscriptionId() + "", handle.getId())) {
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

    /**
     *  未接来电观察器，独立的广播接收器可以采用这种封装模式，降低耦合，功能单一
     */
    public static class MissedCallObserver extends BroadcastReceiver {
        private int mLastCallState = TelephonyManager.CALL_STATE_IDLE;

        public MissedCallObserver(Context context) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
            context.registerReceiver(this, filter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(intent.getAction())) {  //added by stur 20180910 for missing call not showd on launcher
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                int currentCallState = telephonyManager.getCallState();
                Log.d(this, "currentCallState=" + currentCallState);
                if (currentCallState == TelephonyManager.CALL_STATE_IDLE) {// 空闲
                    //TODO
                } else if (currentCallState == TelephonyManager.CALL_STATE_RINGING) {// 响铃
                    //TODO
                } else if (currentCallState == TelephonyManager.CALL_STATE_OFFHOOK) {// 接听
                    //TODO
                }
                if (mLastCallState == TelephonyManager.CALL_STATE_RINGING &&
                        currentCallState == TelephonyManager.CALL_STATE_IDLE) {
                    Log.d(this, "MissedCallObserver onReceive missed call catched");
                }
                mLastCallState = currentCallState;
            }
        }
    }
}
