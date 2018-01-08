package com.stur.chest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.stur.lib.AdbUtils;
import com.stur.lib.Constant;
import com.stur.lib.Log;
import com.stur.lib.SystemPropertiesProxy;
import com.stur.lib.Utils;
import com.stur.lib.network.WakeOnLan;
import com.stur.lib.network.WifiUtils;

import java.io.IOException;

public class MineFragment extends Fragment {
    Button mBtnCmdExc;
    TextView mEtInput;
    TextView mTvOutput;
    Button mBtnWifiAdb;
    Button mBtnPCWakeup;
    Button mBtnLogLevel;
    Handler mHandler;
    String mOutput = "";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tools, null);

        mBtnCmdExc = (Button)view.findViewById(R.id.Btn_cmd_exc);
        mBtnCmdExc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cmd = mEtInput.getText().toString();
                Log.d(this, "onCmdExcClick E: " + cmd);
                if (cmd != null && cmd.length() > 0) {
                    try {
                        mOutput += Utils.execCommand(cmd);
                    } catch (Exception e) {
                        Log.e(getActivity(), e.toString());
                    }finally {

                    }
                    mOutput += '\n';
                    mTvOutput.setText(mOutput);
                }
            }
        });

        mEtInput = (TextView)view.findViewById(R.id.et_cmd_input);
        mTvOutput = (TextView)view.findViewById(R.id.tv_output);
        mEtInput.setText("setprop " + AdbUtils.WIFI_ADB_PORT_PROP + " " + AdbUtils.WIFI_ADB_DEFAULT_PORT);

        mBtnWifiAdb = (Button) view.findViewById(R.id.Btn_wifiadb);
        mBtnWifiAdb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(this, "onWifiAdbClick");
                try {
                    AdbUtils.enableWifiAdb(getActivity(), null);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        mBtnPCWakeup = (Button)view.findViewById(R.id.Btn_wakeup_pc);
        mBtnPCWakeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(this, "onPCWakeup");
                WakeOnLan.start("408D5CC1DB5B");
                mTvOutput.setText("408D5CC1DB5B");
            }
        });

        mBtnLogLevel = (Button)view.findViewById(R.id.Btn_log_level);
        mBtnLogLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String prop = "log.tag." + com.stur.lib.config.ConfigBase.APP_NAME;
                String[] llArr = {"V", "D", "I", "W", "E", "A"};
                String logLevel = SystemPropertiesProxy.get(getActivity(), prop, "V");
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
                SystemPropertiesProxy.set(getActivity(), prop, nextLogLevel);
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            }
        };
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        StringBuilder sb = new StringBuilder();
        sb.append("setprop " + AdbUtils.WIFI_ADB_PORT_PROP + " " + AdbUtils.WIFI_ADB_DEFAULT_PORT + "\n");
        sb.append("adb connect " + WifiUtils.getIp(getActivity()) + ":" + AdbUtils.WIFI_ADB_DEFAULT_PORT + "\n");
        sb.append("setprop  " + Constant.PROP_ACTIVITY_NAME + "com.stur.lib.activity.SplashActivity" + "\n");
        mTvOutput.setText(sb);
    }
}
