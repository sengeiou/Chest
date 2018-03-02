package com.stur.activity;

import android.content.Intent;
import android.os.Handler;

import com.stur.chest.activity.ChestActivity;
import com.stur.chest.R;
import com.stur.lib.activity.ActivityBase;

public class WelcomeActivity extends ActivityBase {
    protected static final int WELCOME_TIME = 2500;
    protected boolean mIsDestroyed = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroyed = true;
    }

    @Override
    protected void beforeInitView() {
        setContentView(R.layout.activity_welcome);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        finishAfterDelayed(WELCOME_TIME);
    }

    @Override
    protected void initData() {
    }

    private void finishAfterDelayed(int delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mIsDestroyed) {
                    return;
                }

                Intent i = new Intent(WelcomeActivity.this, ChestActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

        }, delay);
    }
}
