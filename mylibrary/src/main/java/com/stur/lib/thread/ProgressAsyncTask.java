package com.stur.lib.thread;

import android.os.AsyncTask;

/**
 * 添加进度条更新
 */
public class ProgressAsyncTask extends AsyncTask<Void,Integer,Void> {
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        //通过publishProgress方法传过来的值进行进度条的更新.
        //progressBar.setProgress(values[0]);
    }

    @Override
    protected Void doInBackground(Void... params) {
        //使用for循环来模拟进度条的进度.
        for (int i = 0;i < 100; i ++){
            //如果task是cancel状态,则终止for循环,以进行下个task的执行.
            if (isCancelled()){
                break;
            }
            //调用publishProgress方法将自动触发onProgressUpdate方法来进行进度条的更新.
            publishProgress(i);
            try {
                //通过线程休眠模拟耗时操作
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
