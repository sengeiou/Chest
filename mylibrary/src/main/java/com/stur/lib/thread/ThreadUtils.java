package com.stur.lib.thread;

import android.os.Handler;

public class ThreadUtils {
    /**
     * 启用线程的方法3-1
     * 通过继承Thread类，并改写run方法来实现一个线程
     * 启动方式： new MyThread().start();
     */
    public class MyThread extends Thread {
        //继承Thread类，并改写其run方法
        @Override
        public void run(){
        }
    }

    /**
     * 启用线程的方法3-2
     * 启动方式：new Thread(new MyRunnable()).start();
     */
    public class MyRunnable implements Runnable{
        @Override
        public void run() {
        }
    }

    /**
     * 启用线程的方法3-3
     * 通过Handler启动线程
     * 通过Handler启动线程： mHandler.post(mRunnable);  //发送消息，启动线程运行
     * 将线程销毁掉： mHandler.removeCallbacks(mRunnable);
     */
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        public void run() {
            // 每3秒执行一次
            mHandler.postDelayed(mRunnable, 3000);  //给自己发送消息，自运行
        }
    };
}
