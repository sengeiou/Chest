package com.stur.chest.fragment;

import android.graphics.Color;
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

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.reflect.TypeToken;
import com.stur.chest.R;
import com.stur.chest.dto.UserAccountDTO;
import com.stur.chest.utils.ApiUtils;
import com.stur.lib.AdbUtils;
import com.stur.lib.Log;
import com.stur.lib.SharedPreferenceUtils;
import com.stur.lib.StringUtils;
import com.stur.lib.SystemPropertiesProxy;
import com.stur.lib.UIHelper;
import com.stur.lib.Utils;
import com.stur.lib.config.ConfigManager;
import com.stur.lib.constant.StActivityName;
import com.stur.lib.constant.StCommand;
import com.stur.lib.file.FileUtils;
import com.stur.lib.network.TMUtils;
import com.stur.lib.network.WakeOnLan;
import com.stur.lib.network.WifiUtils;
import com.stur.lib.os.PackageUtils;
import com.stur.lib.web.HttpFactory;
import com.stur.lib.web.api.HttpResponseListener;
import com.stur.lib.web.request.HttpMethod;
import com.stur.lib.web.request.HttpRequest;
import com.stur.lib.web.response.HttpResponse;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.stur.lib.Utils.execCommand;


public class ToolsFragment extends Fragment {
    private Button mBtnCmdExc;
    private TextView mEtInput;
    private TextView mTvOutput;
    private PieChart mPieChart;
    private Handler mHandler;
    private String mOutput = "";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tools, null);
        Log.d(view, "onCreateView");

        initView(view);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            }
        };
        return view;
    }

    private void initView(View view) {
        mBtnCmdExc = (Button) view.findViewById(R.id.btn_cmd_exc);
        mBtnCmdExc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cmd = mEtInput.getText().toString();
                Log.d(this, "onCmdExcClick E: " + cmd);
                if (cmd != null && cmd.length() > 0) {
                    try {
                        mOutput += execCommand(cmd);
                    } catch (Exception e) {
                        Log.e(getActivity(), e.toString());
                    } finally {

                    }
                    mOutput += '\n';
                    mTvOutput.setText(mOutput);
                }
            }
        });

        mEtInput = (TextView) view.findViewById(R.id.et_cmd_input);
        mTvOutput = (TextView) view.findViewById(R.id.tv_output);
        mEtInput.setText("getprop " + AdbUtils.WIFI_ADB_PORT_PROP);

        view.findViewById(R.id.btn_wifiadb).setOnClickListener(new View.OnClickListener() {
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

        view.findViewById(R.id.btn_wakeup_pc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(this, "onPCWakeup");
                String mac = mEtInput.getText().toString();
                if(StringUtils.isMacAddr(mac)) {
                    SharedPreferenceUtils.putString(SharedPreferenceUtils.KEY_MAC_ADDR, mac);
                    UIHelper.toastMessageMiddle(getContext(), "new mac addr is saved");
                } else {
                    String oldMac = SharedPreferenceUtils.getString(SharedPreferenceUtils.KEY_MAC_ADDR);
                    if (StringUtils.isMacAddr(oldMac)) {
                        mac = oldMac;
                    } else {
                        UIHelper.toastMessageMiddle(getContext(), "no mac addr is found");
                        return;
                    }
                }
                WakeOnLan.start(mac);
                mTvOutput.setText("wake on lan at: " + mac);
            }
        });

        view.findViewById(R.id.btn_log_offline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileUtils.deleteOfflineLogs();
                PackageUtils.startOfflineLogActivity(getContext());
                try {
                    //清理缓存未报错，但始终未清理成功，adb可以清理成功，可能是需要root权限。
                    String ret = Utils.execCommand(StCommand.CLEAR_LOG_CACHE);
                    Log.d(this, "clear log cache return: " + ret);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        view.findViewById(R.id.btn_log_level).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String prop = "log.tag." + ConfigManager.getInstance().getAppName();
                String[] llArr = Log.LOG_LEVEL_ARR;
                String logLevel = SystemPropertiesProxy.get(getActivity(), prop, "V");
                String nextLogLevel = "D";
                for (int i = 0; i < llArr.length; i++) {
                    if (llArr[i].equals(logLevel) && i != llArr.length - 1) {
                        nextLogLevel = llArr[i + 1];
                        break;
                    } else if (logLevel.equals(llArr[llArr.length - 1])) {
                        nextLogLevel = llArr[0];
                        break;
                    }
                }
                mTvOutput.setText("log.tag." + ConfigManager.getInstance().getAppName() + ": " + nextLogLevel);
                //非ROM应用这里写系统属性不会成功，功能失效
                SystemPropertiesProxy.set(getContext(), prop, nextLogLevel);
            }
        });

        //查看应用的签名证书，输入框中输入已安装应用的包名
        view.findViewById(R.id.btn_cert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pkg = mEtInput.getText().toString();
                if (pkg != null && pkg.length() > 0) {
                    mTvOutput.setText(PackageUtils.getSign(getContext(), pkg));
                }
            }
        });

        view.findViewById(R.id.btn_share_me).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tmpPath = FileUtils.getWorkPath(getContext(),null) + "chest.apk";
                try {
                    FileUtils.copyFile(getContext(), getContext().getPackageResourcePath(), tmpPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileUtils.shareFile(getContext(), tmpPath);
            }
        });

        view.findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this is test field
                //BluetoothUtils.getBondedDevices();
                //CrashReport.testJavaCrash();
                //mTvOutput.setText(PackageUtils.getSign(getContext(), "com.qualcomm.uimremoteclient"));

                //拉起某个界面
                //Intent intent = new Intent(this, WiFiActivity.class);
                //startActivity(intent);

                //拉起某个 服务
                //Intent startIntent = new Intent(this, ChestService.class);
                //startService(startIntent);

                //开启ping
                //WifiUtils.startPing();

                //从arp缓存中获取某个mac地址对应的IP地址
                //ArrayList<ClientScanResult> csrList = WifiUtils.getClientList(false, 0);
                //for (ClientScanResult csr : csrList) {
                //if(csr.getMac().equals("fc:25:3f:c2:3b:0a")) {
                //mTv_output.setText(csr.getAddress()); }
                //}

                //testHttpRequest();

                //String ret = IccidParser.getInstance().getCarrier("898603");
                TMUtils.callPhone(getContext(), "10010", 1);

            }
        });

        initPieChart(view);
    }

    private void initPieChart(View view) {
        mPieChart = (PieChart) view.findViewById(R.id.pc_controller);
        //mPieChart.setUsePercentValues(true);//设置value是否用显示百分数,默认为false
        //mPieChart.setDescription(null);  //右下角的总体描述部分，设为null即为不显示

        //设置描述部分
//        Description des = new Description();
//        des.setText("流量套餐概况");
//        des.setTextSize(5);
//        des.setTextColor(0x555555);
//        des.setPosition(765,1683);
//        des.setEnabled(true);
//        mPieChart.setDescription(des);

//        Description des = mPieChart.getDescription();
//        des.setText("流量套餐概况");
//        des.setTextSize(5);
//        des.setTextColor(0x555555);
//        des.setPosition(765,1683);
        mPieChart.getDescription().setEnabled(true);


        mPieChart.setDrawLegendEnabled(true);  //设置右边中间的说明图表是否显示

        mPieChart.setExtraOffsets(5, 5, 5, 5);//设置饼状图距离左上右下的偏移量，以dp为单位

        mPieChart.setDragDecelerationFrictionCoef(0.95f);//设置阻尼系数,范围在[0,1]之间,越小饼状图转动越困难

        mPieChart.setDrawCenterText(true);//是否绘制中间的文字
        mPieChart.setCenterTextColor(Color.RED);//中间的文字颜色
        mPieChart.setCenterTextSize(24);//中间的文字字体大小

        mPieChart.setDrawHoleEnabled(true);//是否绘制饼状图中间的圆
        mPieChart.setHoleColor(Color.WHITE);//饼状图中间的圆的绘制颜色
        mPieChart.setHoleRadius(58f);//饼状图中间的圆的半径大小

        mPieChart.setTransparentCircleColor(Color.BLACK);//设置圆环的颜色
        mPieChart.setTransparentCircleAlpha(110);//设置圆环的透明度[0,255]
        mPieChart.setTransparentCircleRadius(60f);//设置圆环的半径值

        // enable rotation of the chart by touch
        mPieChart.setRotationEnabled(false);//设置饼状图是否可以旋转(默认为true)
        mPieChart.setRotationAngle(10);//设置饼状图旋转的角度

        mPieChart.setHighlightPerTapEnabled(true);//设置旋转的时候点中的tab是否高亮(默认为true)

        Legend l = mPieChart.getLegend();  //Legend就是右边中间的说明区域，一般用于指明不同颜色对应的类别
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);//设置每个tab的显示位置
        l.setXEntrySpace(0f);
        l.setYEntrySpace(0f);//设置tab之间Y轴方向上的空白间距值
        l.setYOffset(0f);

        // entry label styling
        mPieChart.setDrawEntryLabels(true);//设置是否绘制Label
        mPieChart.setEntryLabelColor(Color.BLACK);//设置绘制Label的颜色
        //mPieChart.setEntryLabelTypeface(mTfRegular);
        mPieChart.setEntryLabelTextSize(10f);//设置绘制Label的字体大小

        //mPieChart.setOnChartValueSelectedListener(this);//设值点击时候的回调
        mPieChart.animateY(3400, Easing.EasingOption.EaseInQuad);//设置Y轴上的绘制动画
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            PieEntry pieEntry = new PieEntry((float) 20, 400);
            pieEntries.add(pieEntry);
        }

        String centerText = "控制面板";
        mPieChart.setCenterText(centerText);//设置中间的文字

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        pieDataSet.setColors(colors);

        pieDataSet.setSliceSpace(3f);//设置选中的Tab离两边的距离
        pieDataSet.setSelectionShift(5f);//设置选中的tab的多出来的
        PieData pieData = new PieData();
        pieData.setDataSet(pieDataSet);

        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.BLUE);

        mPieChart.setData(pieData);
        // undo all highlights
        mPieChart.highlightValues(null);
        mPieChart.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(getActivity(), "ToolsFragment onResume");
        StringBuilder sb = new StringBuilder();
        sb.append("setprop " + AdbUtils.WIFI_ADB_PORT_PROP + " " + AdbUtils.WIFI_ADB_DEFAULT_PORT + "\n");
        sb.append("adb connect " + WifiUtils.getIp(getActivity()) + ":" + AdbUtils.WIFI_ADB_DEFAULT_PORT + "\n");
        sb.append("setprop  " + StActivityName.PROP_ACTIVITY_NAME + "com.stur.lib.activity.SplashActivity" + "\n");
        sb.append("mac addr: " + SharedPreferenceUtils.getString(SharedPreferenceUtils.KEY_MAC_ADDR));
        mTvOutput.setText(sb);
    }

    private void testHttpRequest() {
        HttpRequest httpRequest = new HttpRequest("http://10.66.128.123:8088/test", HttpMethod.POST);
        httpRequest.addParams(ApiUtils.KEY_TOKEN, "123456");
        HttpFactory.getHttpService().sendRequest(httpRequest, new HttpResponseListener() {
            @Override
            public void onSuccess(HttpResponse response) {
                Log.d(this, "[response]" + response.toString());
                Type type = new TypeToken<UserAccountDTO>() {
                }.getType();
                UserAccountDTO uat = response.convert(type);
                String email = uat.getEmail();
                UIHelper.toastMessageMiddle(getContext(), "email = " + email);
            }
        });
    }
}
