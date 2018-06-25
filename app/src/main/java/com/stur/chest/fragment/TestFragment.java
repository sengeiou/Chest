package com.stur.chest.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.stur.activity.BetterListDemoActivity;
import com.stur.chest.R;
import com.stur.chest.activity.ClientActivity;
import com.stur.chest.activity.ServerActivity;
import com.stur.lib.Log;
import com.stur.lib.SystemPropertiesProxy;
import com.stur.lib.UIHelper;
import com.stur.lib.activity.WebServerActivity;
import com.stur.lib.bt.StBluetoothActivity;
import com.stur.lib.constant.StActivityName;
import com.stur.lib.constant.StConstant;

import static com.stur.lib.constant.StActivityName.DEFAULT_ACTIVITY;

public class TestFragment extends Fragment {
    private static final String UNLOCK_ADB        = "persist.sys.adbcontrol";
    private static final String UNLOCK_BLUETOOTH = "persist.sys.bluet";
    private static final String UNLOCK_DATA      = "persist.sys.mobiledata";
    private static final String UNLOCK_WIFI      = "persist.sys.wificontrol";
    private static final String UNLOCK_GPS       = "persist.sys.gpscontrol";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, null);
        Log.d(view, "onCreateView");
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEFAULT_ACTIVITY.length() > 0) {
            //startTestActivity();
        }
        Log.d(getActivity(), "TestFragment onResume");
    }

    private void initView(View view) {
        view.findViewById(R.id.btn_user_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTestActivity();
            }
        });

        view.findViewById(R.id.btn_web_server).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TestFragment.this.getContext(), WebServerActivity.class));
            }
        });

        view.findViewById(R.id.btn_bluetooth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TestFragment.this.getContext(), StBluetoothActivity.class));
            }
        });

        view.findViewById(R.id.btn_select_role).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectRole();
            }
        });

        view.findViewById(R.id.btn_better_list_frgmt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TestFragment.this.getContext(), BetterListDemoActivity.class));
            }
        });

        view.findViewById(R.id.btn_tcp_client).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TestFragment.this.getContext(), ClientActivity.class));
            }
        });

        view.findViewById(R.id.btn_tcp_server).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TestFragment.this.getContext(), ServerActivity.class));
            }
        });

        view.findViewById(R.id.btn_unlock_adb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!"on".equals(SystemPropertiesProxy.get(getContext(), UNLOCK_ADB))){
                    SystemPropertiesProxy.set(getContext(), UNLOCK_ADB, "on");
                    ((Button)view.findViewById(R.id.btn_unlock_adb)).setText(UNLOCK_ADB + ": on");
                } else {
                    SystemPropertiesProxy.set(getContext(), UNLOCK_ADB, "off");
                    ((Button)view.findViewById(R.id.btn_unlock_adb)).setText(UNLOCK_ADB + ": off");
                }
            }
        });

        view.findViewById(R.id.btn_unlock_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!"on".equals(SystemPropertiesProxy.get(getContext(), UNLOCK_BLUETOOTH))){
                    SystemPropertiesProxy.set(getContext(), UNLOCK_BLUETOOTH, "on");
                    ((Button)view.findViewById(R.id.btn_unlock_bt)).setText(UNLOCK_BLUETOOTH + ": on");
                } else {
                    SystemPropertiesProxy.set(getContext(), UNLOCK_BLUETOOTH, "off");
                    ((Button)view.findViewById(R.id.btn_unlock_bt)).setText(UNLOCK_BLUETOOTH + ": off");
                }
            }
        });

        view.findViewById(R.id.btn_unlock_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!"on".equals(SystemPropertiesProxy.get(getContext(), UNLOCK_DATA))){
                    SystemPropertiesProxy.set(getContext(), UNLOCK_DATA, "on");
                    ((Button)view.findViewById(R.id.btn_unlock_data)).setText(UNLOCK_DATA + ": on");
                } else {
                    SystemPropertiesProxy.set(getContext(), UNLOCK_DATA, "off");
                    ((Button)view.findViewById(R.id.btn_unlock_data)).setText(UNLOCK_DATA + ": off");
                }
            }
        });

        view.findViewById(R.id.btn_unlock_wifi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!"on".equals(SystemPropertiesProxy.get(getContext(), UNLOCK_WIFI))){
                    SystemPropertiesProxy.set(getContext(), UNLOCK_WIFI, "on");
                    ((Button)view.findViewById(R.id.btn_unlock_wifi)).setText(UNLOCK_WIFI + ": on");
                } else {
                    SystemPropertiesProxy.set(getContext(), UNLOCK_WIFI, "off");
                    ((Button)view.findViewById(R.id.btn_unlock_wifi)).setText(UNLOCK_WIFI + ": off");
                }
            }
        });

        view.findViewById(R.id.btn_unlock_gps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!"on".equals(SystemPropertiesProxy.get(getContext(), UNLOCK_GPS))){
                    SystemPropertiesProxy.set(getContext(), UNLOCK_GPS, "on");
                    ((Button)view.findViewById(R.id.btn_unlock_gps)).setText(UNLOCK_GPS + ": on");
                } else {
                    SystemPropertiesProxy.set(getContext(), UNLOCK_GPS, "off");
                    ((Button)view.findViewById(R.id.btn_unlock_gps)).setText(UNLOCK_GPS + ": off");
                }
            }
        });


    }

    public void startTestActivity() {
        String clsName = SystemPropertiesProxy.get(getActivity(), StActivityName.PROP_ACTIVITY_NAME, DEFAULT_ACTIVITY);
        if (clsName != null && clsName.length() > 0) {
            try {
                Class cls = Class.forName(clsName);
                Intent intent = new Intent(getActivity(), cls);
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

    private void selectRole() {
        if (StConstant.isRoleServer(getContext())) {
            UIHelper.toastMessage(getContext(), "server started");
        } else if (StConstant.isRoleClient(getContext())){
            UIHelper.toastMessage(getContext(), "client started");
        } else if (StConstant.isRoleNone(getContext())) {
            new AlertDialog.Builder(getContext())
                    .setMessage("please select role")
                    .setPositiveButton("Client", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            startActivity(new Intent(getContext(), ClientActivity.class));
                        }
                    })
                    .setNegativeButton("Server", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            startActivity(new Intent(getContext(), ServerActivity.class));
                        }
                    })
                    .create()
                    .show();
        } else {
            Log.e(getContext(), "something is wrong when read role");
        }
    }
}
