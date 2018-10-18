package com.stur.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stur.chest.R;
import com.stur.lib.Log;
import com.stur.lib.view.GestureListView;
import com.stur.lib.view.OnFlingListener;

/**
 * 继承这个activity的都具有手势滑动功能
 */
public class GestureActivity extends Activity implements OnGestureListener {
    // 这个用来测试activity本身响应手势事件
    GestureDetector gestureDetector;
    LinearLayout mLinearLayout;

    // 这个用来测试activity中的listview响应手势事件
    GestureListView mGestureListView;
    MyBaseAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gestureDetector = new GestureDetector(this, this);

        setContentView(R.layout.activity_blank);
        mLinearLayout = (LinearLayout) this.findViewById(R.id.ll_root);
        mGestureListView = new GestureListView(this);
        mGestureListView.setOnFlingListener(new OnFlingListener() {
            @Override
            public void onLeftSlide() {
                Log.d(this, "onLeftSlide");
            }

            @Override
            public void onRightSlide() {
                Log.d(this, "onRightSlide");
            }

            @Override
            public void onUpSlide() {

            }

            @Override
            public void onDownSlide() {

            }
        });
        mAdapter = new MyBaseAdapter(this);
        mGestureListView.setAdapter(mAdapter);
        mLinearLayout.addView(mGestureListView);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    /**
     * 滑动事件的处理
     */
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        //左滑动
        if (e1.getX() - e2.getX() > 80 || e1.getY() - e2.getY() > 80) {
            Log.d(this, "onFling: left slide");
        }
        //右滑动
        else if (e1.getX() - e2.getX() < -80 || e1.getY() - e2.getY() < -80) {
            Log.d(this, "onFling: right slide");
        }
        return true;
    }

    public class MyBaseAdapter extends BaseAdapter {
        Context context;

        public MyBaseAdapter(Context context) {
            // TODO Auto-generated constructor stub
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            TextView textView = new TextView(context);
            textView.setText("测试我的滑动");
            return textView;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 10;
        }

    }
}
