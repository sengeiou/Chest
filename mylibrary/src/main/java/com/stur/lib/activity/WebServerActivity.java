package com.stur.lib.activity;

import android.widget.TextView;

import com.stur.lib.R;
import com.stur.lib.constant.StConstant;
import com.stur.lib.file.FileUtils;
import com.stur.lib.network.WifiUtils;
import com.stur.lib.web.NanoHttpdServer;

import java.io.File;
import java.io.IOException;

public class WebServerActivity extends ActivityBase {
    private TextView mTipsTextView;
    private NanoHttpdServer mHttpServer;

    @Override
    protected void onDestroy() {
        mHttpServer.stop();
        super.onDestroy();
    }

    @Override
    protected void beforeInitView() {
        setContentView(R.layout.activity_nanohttpdserver);
    }

    @Override
    protected void initView() {
        mTipsTextView = (TextView) findViewById(R.id.tv_content);
        String ip = WifiUtils.getIp(this);
        StringBuilder sb = new StringBuilder();
        sb.append("please input in the browser:\n" + ip + ":" + StConstant.DEFAULT_WEB_SERVER_PORT + "\n");
        sb.append("or : " + ip + ":" + StConstant.DEFAULT_WEB_SERVER_PORT + File.separator + "baidu.html" +
                ", and ensure it has been put in /sdcard/stur/");
        //需要保证在/sdcard/stur/目录下有baidu.html及其相关文件
        //请求html文件在IE浏览器中会直接显示，在百度浏览器中会被当成一个文件下载
        mTipsTextView.setText(sb.toString());
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        mHttpServer = new NanoHttpdServer(this, new File(FileUtils.getWorkPath(this, null)));
        try {
            mHttpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
            mTipsTextView.setText(mTipsTextView.getText() + e.getMessage());
        }
    }

}

