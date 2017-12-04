package com.stur.lib.activity;

import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

import com.stur.chest.Constant;
import com.stur.chest.R;
import com.stur.lib.network.WifiUtils;
import com.stur.lib.web.NanoHttpdServer;

import java.io.File;
import java.io.IOException;

public class WebServerActivity extends ActivityBase {
    private TextView mTipsTextView;
    private NanoHttpdServer httpServer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nanohttpdserver);
        mTipsTextView = (TextView) findViewById(R.id.tv_content);
        String ip = WifiUtils.getIp(this);
        mTipsTextView.setText("please input in the browser:\n\n" + ip);
        httpServer = new NanoHttpdServer(this, new File(Environment.getExternalStorageDirectory() + Constant.IVVI_PATH));
        try {
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
            mTipsTextView.setText(mTipsTextView.getText() + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        httpServer.stop();
        super.onDestroy();
    }

}

