package com.stur.lib.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import com.stur.lib.R;
import com.stur.lib.location.LocationService;
/**
 *
 * @description
 * @charset UTF-8
 * @author xiong_it
 * @date 2015-7-20上午10:34:58
 * @version
 */
public class LocationActivity extends Activity {
    public static final String LOCATION = "location";
    public static final String LOCATION_ACTION = "locationAction";

    private TextView text;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        text = (TextView) findViewById(R.id.text);

        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(LOCATION_ACTION);
        this.registerReceiver(new LocationBroadcastReceiver(), filter);

        // 启动服务
        Intent intent = new Intent(this, LocationService.class);
//        intent.setClass(this, LocationService.class);
        startService(intent);

        // 等待提示
        dialog = new ProgressDialog(this);
        dialog.setMessage("Locating...");
        dialog.setCancelable(true);
        dialog.show();
    }

    private class LocationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String locationInfo = intent.getStringExtra(LOCATION);
            text.setText(locationInfo);
            dialog.dismiss();
            LocationActivity.this.unregisterReceiver(this);// 不需要时注销
        }
    }

}
