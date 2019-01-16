package com.stur.lib;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.widget.Toast;

import com.stur.lib.network.NetworkUtils;

/**
 * Created by Administrator on 2016/3/4.
 */
public class UIHelper {
    private static Toast sToastMiddle;
    private static Toast sToastNormal;

    private static NotificationManager sNotificationManager = null;
    // 通知渠道的id，可以使用context.getPackageName()来代替
    public static final String CHANNEL_ID_STATUS_CHANGE = "status change";
    // 用户可以看到的通知渠道的名字，可以使用getTag()来代替
    public static final String CHANNEL_NAME_STATUS_CHANGE = "status change";

//    static MaterialDialog mMaterialDialog = null;

    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.')+1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    /**
     * 弹出Toast消息
     *
     * @param charSequence
     */
    public static void toastMessage(Context context, CharSequence charSequence) {
        if (sToastNormal == null) {
            sToastNormal = Toast.makeText(context, charSequence, Toast.LENGTH_SHORT);
        } else {
            sToastNormal.setText(charSequence);
        }
        sToastNormal.show();
    }

    /**
     * Override for string extraction.
     * @param context
     * @param msgStrId
     */
    public static void toastMessage(Context context, @StringRes int msgStrId) {
        if (sToastNormal == null) {
            sToastNormal = Toast.makeText(context, msgStrId, Toast.LENGTH_SHORT);
        } else {
            sToastNormal.setText(msgStrId);
        }
        sToastNormal.show();
    }

    /**
     * 弹出Toast消息
     * @param charSequence
     */
    public static void toastMessageMiddle(Context context, CharSequence charSequence) {
        if (sToastMiddle == null) {
            sToastMiddle = Toast.makeText(context, charSequence, Toast.LENGTH_SHORT);
        } else {
            sToastMiddle.setText(charSequence);
        }
        sToastMiddle.setGravity(Gravity.CENTER, 0, 0);
        sToastMiddle.show();
    }

    /**
     * 弹出Toast消息
     */
    public static void toastMessageMiddle(Context context, @StringRes int msgStrId) {
        if (sToastMiddle == null) {
            sToastMiddle = Toast.makeText(context, msgStrId, Toast.LENGTH_SHORT);
        } else {
            sToastMiddle.setText(msgStrId);
        }
        sToastMiddle.setGravity(Gravity.CENTER, 0, 0);
        sToastMiddle.show();
    }

    /**
     * 显示通知
     *
     * @param context
     * @param id
     * @param notification
     */
    public static void showNotification(Context context, int id, Notification notification) {
        if (sNotificationManager == null) {
            sNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        sNotificationManager.notify(id, notification); // 发动通知,id由自己指定，每一个Notification对应的唯一标志
    }

    /**
     * Android O 之后增加通知渠道
     * @param context
     * @param id
     * @param notification
     */
    @TargetApi(27)
    public static void showNotificationWithChannel(Context context, int id, Notification notification) {
        if (sNotificationManager == null) {
            sNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID_STATUS_CHANGE,
                CHANNEL_NAME_STATUS_CHANGE,
                NotificationManager.IMPORTANCE_DEFAULT);
        sNotificationManager.createNotificationChannel(channel);
        sNotificationManager.notify(id, notification); // 发动通知,id由自己指定，每一个Notification对应的唯一标志
    }

    /**
     * 测试：UIHelper.showNotification(context, 5555, UIHelper.buildNotificationBuilder(context, "test", "test").build());
     * @param context
     * @param title
     * @param text
     * @return
     */
    @TargetApi(27)
    public static Notification.Builder buildNotificationBuilder(Context context, CharSequence title,
                                                          CharSequence text) {
        if (sNotificationManager == null) {
            sNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_sd_card_48dp)
                .setColor(context.getColor(R.color.colorPrimary))
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new Notification.BigTextStyle().bigText(text))
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setLocalOnly(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    context.getPackageName(),
                    getTag(),
                    NotificationManager.IMPORTANCE_DEFAULT);
            sNotificationManager.createNotificationChannel(channel);
            builder.setChannelId(context.getPackageName());
        }
        return builder;
    }

    /**
     * cancel
     *
     * @param id
     */
    public static void cancelNotification(int id) {
        if (sNotificationManager != null) {
            sNotificationManager.cancel(id);
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
