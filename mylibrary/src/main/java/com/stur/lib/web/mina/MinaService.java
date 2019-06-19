package com.stur.lib.web.mina;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.stur.lib.Log;

public class MinaService extends Service{
    // 服务器配置相关参数
    public static final String SERVER_ADDRESS   = "192.168.1.100";//"47.107.175.95";
    public static final int SERVER_PORT         = 9234;
    public static final int READ_BUFFER_SIZE    = 10240;
    public static final int CONNECT_TIMEOUT     = 10000;

    private ConnectionThread thread;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(this, "onCreate E:");
        //全局context 避免内存泄漏
        //使用子线程开启连接
        thread = new ConnectionThread("mina",getApplicationContext());
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.disConnection();
        thread = null;
    }


    //负责调用ConnectionManager类来完成与服务器连接
    class ConnectionThread extends HandlerThread{
        private Context context;
        boolean isConnection;
        ConnectionManager mManager;

        public ConnectionThread(String name,Context context) {
            super(name);
            this.context = context;

            //创建配置文件类
            ConnectionConfig config = new ConnectionConfig.Builder(context)
                    .setIp(SERVER_ADDRESS)
                    .setPort(SERVER_PORT)
                    .setReadBuilder(READ_BUFFER_SIZE)
                    .setConnectionTimeout(CONNECT_TIMEOUT)
                    .builder();
            //创建连接的管理类
            mManager = new ConnectionManager(config);
        }

        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            //利用循环请求连接
            for (;;){
                isConnection = mManager.connect();
                if (isConnection){
                    //ChargerService.getInstance().notifySocketConnected();
                    //当请求成功的时候,跳出循环
                    break;
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //断开连接
        public void disConnection(){
            //ChargerService.getInstance().notifySocketDisconnected();
            mManager.disConnect();
        }
    }
}
