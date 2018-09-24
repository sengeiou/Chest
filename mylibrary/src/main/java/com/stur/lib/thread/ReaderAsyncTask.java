package com.stur.lib.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 使用AsyncTask后台获取图片并在前台展示，注意事项：
 * 必须在UI线程中创建AsyncTask的实例.
 * 只能在UI线程中调用AsyncTask的execute方法.
 * AsyncTask被重写的四个方法是系统自动调用的,不应手动调用.
 * 每个AsyncTask只能被执行(execute方法)一次,多次执行将会引发异常.
 * AsyncTask的四个方法,只有doInBackground方法是运行在其他线程中,其他三个方法都运行在UI线程中,也就说其他三个方法都可以进行UI的更新操作.
 * 使用方法：new ReaderAsyncTask().execute(URL);
 * AsyncTask的三个参数：Params , Progress, Result
 */
public class ReaderAsyncTask extends AsyncTask<String,Void,Bitmap> {

    //onPreExecute用于异步处理前的操作
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //此处将progressBar设置为可见.
        //progressBar.setVisibility(View.VISIBLE);
    }

    //在doInBackground方法中进行异步任务的处理.
    @Override
    protected Bitmap doInBackground(String... params) {
        //获取传进来的参数
        String url = params[0];
        Bitmap bitmap = null;
        URLConnection connection ;
        InputStream is ;
        try {
            connection = new URL(url).openConnection();
            is = connection.getInputStream();
            //为了更清楚的看到加载图片的等待操作,将线程休眠3秒钟.
            Thread.sleep(3000);
            BufferedInputStream bis = new BufferedInputStream(is);
            //通过decodeStream方法解析输入流
            bitmap = BitmapFactory.decodeStream(bis);
            is.close();
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    //onPostExecute用于UI的更新.此方法的参数为doInBackground方法返回的值.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        //隐藏progressBar
        //progressBar.setVisibility(View.GONE);
        //更新imageView
        //imageView.setImageBitmap(bitmap);
    }
}

