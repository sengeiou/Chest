package com.stur.chest;

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
import com.stur.lib.AdbUtils;
import com.stur.lib.Constant;
import com.stur.lib.Log;
import com.stur.lib.SystemPropertiesProxy;
import com.stur.lib.Utils;
import com.stur.lib.network.WakeOnLan;
import com.stur.lib.network.WifiUtils;

import java.io.IOException;
import java.util.ArrayList;

public class ToolsFragment extends Fragment {
    Button mBtnCmdExc;
    TextView mEtInput;
    TextView mTvOutput;
    Button mBtnWifiAdb;
    Button mBtnPCWakeup;
    Button mBtnLogLevel;
    PieChart mPieChart;
    Handler mHandler;
    String mOutput = "";

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

        initPieChart(view);
    }

    private void initPieChart(View view) {
        mPieChart = (PieChart)view.findViewById(R.id.pc_controller);
        //mPieChart.setUsePercentValues(true);//����value�Ƿ�����ʾ�ٷ���,Ĭ��Ϊfalse
        mPieChart.setDescription(null);  //���½ǵ������������֣���Ϊnull��Ϊ����ʾ
        //mPieChart.setDescription("ȫ���������");//��������
        //mPieChart.setDescriptionTextSize(20);//�������������С
        //mPieChart.setDescriptionColor(); //����������ɫ
        //pieChart.setDescriptionTypeface();//������������
        mPieChart.setDrawLegendEnabled(false);  //�����ұ��м��˵��ͼ���Ƿ���ʾ

        mPieChart.setExtraOffsets(5, 5, 5, 5);//���ñ�״ͼ�����������µ�ƫ��������dpΪ��λ

        mPieChart.setDragDecelerationFrictionCoef(0.95f);//��������ϵ��,��Χ��[0,1]֮��,ԽС��״ͼת��Խ����

        mPieChart.setDrawCenterText(true);//�Ƿ�����м������
        mPieChart.setCenterTextColor(Color.RED);//�м��������ɫ
        mPieChart.setCenterTextSize(24);//�м�����������С

        mPieChart.setDrawHoleEnabled(true);//�Ƿ���Ʊ�״ͼ�м��Բ
        mPieChart.setHoleColor(Color.WHITE);//��״ͼ�м��Բ�Ļ�����ɫ
        mPieChart.setHoleRadius(58f);//��״ͼ�м��Բ�İ뾶��С

        mPieChart.setTransparentCircleColor(Color.BLACK);//����Բ������ɫ
        mPieChart.setTransparentCircleAlpha(110);//����Բ����͸����[0,255]
        mPieChart.setTransparentCircleRadius(60f);//����Բ���İ뾶ֵ

        // enable rotation of the chart by touch
        mPieChart.setRotationEnabled(true);//���ñ�״ͼ�Ƿ������ת(Ĭ��Ϊtrue)
        mPieChart.setRotationAngle(10);//���ñ�״ͼ��ת�ĽǶ�

        mPieChart.setHighlightPerTapEnabled(true);//������ת��ʱ����е�tab�Ƿ����(Ĭ��Ϊtrue)

        Legend l = mPieChart.getLegend();  //Legend�����ұ��м��˵������һ������ָ����ͬ��ɫ��Ӧ�����
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);//����ÿ��tab����ʾλ��
        l.setXEntrySpace(0f);
        l.setYEntrySpace(0f);//����tab֮��Y�᷽���ϵĿհ׼��ֵ
        l.setYOffset(0f);

        // entry label styling
        //mPieChart.setDrawEntryLabels(true);//�����Ƿ����Label
        //mPieChart.setEntryLabelColor(Color.BLACK);//���û���Label����ɫ
        //pieChart.setEntryLabelTypeface(mTfRegular);
        //mPieChart.setEntryLabelTextSize(10f);//���û���Label�������С

        //mPieChart.setOnChartValueSelectedListener(this);//��ֵ���ʱ��Ļص�
        mPieChart.animateY(3400, Easing.EasingOption.EaseInQuad);//����Y���ϵĻ��ƶ���
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < 3; i++){
            PieEntry pieEntry = new PieEntry((float)20, 400);
            pieEntries.add(pieEntry);
        }

        String centerText = "�������";
        mPieChart.setCenterText(centerText);//�����м������

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        pieDataSet.setColors(colors);

        pieDataSet.setSliceSpace(3f);//����ѡ�е�Tab�����ߵľ���
        pieDataSet.setSelectionShift(5f);//����ѡ�е�tab�Ķ������
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
        sb.append("setprop  " + Constant.PROP_ACTIVITY_NAME + "com.stur.lib.activity.SplashActivity" + "\n");
        mTvOutput.setText(sb);
    }
}
