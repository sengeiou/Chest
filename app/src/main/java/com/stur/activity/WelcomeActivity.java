package com.stur.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.stur.chest.R;

public class WelcomeActivity extends Activity {
    protected static final int DELAY_LONG = 1000;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (com.stur.lib.constant.StConstant.EVENT_WELCOM_DELAY == msg.what) {
                    //startActivity(new Intent(WelcomeActivity.this, ChestActivity.class));
                    finish();
                }
            }
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mHandler.sendEmptyMessageDelayed(com.stur.lib.constant.StConstant.EVENT_WELCOM_DELAY, DELAY_LONG);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
