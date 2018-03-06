package com.stur.chest.activity;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.stur.chest.R;
import com.stur.lib.activity.ActivityBase;
import com.stur.lib.constant.StConstant;
import com.stur.lib.constant.StMessageID;
import com.stur.lib.network.OnTCPListener;
import com.stur.lib.network.TCPClient;

import java.io.IOException;

/**
 * Created by guanxuejin on 2018/3/5.
 */

public class ClientActivity extends ActivityBase {
    private TextView mMsgTv;
    private String mOutput = "";
    private Handler mHandler;

    @Override
    protected void beforeInitView() {
        setContentView(R.layout.activity_client);
    }

    @Override
    protected void initView() {
        mMsgTv = (TextView) findViewById(R.id.tv_clt_msg_output);
    }

    @Override
    protected void initListener() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case StMessageID.EVENT_TEXT_PRINT:
                        textViewPrint((String)msg.obj);
                        break;
                }
            }
        };
    }

    @Override
    protected void initData() {
        TCPClient.init(this, StConstant.DEFAULT_SERVER, StConstant.DEFAULT_PORT, new OnTCPListener() {
            @Override
            public void onConnectStatusChange(int status) {

            }

            @Override
            public void onMsgReceived(String str) {
                Message.obtain(mHandler, StMessageID.EVENT_TEXT_PRINT, "received: " + str).sendToTarget();
            }

            @Override
            public void onMsgSended(String str) {
                Message.obtain(mHandler, StMessageID.EVENT_TEXT_PRINT, "sent: " + str).sendToTarget();
            }

            @Override
            public void onTextViewPrint(String str) {
                Message.obtain(mHandler, StMessageID.EVENT_TEXT_PRINT, str).sendToTarget();
            }
        });
        TCPClient.getInstance().start();
    }

    private void textViewPrint(String str) {
        mOutput += str;
        mOutput += '\n';
        mMsgTv.setText(mOutput);
    }

    public void onTestClick(View view) {
        try {
            TCPClient.getInstance().send("I am client");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            TCPClient.getInstance().exit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
