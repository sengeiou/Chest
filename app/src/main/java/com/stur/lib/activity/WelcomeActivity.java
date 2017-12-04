package com.stur.lib.activity;

import com.stur.chest.ChestActivity;
import com.stur.chest.Constant;
import com.stur.chest.Constant.ViewState;
import com.stur.chest.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class WelcomeActivity extends Activity {
    protected static final int DELAY_LONG = 1000;
    protected static ViewState mViewState = ViewState.UNKNOWN;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mViewState == ViewState.CREATED) {
                if (Constant.EVENT_WELCOM_DELAY == msg.what) {
                    startActivity(new Intent(WelcomeActivity.this, ChestActivity.class));
                    finish();
                }
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mHandler.sendEmptyMessageDelayed(Constant.EVENT_WELCOM_DELAY, DELAY_LONG);
        mViewState = ViewState.CREATED;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewState = ViewState.CREATED;
    }

    @Override
    protected void onPause() {
        mViewState = ViewState.DESTROED;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewState = ViewState.DESTROED;
    }
}
