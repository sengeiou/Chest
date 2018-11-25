package com.stur.chest.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.stur.chest.service.ChestService.MyBinder;
import com.stur.chest.fragment.MineFragment;
import com.stur.chest.R;
import com.stur.chest.fragment.TestFragment;
import com.stur.chest.fragment.ToolsFragment;
import com.stur.lib.Log;
import com.stur.lib.Utils;
import com.stur.lib.activity.FragmentActivityBase;
import com.stur.lib.web.NanoHttpdServer;
import com.tab.view.demo3.FragmentAdapter;

import java.util.ArrayList;

public class ChestActivity extends FragmentActivityBase {
    private BroadcastReceiver mBroadcastReceiver;
    private Button mBtnDemo;
    private TextView mTvOutput;
    private ImageView mQRCodeImg;
    private Handler mHandler;
    private MyBinder myBinder;
    private NanoHttpdServer httpServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mBtnDemo = (Button)findViewById(R.id.Btn_chest_demo);
        //mTvOutput = (TextView)findViewById(R.id.tv_output);
        //mQRCodeImg = (ImageView)findViewById(R.id.iv_qrcode);
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

    protected void handleIntent(Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        Log.d(this, "onReceive: " + action);
        if (action.equals(Utils.INTENT_DISPLAY)) {
            String str = intent.getStringExtra(Utils.INTENT_DISPLAY_EXTRA);
            mTvOutput.setText(str);
        } else if (action.equals("un.intent.incallui.action.CALL_RECORDED")) {
            String callType = intent.getStringExtra("callType");
            String loalNumber = intent.getStringExtra("loalNumber");
            String remoteNumber = intent.getStringExtra("remoteNumber");
            String startTime = intent.getStringExtra("startTime");
            String endTime = intent.getStringExtra("endTime");
            String filePath = intent.getStringExtra("filePath");
            Log.d(this, "handleIntent: " + callType + loalNumber + remoteNumber + startTime + endTime + filePath);
        }
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
        unregisterReceiver(mBroadcastReceiver);
        Log.d(this, "onDestroy");
    }

    @Override
    protected void initListener() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleIntent(intent);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Utils.INTENT_DISPLAY);
        filter.addAction(Utils.INTENT_TEST);
        filter.addAction("un.intent.incallui.action.CALL_RECORDED");
        registerReceiver(mBroadcastReceiver, filter);// 注册Broadcast Receiver
    }

    @Override
    protected void initData() {

    }

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
}
