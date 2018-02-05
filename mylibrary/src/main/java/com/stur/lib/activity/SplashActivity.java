package com.stur.lib.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.stur.lib.StConstant;
import com.stur.lib.R;
import com.stur.lib.SharedPreferenceUtils;
import com.stur.lib.Utils;
import com.stur.lib.config.ConfigBase;
import com.stur.lib.widget.BlankFragment;
import com.stur.lib.widget.CircleIndicator;

import java.util.HashMap;
import java.util.Map;


public class SplashActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private CircleIndicator mCircleIndicator;
    private boolean mEntranceShow = ConfigBase.SPLASH_ENTRANCE_SHOW_DEFAULT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (ignoreSplash()) return;

        initView();
    }

    private boolean ignoreSplash() {
        if(ConfigBase.SPLASH_ONLY_ONCE && SharedPreferenceUtils.contatins(this, StConstant.FILE_NAME, Utils.getAppVersionCode(this) + "")){
            /*Intent intent=new Intent(SplashActivity.this,SelectDeivce.class);
            startActivity(intent);*/
            this.finish();
            return true;
        }
        return false;
    }

    private void initView() {
        mViewPager= (ViewPager) findViewById(R.id.viewpager);
        mCircleIndicator= (CircleIndicator) findViewById(R.id.circle_indicator);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            private int[] resId={R.mipmap.ic_help_view_1,R.mipmap.ic_help_view_2,R.mipmap.ic_help_view_3,R.mipmap.ic_help_view_4};
            private Map<Integer,Fragment> fgMap=new HashMap<Integer,Fragment>();
            @Override
            public Fragment getItem(int i) {
                Fragment fragment=fgMap.get(i);
                if(fragment==null){
                    fragment=BlankFragment.newInstance(resId[i], i, resId.length, getEntraceShow());
                    fgMap.put(i,fragment);
                }
                return fragment;
            }

            @Override
            public int getCount() {
                return resId.length;
            }
        });
        mCircleIndicator.setViewPager(mViewPager);
    }

    public void setEntraceShow(boolean isShow) {
        mEntranceShow = isShow;
    }

    public boolean getEntraceShow() {
        return mEntranceShow;
    }
}
