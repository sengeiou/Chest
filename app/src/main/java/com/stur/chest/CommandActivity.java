package com.stur.chest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.stur.lib.AdbUtils;
import com.stur.lib.Log;
import com.stur.lib.Utils;
import com.stur.lib.network.WakeOnLan;
import com.stur.lib.network.WifiUtils;

import java.io.IOException;

public class CommandActivity extends Activity {

    Button mBtnCmdExc;
    TextView mEtInput;
    TextView mTvOutput;
    Handler mHandler;
    String mOutput = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);
        mBtnCmdExc = (Button)findViewById(R.id.Btn_cmd_exc);
        mEtInput = (TextView)findViewById(R.id.et_cmd_input);
        mTvOutput = (TextView)findViewById(R.id.tv_cmd_output);
        mEtInput.setText("setprop " + AdbUtils.WIFI_ADB_PORT_PROP + " " + AdbUtils.WIFI_ADB_DEFAULT_PORT);
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

    public void onCmdExcClick(View view) throws IOException
    {
        String cmd = mEtInput.getText().toString();
        Log.d(this, "onCmdExcClick E: " + cmd);
        if (cmd != null && cmd.length() > 0) {
            mOutput += Utils.execCommand(cmd);
            mOutput += '\n';
            mTvOutput.setText(mOutput);
        }
    }

    public void onWifiAdbClick(View view) {
        Log.d(this, "onWifiAdbClick");
        try {
            AdbUtils.enableWifiAdb(this, null);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void onPCWakeup(View view) {
        Log.d(this, "onPCWakeup");
        WakeOnLan.start("408D5CC1DB5B");
        mTvOutput.setText("408D5CC1DB5B");
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        StringBuilder sb = new StringBuilder();
        sb.append("setprop " + AdbUtils.WIFI_ADB_PORT_PROP + " " + AdbUtils.WIFI_ADB_DEFAULT_PORT + "\n");
        sb.append("adb connect " + WifiUtils.getIp(this) + ":" + AdbUtils.WIFI_ADB_DEFAULT_PORT + "\n");
        sb.append("setprop  " + ChestActivity.PROP_ACTIVITY_NAME + "com.stur.lib.activity.SplashActivity" + "\n");
        mTvOutput.setText(sb);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.d(this, "onDestroy");
    }
}
