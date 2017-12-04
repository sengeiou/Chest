package com.stur.chest;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.stur.chest.ChestService.MyBinder;
import com.stur.lib.Log;
import com.stur.lib.SystemPropertiesProxy;
import com.stur.lib.Utils;
import com.stur.lib.activity.ActivityBase;
import com.stur.lib.web.NanoHttpdServer;

import java.io.IOException;

public class ChestActivity extends ActivityBase {
    public static final String PROP_ACTIVITY_NAME = "persist.stur.activity";

    Button mBtnDemo;
    TextView mTvOutput;
    ImageView mQRCodeImg;
    Handler mHandler;
    MyBinder myBinder;
    private NanoHttpdServer httpServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chest);
        mBtnDemo = (Button)findViewById(R.id.Btn_chest_demo);
        mTvOutput = (TextView)findViewById(R.id.tv_output);
        mQRCodeImg = (ImageView)findViewById(R.id.iv_qrcode);
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

    public void onTestClick(View view) {
        String clsName = SystemPropertiesProxy.get(this, PROP_ACTIVITY_NAME, "");
        if (clsName != null && clsName.length() > 0) {
            try {
                Class cls = Class.forName(clsName);
                Intent intent = new Intent(this, cls);
                startActivity(intent);
                return;
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        //selectActivity("encoder", "decoder", MediaEncoderActivity.class, MediaDecoderActivity.class);

        //mTvOutput.setText("ScreenMetrics:" +DisplayUtil.getScreenMetrics(this).x + ", " + DisplayUtil.getScreenMetrics(this).y);

        /*try {
            runDownloadManagerTest(R.raw.valid_chain, R.raw.test_key);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/

        //boolean b = Settings.System.putInt(this.getContentResolver(),"hd_voice_on",2);
        //Utils.display(this, String.valueOf(b));


        /*String out = "";
        ArrayList<HashMap<String,String>> allPics = MediaUtils.getAllPictures(this);
        for(HashMap<String,String> hm : allPics) {
            out += hm.get("thumbnail_path");
            out += "/";
        }
        Utils.display(this, out);*/

        /*Intent startIntent = new Intent(this, SocketService.class);
        startService(startIntent);*/

        //TCPClient tc = TCPClient.init(this, "www.baidu.com", "80");
        //tc.start();

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

    public void onCmdClick(View view) {
        Intent intent = new Intent(this, CommandActivity.class);
        startActivity(intent);
    }

    public void onLogClick(View view) {
        String prop = "log.tag." + com.stur.lib.config.ConfigBase.APP_NAME;
        String[] llArr = {"V", "D", "I", "W", "E", "A"};
        String logLevel = SystemPropertiesProxy.get(this, prop, "V");
        String nextLogLevel = "D";
        for(int i=0; i<llArr.length; i++) {
            if (llArr[i].equals(logLevel) && i != llArr.length - 1) {
                nextLogLevel = llArr[i+1];
                break;
            } else if (logLevel.equals(llArr[llArr.length -1])) {
                nextLogLevel = llArr[0];
                break;
            }
        }

        mTvOutput.setText("log.tag." + com.stur.lib.config.ConfigBase.APP_NAME + ": " + nextLogLevel);
        SystemPropertiesProxy.set(this, prop, nextLogLevel);
    }
}
