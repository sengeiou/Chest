package com.stur.chest.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.stur.chest.R;
import com.stur.chest.fragment.MineFragment;
import com.stur.chest.fragment.TestFragment;
import com.stur.chest.fragment.ToolsFragment;
import com.stur.chest.service.ChestService.MyBinder;
import com.stur.lib.Log;
import com.stur.lib.activity.FragmentActivityBase;
import com.stur.lib.web.NanoHttpdServer;
import com.tab.view.demo3.FragmentAdapter;

import java.util.ArrayList;

public class ChestActivity extends FragmentActivityBase {
    private Handler mHandler;
    private MyBinder myBinder;
    private NanoHttpdServer httpServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //myBinder = (ChestService.MyBinder) service;
            //myBinder.startDownload();
        }
    };

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        checkInitkMultiPermissions();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.d(this, "onDestroy");
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.vp_content);
        fragmentsList = new ArrayList<>();
        Fragment fragment = new ToolsFragment();
        fragmentsList.add(fragment);
        fragment = new TestFragment();
        fragmentsList.add(fragment);
        fragment = new MineFragment();
        fragmentsList.add(fragment);

        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), fragmentsList));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(this);
    }

    /**
     * 为了得到传回的数据，必须在前面的Activity中重写onActivityResult方法
     * requestCode 请求码，即调用startActivityForResult()传递过去的值
     * resultCode 结果码，结果码用于标识返回数据来自哪个新Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(this, "onActivityResult E: requestCode = " + requestCode + ", resultCode = " + resultCode);
        String result = data.getExtras().getString("result");//得到新Activity 关闭后返回的数据
    }
}
