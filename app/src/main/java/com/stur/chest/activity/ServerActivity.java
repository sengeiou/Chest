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
import com.stur.lib.network.TCPServer;

import java.io.IOException;

/**
 * Created by Sturmegezhutz on 2018/3/5.
 */

public class ServerActivity extends ActivityBase {
    private TextView mMsgTv;
    private String mOutput = "";
    private Handler mHandler;

    @Override
    protected void beforeInitView() {
        setContentView(R.layout.activity_server);
    }

    @Override
    protected void initView() {
        mMsgTv = (TextView) findViewById(R.id.tv_svr_msg_output);
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
        TCPServer.init(this, StConstant.DEFAULT_PORT, new OnTCPListener() {
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
        TCPServer.getInstance().start();
    }

    private void textViewPrint(String str) {
        mOutput += str;
        mOutput += '\n';
        mMsgTv.setText(mOutput);
    }

    public void onServerClick(View view) {
        try {
            TCPServer.getInstance().send("I am server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            TCPServer.getInstance().exit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
