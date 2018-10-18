package com.stur.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListView;
import android.view.GestureDetector.OnGestureListener;

import com.stur.lib.Log;

/**
 * 该listview实现了对左右滑动的监听事件
 * 用户需要实现OnFlingListener接口，通过setOnFlingListener接口即可实现左右滑动时要实现的操作
 * 加入左右滑动后,listview上下滑动很慢,手指上下移动多少listview就滑动多少,手指离开屏幕立刻停止滚动,没有缓冲
 */
public class GestureListView extends ListView implements OnGestureListener{
    Context context;
    GestureDetector gestureDetector;
    OnFlingListener mListener;

    public GestureListView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
        gestureDetector = new GestureDetector(context, this);
    }

    /**
     * 在xml布局里面使用GestureList，默认的会调用这个构造方法
     *
     * @param context
     * @param attrs
     */
    public GestureListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.context = context;
        gestureDetector = new GestureDetector(context, this);
    }

    public void setOnFlingListener(OnFlingListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (gestureDetector.onTouchEvent(ev)) {
            return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float v, float v1) {
        //左滑动
        if (e1.getX() - e2.getX() > 80) {
            Log.d(this, "onFling left slide");
            if (mListener != null) {
                mListener.onLeftSlide();
            }
            return true;
        }
        // 右滑动
        else if (e1.getX() - e2.getX() < -80) {
            Log.d(this, "onFling right slide");
            if (mListener != null) {
                mListener.onRightSlide();
            }
            return true;
        }
        return true;
    }
}
