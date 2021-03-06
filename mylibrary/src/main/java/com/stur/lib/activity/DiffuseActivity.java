package com.stur.lib.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.stur.lib.R;
import com.stur.lib.view.DiffuseView;

public class DiffuseActivity extends Activity {
    private DiffuseView mDiffuseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diffuse);
        mDiffuseView = (DiffuseView) findViewById(R.id.diffuseView);
    }

    public void start(View v){
        mDiffuseView.start();
    }

    public void stop(View v){
        mDiffuseView.stop();
    }
}
