package com.stur.lib.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stur.lib.Log;
import com.stur.lib.R;
import com.stur.lib.app.AppManager;
import com.stur.lib.app.IContext;
import com.stur.lib.os.BusProvider;

import java.util.ArrayList;


public abstract class FragmentActivityBase extends ActivityBase
        implements View.OnClickListener,
        OnPageChangeListener, IContext {
    protected TextView tab1Tv, tab2Tv, tab3Tv;   // 三个textview
    protected ImageView cursorImg;    // 指示器
    protected ViewPager viewPager;
    protected ArrayList<Fragment> fragmentsList;    // fragment对象集合
    protected int currentIndex = 0;    // 记录当前选中的tab的index
    protected int offset = 0;    // 指示器的偏移量
    protected int leftMargin = 0;    // 左margin
    protected int screenWidth = 0;    // 屏幕宽度
    protected int screen1_3;    // 屏幕宽度的三分之一
    protected RelativeLayout.LayoutParams lp;

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        BusProvider.getInstance().unregister(this);
        AppManager.getInstance().removeActivity(this);
    }

    @Override
    protected void beforeInitView() {
        setContentView(R.layout.activity_fragment_base);
    }

    @Override
    protected void initView() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screen1_3 = screenWidth / 3;

        cursorImg = (ImageView) findViewById(R.id.iv_cursor);
        lp = (RelativeLayout.LayoutParams) cursorImg.getLayoutParams();
        leftMargin = lp.leftMargin;

        tab1Tv = (TextView) findViewById(R.id.tv_tab1);
        tab2Tv = (TextView) findViewById(R.id.tv_tab2);
        tab3Tv = (TextView) findViewById(R.id.tv_tab3);
        //
        tab1Tv.setOnClickListener(this);
        tab2Tv.setOnClickListener(this);
        tab3Tv.setOnClickListener(this);

        initViewPager();
    }

    protected void initViewPager() {}

    @Override
    public void onClick(View v) {
        /* for Resource IDs are not allowed to be used in a switch statement in Android library modules
        switch (v.getId()) {
            case R.id.tv_tab1:
                viewPager.setCurrentItem(0);
                break;
            case R.id.tv_tab2:
                viewPager.setCurrentItem(1);
                break;
            case R.id.tv_tab3:
                viewPager.setCurrentItem(2);
                break;
        }*/

        if (v.getId() == R.id.tv_tab1 ) {
            viewPager.setCurrentItem(0);
        } else if (v.getId() == R.id.tv_tab2) {
            viewPager.setCurrentItem(1);
        } else if (v.getId() == R.id.tv_tab3) {
            viewPager.setCurrentItem(2);
        }


    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        offset = (screen1_3 - cursorImg.getLayoutParams().width) / 2;
        Log.v(this, "onPageScrolled: " + position + "--" + positionOffset + "--"+ positionOffsetPixels);
        final float scale = getResources().getDisplayMetrics().density;
        if (position == 0) {// 0<->1
            lp.leftMargin = (int) (positionOffsetPixels / 3) + offset;
            //startTestActivity();
        } else if (position == 1) {// 1<->2
            lp.leftMargin = (int) (positionOffsetPixels / 3) + screen1_3 +offset;
        }
        cursorImg.setLayoutParams(lp);
        currentIndex = position;
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageSelected(int arg0) {
    }
}
