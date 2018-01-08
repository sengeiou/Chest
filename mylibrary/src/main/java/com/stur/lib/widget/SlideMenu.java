package com.stur.lib.widget;


import com.stur.lib.R;

import android.content.Context;
import android.graphics.Canvas;
import android.text.style.LineHeightSpan.WithDensity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Scroller;

public class SlideMenu extends ViewGroup {
    public static final int SCREEN_MENU = 0;
    public static final int SCREEN_MAIN = 1;
    private static final int SCREEN_INVALID = -1;

    private int mCurrentScreen;
    private int mNextScreen = SCREEN_INVALID;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mTouchSlop;

    private float mLastMotionX;
    private float mLastMotionY;

    private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_SCROLLING = 1;
    private static final int SNAP_VELOCITY = 1000;

    public int mTouchState = TOUCH_STATE_REST;
    private boolean mLocked;
    private boolean mAllowLongPress;

    private View vMaskShade;

    public SlideMenu(Context context) {
        this(context, null, 0);
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mScroller = new Scroller(getContext());
        mCurrentScreen = SCREEN_MAIN;
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        vMaskShade=findViewById(R.id.view_mask_slider);
        WindowManager wm = (WindowManager) getContext()

                .getSystemService(Context.WINDOW_SERVICE);
        nwidth= wm.getDefaultDisplay().getWidth();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureViews(widthMeasureSpec, heightMeasureSpec);
    }

    public void measureViews(int widthMeasureSpec, int heightMeasureSpec) {
        View menuView = getChildAt(0);
        menuView.measure(menuView.getLayoutParams().width + menuView.getLeft()
                + menuView.getRight(), heightMeasureSpec);

        View contentView = getChildAt(1);
        contentView.measure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        if (childCount != 2) {
            throw new IllegalStateException(
                    "The childCount of SlidingMenu must be 2");
        }

        View menuView = getChildAt(0);
        final int width = menuView.getMeasuredWidth();
        menuView.layout(-width, 0, 0, menuView.getMeasuredHeight());


        View contentView = getChildAt(1);
        contentView.layout(0, 0, contentView.getMeasuredWidth(),
                contentView.getMeasuredHeight());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View child;
        for (int i = 0; i < getChildCount(); i++) {
            child = getChildAt(i);
            child.setFocusable(true);
            child.setClickable(true);
            //LogUtil.e("slideMenu", "finishInflate");
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (mLocked) {
            return true;
        }

        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE)
                && (mTouchState != TOUCH_STATE_REST)) {
            return true;
        }

        final float x = ev.getX();
        final float y = ev.getY();

        switch (action) {
        case MotionEvent.ACTION_MOVE:

            final int xDiff = (int) Math.abs(x - mLastMotionX);
            final int yDiff = (int) Math.abs(y - mLastMotionY);

            final int touchSlop = mTouchSlop;
            boolean xMoved = xDiff > touchSlop;
            boolean yMoved = yDiff > touchSlop;

            if (xMoved || yMoved) {

                if (xMoved) {
                    // Scroll if the user moved far enough along the X axis
                    mTouchState = TOUCH_STATE_SCROLLING;
                    enableChildrenCache();
                }
                // Either way, cancel any pending longpress
                if (mAllowLongPress) {
                    mAllowLongPress = false;
                    // Try canceling the long press. It could also have been
                    // scheduled
                    // by a distant descendant, so use the mAllowLongPress flag
                    // to block
                    // everything
                    final View currentScreen = getChildAt(mCurrentScreen);
                    currentScreen.cancelLongPress();
                }
            }
            break;

        case MotionEvent.ACTION_DOWN:
            // Remember location of down touch
            mLastMotionX = x;
            mLastMotionY = y;
            mAllowLongPress = true;

            mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
                    : TOUCH_STATE_SCROLLING;

            break;

        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            // Release the drag
            clearChildrenCache();
            mTouchState = TOUCH_STATE_REST;
            mAllowLongPress = false;
            break;
        }

        /*
        * The only time we want to intercept motion events is if we are in the
        * drag mode.
        */
        return mTouchState != TOUCH_STATE_REST;
    }

    void enableChildrenCache() {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View layout = (View) getChildAt(i);
            layout.setDrawingCacheEnabled(true);
        }
    }

    void clearChildrenCache() {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View layout = (View) getChildAt(i);
            layout.setDrawingCacheEnabled(false);
        }
    }
    private int nwidth;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (mLocked) {
            return true;
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        final int action = ev.getAction();
        final float x = ev.getX();

        int[] location = new int[2];
        getChildAt(1).getLocationOnScreen(location);
        int ww = location[0];
        //LogUtil.i("slideMenu", "the deltaX is"+ww);





        //LogUtil.i("slideMenu", "the deltaX is"+ww+", the width is: "+width);

        getChildAt(1).findViewById(R.id.view_mask_slider).setAlpha(18.0f*ww*ww/(25.0f*nwidth*nwidth));
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            /*
            * If being flinged and user touches, stop the fling. isFinished
            * will be false if being flinged.
            */
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }

            // Remember where the motion event started
            mLastMotionX = x;

            break;
        case MotionEvent.ACTION_MOVE:
            if (mTouchState == TOUCH_STATE_SCROLLING) {
                // Scroll to follow the motion event
                final int deltaX = (int) (mLastMotionX - x);
                mLastMotionX = x;

                if (deltaX < 0) {
                    if (deltaX + getScrollX() >= -getChildAt(0).getWidth()) {
                        scrollBy(deltaX, 0);
                        //LogUtil.i("slideMenu", "the deltaX is"+deltaX);
                        //getChildAt(1).findViewById(R.id.view_mask_slider).setAlpha(-0.025f*deltaX);
                    }

                } else if (deltaX > 0) {
                    final int availableToScroll = getChildAt(
                            getChildCount() - 1).getRight()
                            - getScrollX() - getWidth();

                    if (availableToScroll > 0) {
                        int scrolldistance=Math.min(availableToScroll, deltaX);
                        scrollBy(scrolldistance, 0);
                        //LogUtil.i("slideMenu", "availableToScroll"+availableToScroll+" ,and the scrollBy is "+scrolldistance);
                        //getChildAt(1).findViewById(R.id.view_mask_slider).setAlpha(0.025f*scrolldistance);
                    }
                }
            }
            break;
        case MotionEvent.ACTION_UP:

            if (mTouchState == TOUCH_STATE_SCROLLING) {

                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int velocityX = (int) velocityTracker.getXVelocity();
                //LogUtil.e("SlideMenu:", "onTouchEvent action:ACTION_UP1 "+velocityX);
                if (velocityX > SNAP_VELOCITY && mCurrentScreen == SCREEN_MAIN) {
                    // Fling hard enough to move left
                    snapToScreen(SCREEN_MENU);
                } else if (velocityX < -SNAP_VELOCITY
                        && mCurrentScreen == SCREEN_MENU) {
                    // Fling hard enough to move right
                    snapToScreen(SCREEN_MAIN);

                } else {
                    //LogUtil.e("SlideMenu:", "onTouchEvent action:ACTION_UP2 ");
                    snapToDestination();

                }

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
            }

            mTouchState = TOUCH_STATE_REST;

            break;
        case MotionEvent.ACTION_CANCEL:
            mTouchState = TOUCH_STATE_REST;
        }

        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
        } else if (mNextScreen != SCREEN_INVALID) {
            mCurrentScreen = Math.max(0,
                    Math.min(mNextScreen, getChildCount() - 1));
            mNextScreen = SCREEN_INVALID;
            clearChildrenCache();
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        postInvalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        final int scrollX = getScrollX();
        super.dispatchDraw(canvas);
        canvas.translate(scrollX, 0);
    }

    @Override
    public boolean dispatchUnhandledMove(View focused, int direction) {
        if (direction == View.FOCUS_LEFT) {
            if (getCurrentScreen() > 0) {
                snapToScreen(getCurrentScreen() - 1);
                return true;
            }
        } else if (direction == View.FOCUS_RIGHT) {
            if (getCurrentScreen() < getChildCount() - 1) {
                snapToScreen(getCurrentScreen() + 1);
                return true;
            }
        }
        return super.dispatchUnhandledMove(focused, direction);
    }

    protected void snapToScreen(int whichScreen) {
        //LogUtil.e("SlideMenu:", "snapToScreen1: "+whichScreen);
        enableChildrenCache();

        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));

        //LogUtil.e("SlideMenu:", "snapToScreen2: "+whichScreen);
        if(0==whichScreen){
            //getChildAt(1).setAlpha(0.3f);
            //getChildAt(1).findViewById(R.id.view_mask_slider).setVisibility(View.VISIBLE);
            getChildAt(1).findViewById(R.id.view_mask_slider).setAlpha(0.5f);
            /*if(vMaskShade!=null)

            vMaskShade.setVisibility(View.GONE);*/
        }else
        if(1==whichScreen)
        {
            //getChildAt(1).findViewById(R.id.view_mask_slider).setVisibility(View.GONE);
            getChildAt(1).findViewById(R.id.view_mask_slider).setAlpha(0.0f);
            //getChildAt(1).setAlpha(1.0f);
            /*if(vMaskShade!=null)
            vMaskShade.setVisibility(View.VISIBLE);*/
        }


        boolean changingScreens = whichScreen != mCurrentScreen;

        mNextScreen = whichScreen;

        View focusedChild = getFocusedChild();
        if (focusedChild != null && changingScreens
                && focusedChild == getChildAt(mCurrentScreen)) {
            focusedChild.clearFocus();
        }




        final int newX = (whichScreen - 1) * getChildAt(0).getWidth();
        final int delta = newX - getScrollX();
        mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
        invalidate();
    }



    protected void snapToDestination() {

        /*if (getScrollX() == 0) {
            getChildAt(1).setAlpha(1.0f);
            return;
        }*/
        final int screenWidth = getChildAt(0).getWidth();
        final int whichScreen = (screenWidth + getScrollX() + (screenWidth / 2))
                / screenWidth;

        snapToScreen(whichScreen);

    }

    public int getCurrentScreen() {
        return mCurrentScreen;
    }

    public boolean isMainScreenShowing() {
        return mCurrentScreen == SCREEN_MAIN;
    }

    public void openMenu() {
        mCurrentScreen = SCREEN_MENU;
        snapToScreen(mCurrentScreen);
    }

    public void closeMenu() {
        mCurrentScreen = SCREEN_MAIN;
        snapToScreen(mCurrentScreen);
    }

    public void unlock() {
        mLocked = false;
    }

    public void lock() {
        mLocked = true;
    }

}
