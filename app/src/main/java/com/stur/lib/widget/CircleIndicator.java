package com.stur.lib.widget;

import com.stur.chest.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by lizhangqu on 15-6-16.
 */
public class CircleIndicator extends LinearLayout implements ViewPager.OnPageChangeListener {
    private static final int SCROLL_WHAT=0x01;
    private static final int CIRCLE_STROKE_WIDTH =1;
    private static final int BITMAP_PADDING =2;


    private static final int DEFAULT_CIRCLE_SPACING = 5;
    private static final int DEFAULT_CIRCLE_COLOR=Color.WHITE;
    private static final int DEFAULT_CIRCLE_SIZE=3;
    private static final boolean DEFAULT_CIRCLE_AUTO_SCROLL=false;
    private static final int DEFAULT_CIRCLE_SCROLL_DELAY_TIME=3000;
    private static final boolean DEFAULT_CIRCLE_SCROLL_ANIMATION=true;
    private int mSpacing;
    private int mSize;
    private int mFillColor;
    private int mStrokeColor;
    private boolean mAutoScroll;
    private int mDelayTime;
    private boolean mIsAnimation;

    private ViewPager mViewPager;
    private int mCount;
    private int mLastIndex = 0;
    private Canvas mCanvas;
    private Paint mPaint;
    private Bitmap mSelectBitmap;
    private Bitmap mUnselectBitmap;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCROLL_WHAT:
                    scrollOnce();
                    sendScrollMessage(mDelayTime);
                    break;
            }
        }
    };
    public CircleIndicator(Context context) {
        this(context, null);
    }

    public CircleIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCustomParams(context, attrs);
        init();
    }

    private void initCustomParams(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleIndicator);
        try {
            mSpacing = typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_circle_spacing, DEFAULT_CIRCLE_SPACING);
            mFillColor=typedArray.getColor(R.styleable.CircleIndicator_circle_fill_color,DEFAULT_CIRCLE_COLOR);
            mStrokeColor=typedArray.getColor(R.styleable.CircleIndicator_circle_stroke_color,DEFAULT_CIRCLE_COLOR);
            mSize= typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_circle_radius, DEFAULT_CIRCLE_SIZE);
            mAutoScroll= typedArray.getBoolean(R.styleable.CircleIndicator_circle_auto_scroll, DEFAULT_CIRCLE_AUTO_SCROLL);
            mDelayTime=typedArray.getInt(R.styleable.CircleIndicator_circle_scroll_delay_time,DEFAULT_CIRCLE_SCROLL_DELAY_TIME);
            mIsAnimation=typedArray.getBoolean(R.styleable.CircleIndicator_circle_scroll_animation,DEFAULT_CIRCLE_SCROLL_ANIMATION);

        } finally {
            typedArray.recycle();
        }
    }

    private void init() {
        setOrientation(HORIZONTAL);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mPaint.setStrokeWidth(dip2px(CIRCLE_STROKE_WIDTH));
        mPaint.setColor(mFillColor);

        int size=dip2px(mSize+ BITMAP_PADDING + BITMAP_PADDING);
        int radius=dip2px(mSize / 2);
        int centerPoint=radius+ BITMAP_PADDING;

        mSelectBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        mUnselectBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        mCanvas = new Canvas();
        mCanvas.setBitmap(mSelectBitmap);


        mCanvas.drawCircle(centerPoint, centerPoint, radius, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mStrokeColor);
        mCanvas.setBitmap(mUnselectBitmap);
        mCanvas.drawCircle(centerPoint, centerPoint, radius, mPaint);

    }

    public void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        mViewPager.setOnPageChangeListener(this);
        if (mViewPager != null) {
            mCount = mViewPager.getAdapter().getCount();
            addIndicator(mCount);
        }

    }

    private void addIndicator(int count) {
        removeIndicator();
        if (count <= 0)
            return;
        for (int i = 0; i < count; i++) {
            ImageView imageView = new ImageView(getContext());
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.leftMargin = mSpacing/2;
            params.rightMargin = mSpacing/2;

            imageView.setImageBitmap(mUnselectBitmap);
            addView(imageView, params);
        }
        initIndicator();
        if(mAutoScroll){
            sendScrollMessage(mDelayTime);
        }
    }

    private void initIndicator() {
        ((ImageView) getChildAt(0)).setImageBitmap(mSelectBitmap);
        mLastIndex=0;
    }

    private void removeIndicator() {
        removeAllViews();
    }

    private void updateIndicator(int position) {

        if (position != mLastIndex) {
            ((ImageView) getChildAt(mLastIndex)).setImageBitmap(mUnselectBitmap);
            ((ImageView) getChildAt(position)).setImageBitmap(mSelectBitmap);
        }
        mLastIndex = position;

    }

    private void setAutoScroll(boolean autoScroll){
        if (autoScroll){
            sendScrollMessage(mDelayTime);
        }else{
            mHandler.removeMessages(SCROLL_WHAT);
        }
        mAutoScroll=autoScroll;

    }
    public boolean isAutoScroll() {
        return mAutoScroll;
    }

    public int getDelayTime() {
        return mDelayTime;
    }

    public void setDelayTime(int delayTime) {
        mDelayTime = delayTime;
    }

    public boolean isAnimation() {
        return mIsAnimation;
    }

    public void setIsAnimation(boolean isAnimation) {
        mIsAnimation = isAnimation;
    }

    public void scrollOnce() {
        PagerAdapter adapter = mViewPager.getAdapter();
        if (adapter == null) {
            return;
        }
        int nextIndex=mViewPager.getCurrentItem();
        ++nextIndex;
        if(nextIndex >=mCount){
            nextIndex =0;
        }
        updateIndicator(nextIndex);
        mViewPager.setCurrentItem(nextIndex, mIsAnimation);


    }

    private void sendScrollMessage(long delayTimeInMills) {
        mHandler.removeMessages(SCROLL_WHAT);
        mHandler.sendEmptyMessageDelayed(SCROLL_WHAT, delayTimeInMills);
    }
    
    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int position) {
        updateIndicator(position);
    }


    @Override
    public void onPageScrollStateChanged(int i) {

    }

    private int dip2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }
}
