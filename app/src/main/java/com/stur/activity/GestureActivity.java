package com.stur.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

import com.stur.lib.Log;

/**
 * 依靠 GestureDetector 实现一些简单的手势捕获
 * 动画实现具体ListView的侧滑置顶/删除效果参见： SwipeListActivity
 */
public class GestureActivity extends Activity implements OnGestureListener {
    // 这个用来测试activity本身响应手势事件
    GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gestureDetector = new GestureDetector(this, this);
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
}
