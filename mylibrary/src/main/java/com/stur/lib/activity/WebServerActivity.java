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
    private NanoHttpdServer httpServer;

    @Override
    protected void onDestroy() {
        httpServer.stop();
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
        sb.append("for examle: " + ip + ":" + StConstant.DEFAULT_WEB_SERVER_PORT + File.separator + "Google.html");
        //需要保证在/sdcard/stur/目录下有Google.html及其相关文件
        mTipsTextView.setText(sb.toString());
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        httpServer = new NanoHttpdServer(this, new File(FileUtils.getWorkPath(this, null)));
        try {
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
            mTipsTextView.setText(mTipsTextView.getText() + e.getMessage());
        }
    }

}

