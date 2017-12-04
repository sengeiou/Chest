package com.stur.chest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.stur.lib.Log;

public class ChestService extends Service {
    private MyBinder mBinder = new MyBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(this, "onCreate!");
    }

    //前台service
    /*public void onCreate() {
        super.onCreate();
        Notification notification = new Notification(R.drawable.ic_launcher,
                "有通知到来", System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        notification.setLatestEventInfo(this, "这是通知的标题", "这是通知的内容",
                pendingIntent);
        startForeground(1, notification);
        log("onCreate for foreground service!");
    }*/

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(this, "onStartCommand!");
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 开始执行后台任务
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(this, "onDestroy!");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    class MyBinder extends Binder {

        public void startDownload() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 执行具体的下载任务
                }
            }).start();
        }
    }
}
