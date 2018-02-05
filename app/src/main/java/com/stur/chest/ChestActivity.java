package com.stur.chest;

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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.stur.chest.ChestService.MyBinder;
import com.stur.lib.Log;
import com.stur.lib.Utils;
import com.stur.lib.activity.ActivityBase;
import com.stur.lib.bt.StBluetoothActivity;
import com.stur.lib.web.NanoHttpdServer;
import com.tab.view.demo3.FragmentAdapter;

import java.io.IOException;
import java.util.ArrayList;

public class ChestActivity extends ActivityBase {
    Button mBtnDemo;
    TextView mTvOutput;
    ImageView mQRCodeImg;
    Handler mHandler;
    MyBinder myBinder;
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

    @Override
    protected void handleIntent(Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        Log.d(this, "onReceive: " + action);
        if (action.equals(Utils.INTENT_DISPLAY)) {
            String str = intent.getStringExtra(Utils.INTENT_DISPLAY_EXTRA);
            mTvOutput.setText(str);
        }
    }

    public void onDemoClick(View view) throws IOException
    {
        Log.d(this, "onCommTestButtonClick!");
        Toast.makeText(this, "Button clicked!", Toast.LENGTH_SHORT).show();

        //mTv_output.setText(getProperity(Constant.PROPERTY_OPERATORS_MODE));

        //拉起某个界面
        /*Intent intent = new Intent(this,
                WiFiActivity.class);
        startActivity(intent);*/

        //拉起某个 服务
        /*Intent startIntent = new Intent(this, ChestService.class);
        startService(startIntent);*/

        //开启ping
        //WifiUtils.startPing();

        //从arp缓存中获取某个mac地址对应的IP地址
        /*ArrayList<ClientScanResult> csrList = WifiUtils.getClientList(false, 0);
        for (ClientScanResult csr : csrList) {
            if(csr.getMac().equals("fc:25:3f:c2:3b:0a")) {
                mTv_output.setText(csr.getAddress());
            }
        }*/
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

    protected void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.vp_content);
        fragmentsList = new ArrayList<>();
        Fragment fragment = new ToolsFragment();
        fragmentsList.add(fragment);
        fragment = new TestFragment();
        fragmentsList.add(fragment);
        fragment = new ToolsFragment();
        fragmentsList.add(fragment);

        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), fragmentsList));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(this);
    }

    public void onBluetoothClick(View view) {
        Intent intent = new Intent(this, StBluetoothActivity.class);
        startActivity(intent);
    }

    public void onUserClick(View view) {
        startTestActivity();
    }
}
