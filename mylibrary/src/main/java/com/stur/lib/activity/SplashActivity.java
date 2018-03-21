package com.stur.lib.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.stur.lib.R;
import com.stur.lib.SharedPreferenceUtils;
import com.stur.lib.config.ConfigBase;
import com.stur.lib.constant.StConstant;
import com.stur.lib.os.PackageUtils;
import com.stur.lib.view.BlankFragment;
import com.stur.lib.view.CircleIndicator;

import java.util.HashMap;
import java.util.Map;


public class SplashActivity extends ActivityBase {
    private ViewPager mViewPager;
    private CircleIndicator mCircleIndicator;
    private boolean mEntranceShow = ConfigBase.sSplashEntranceShowDefault;

    @Override
    protected void beforeInitView() {
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void initView() {
        if (ignoreSplash()) {
            return;
        }

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

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {

    }

    private boolean ignoreSplash() {
        if(ConfigBase.sSplashOnlyOnce && SharedPreferenceUtils.contatins(this, StConstant.FILE_NAME, PackageUtils.getAppVersionCode(this) + "")){
            /*Intent intent=new Intent(SplashActivity.this,SelectDeivce.class);
            startActivity(intent);*/
            this.finish();
            return true;
        }
        return false;
    }

    public void setEntraceShow(boolean isShow) {
        mEntranceShow = isShow;
    }

    public boolean getEntraceShow() {
        return mEntranceShow;
    }
}
