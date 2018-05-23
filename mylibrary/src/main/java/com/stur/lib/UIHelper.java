package com.stur.lib;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.widget.Toast;

import com.stur.lib.network.NetworkUtils;

/**
 * Created by Administrator on 2016/3/4.
 */
public class UIHelper {
    private static Toast TOAST_MIDDLE;
    private static Toast TOAST_NORMAL;

    private static NotificationManager NOTIFICATION_MANAGER = null;

//    static MaterialDialog mMaterialDialog = null;

    /**
     * 弹出Toast消息
     *
     * @param charSequence
     */
    public static void toastMessage(Context context, CharSequence charSequence) {
        if (TOAST_NORMAL == null) {
            TOAST_NORMAL = Toast.makeText(context, charSequence, Toast.LENGTH_SHORT);
        } else {
            TOAST_NORMAL.setText(charSequence);
        }
        TOAST_NORMAL.show();
    }

    /**
     * Override for string extraction.
     * @param context
     * @param msgStrId
     */
    public static void toastMessage(Context context, @StringRes int msgStrId) {
        if (TOAST_NORMAL == null) {
            TOAST_NORMAL = Toast.makeText(context, msgStrId, Toast.LENGTH_SHORT);
        } else {
            TOAST_NORMAL.setText(msgStrId);
        }
        TOAST_NORMAL.show();
    }

    /**
     * 弹出Toast消息
     * @param charSequence
     */
    public static void toastMessageMiddle(Context context, CharSequence charSequence) {
        if (TOAST_MIDDLE == null) {
            TOAST_MIDDLE = Toast.makeText(context, charSequence, Toast.LENGTH_SHORT);
        } else {
            TOAST_MIDDLE.setText(charSequence);
        }
        TOAST_MIDDLE.setGravity(Gravity.CENTER, 0, 0);
        TOAST_MIDDLE.show();
    }

    /**
     * 弹出Toast消息
     */
    public static void toastMessageMiddle(Context context, @StringRes int msgStrId) {
        if (TOAST_MIDDLE == null) {
            TOAST_MIDDLE = Toast.makeText(context, msgStrId, Toast.LENGTH_SHORT);
        } else {
            TOAST_MIDDLE.setText(msgStrId);
        }
        TOAST_MIDDLE.setGravity(Gravity.CENTER, 0, 0);
        TOAST_MIDDLE.show();
    }

    /**
     * 显示通知
     *
     * @param context
     * @param id
     * @param notification
     */
    public static void showNotification(Context context, int id, Notification notification) {
        if (NOTIFICATION_MANAGER == null) {
            NOTIFICATION_MANAGER = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        NOTIFICATION_MANAGER.notify(id, notification); // 发动通知,id由自己指定，每一个Notification对应的唯一标志
    }

    /**
     * cancel
     *
     * @param id
     */
    public static void cancelNotification(int id) {
        if (NOTIFICATION_MANAGER != null) {
            NOTIFICATION_MANAGER.cancel(id);
        }
    }

    public static void showMaterLoading(Activity context, String msg, int bgColorAttr, int contentColorAttr) {
//        if (mMaterialDialog != null) {
//            hideMaterLoading();
//        }
//        mMaterialDialog = new MaterialDialog.Builder(context)
//                .backgroundColorAttr(bgColorAttr)
//                .contentColorAttr(contentColorAttr)
//                .progress(true, 0)
//                .content(msg).show();
//        mMaterialDialog.setCanceledOnTouchOutside(false);

    }

    /**
     * Override for string extraction.
     * @param context
     * @param msgStrId
     * @param bgColorAttr
     * @param contentColorAttr
     */
    public static void showMaterLoading(Activity context, @StringRes int msgStrId, int bgColorAttr, int contentColorAttr) {
//        if (mMaterialDialog != null) {
//            hideMaterLoading();
//        }
//        mMaterialDialog = new MaterialDialog.Builder(context)
//                .backgroundColorAttr(bgColorAttr)
//                .contentColorAttr(contentColorAttr)
//                .progress(true, 0)
//                .content(msgStrId).show();
//        mMaterialDialog.setCanceledOnTouchOutside(false);
    }

    public static void hideMaterLoading() {
//        if (mMaterialDialog != null && mMaterialDialog.isShowing()) {
//            mMaterialDialog.dismiss();
//            mMaterialDialog = null;
//        }

    }

    public static boolean notifyNetworkNotAvailable(Context context, String content) {
        if (!NetworkUtils.isInternetConnected(context)) {
            UIHelper.toastMessageMiddle(context, content);
            return true;
        }
        return false;
    }

    public static boolean notifyNetworkNotAvailable(Context context, int resId) {
        if (!NetworkUtils.isInternetConnected(context)) {
            UIHelper.toastMessageMiddle(context, resId);
            return true;
        }
        return false;
    }
}
