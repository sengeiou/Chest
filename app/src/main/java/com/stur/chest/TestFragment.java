package com.stur.chest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stur.lib.Constant;
import com.stur.lib.Log;
import com.stur.lib.SystemPropertiesProxy;

import static com.stur.lib.Constant.DEFAULT_ACTIVITY;

public class TestFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, null);
        Log.d(view, "onCreateView");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEFAULT_ACTIVITY.length() > 0) {
            startTestActivity();
        }
        Log.d(getActivity(), "TestFragment onResume");
    }

    public void startTestActivity() {
        String clsName = SystemPropertiesProxy.get(getActivity(), Constant.PROP_ACTIVITY_NAME, DEFAULT_ACTIVITY);
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
}
