package com.stur.lib.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.stur.lib.Log;

/**
 * 侧滑Layout
 */
public class SwipeListLayout extends FrameLayout {

	private View mHiddenView;  //被隐藏的置顶/删除按钮
	private View mItemView;  // List视图
	private int hiddenViewWidth;
	private ViewDragHelper mDragHelper;
	private int hiddenViewHeight;
	private int itemWidth;
	private int itemHeight;
	private OnSwipeStatusListener mListener;
	private Status status = Status.Close;
	private boolean smooth = true;

	// 状态
	public enum Status {
		Open, Close
	}

	/**
	 * 设置侧滑状态
	 * 
	 * @param status
	 *            状态 Open or Close
	 * @param smooth
	 *            若为true则有过渡动画，否则没有
	 */
	public void setStatus(Status status, boolean smooth) {
		Log.d(this, "setStatus: status = " + status + ", smooth = " + smooth);
		this.status = status;
		if (status == Status.Open) {
			open(smooth);
		} else {
			close(smooth);
		}
	}

	public void setOnSwipeStatusListener(OnSwipeStatusListener listener) {
		this.mListener = listener;
	}

	/**
	 * 是否设置过渡动画
	 * 
	 * @param smooth
	 */
	public void setSmooth(boolean smooth) {
		this.smooth = smooth;
	}

	public interface OnSwipeStatusListener {

		/**
		 * 当状态改变时回调
		 * 
		 * @param status
		 */
		void onStatusChanged(Status status);

		/**
		 * 开始执行Open动画
		 */
		void onStartCloseAnimation();

		/**
		 * 开始执行Close动画
		 */
		void onStartOpenAnimation();

	}

	public SwipeListLayout(Context context) {
		this(context, null);
	}

	public SwipeListLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDragHelper = ViewDragHelper.create(this, callback);
	}

	// ViewDragHelper的回调
	Callback callback = new Callback() {

		@Override
		public boolean tryCaptureView(View view, int arg1) {
			return view == mItemView;
		}

		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if (child == mItemView) {
				if (left > 0) {
					return 0;
				} else {
					left = Math.max(left, -hiddenViewWidth);
					return left;
				}
			}
			return 0;
		}

		@Override
		public int getViewHorizontalDragRange(View child) {
			return hiddenViewWidth;
		}

		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			if (mItemView == changedView) {
				mHiddenView.offsetLeftAndRight(dx);
			}
			// 有时候滑动很快的话 会出现隐藏按钮的linearlayout没有绘制的问题
			// 为了确保绘制成功 调用 invalidate
			invalidate();
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			Log.d(this, "onViewReleased: ");
			// 向右滑xvel为正 向左滑xvel为负
			if (releasedChild == mItemView) {
				if (xvel == 0
						// 如果左滑的距离超过侧滑按钮布局控件一般的宽度，则启动拉起侧滑动画
						// 如果侧滑体验不好，需要在这里调试参数
						&& Math.abs(mItemView.getLeft()) > hiddenViewWidth / 2.0f) {
					open(smooth);
				} else if (xvel < 0) {
					open(smooth);
				} else {
					close(smooth);
				}
			}
		}

	};
	private Status preStatus = Status.Close;

	/**
	 * 侧滑关闭
	 * 
	 * @param smooth
	 *            为true则有平滑的过渡动画
	 */
	private void close(boolean smooth) {
		preStatus = status;
		status = Status.Close;
		if (smooth) {
			if (mDragHelper.smoothSlideViewTo(mItemView, 0, 0)) {
				if (mListener != null) {
					Log.i(this, "start close animation");
					mListener.onStartCloseAnimation();
				}
				ViewCompat.postInvalidateOnAnimation(this);
			}
		} else {
			layout(status);
		}
		if (mListener != null && preStatus == Status.Open) {
			Log.i(this, "close");
			mListener.onStatusChanged(status);
		}
	}

	/**
	 * 
	 * @param status
	 */
	private void layout(Status status) {
		if (status == Status.Close) {
			mHiddenView.layout(itemWidth, 0, itemWidth + hiddenViewWidth,
					itemHeight);
			mItemView.layout(0, 0, itemWidth, itemHeight);
		} else {
			mHiddenView.layout(itemWidth - hiddenViewWidth, 0, itemWidth,
					itemHeight);
			mItemView.layout(-hiddenViewWidth, 0, itemWidth - hiddenViewWidth,
					itemHeight);
		}
	}

	/**
	 * 侧滑打开
	 * 
	 * @param smooth 为true则有平滑的过渡动画
	 */
	private void open(boolean smooth) {
		Log.d(this, "open smooth = " + smooth);
		preStatus = status;
		status = Status.Open;
		if (smooth) {
			if (mDragHelper.smoothSlideViewTo(mItemView, -hiddenViewWidth, 0)) {
				if (mListener != null) {
					Log.i(this, "start open animation");
					mListener.onStartOpenAnimation();
				}
				ViewCompat.postInvalidateOnAnimation(this);
			}
		} else {
			layout(status);
		}
		if (mListener != null && preStatus == Status.Close) {
			Log.i(this, "open");
			mListener.onStatusChanged(status);
		}
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		// 开始执行动画
		if (mDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	// 让ViewDragHelper来处理触摸事件
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		if (action == MotionEvent.ACTION_CANCEL) {
			mDragHelper.cancel();
			return false;
		}
		return mDragHelper.shouldInterceptTouchEvent(ev);
	}

	// 让ViewDragHelper来处理触摸事件
	public boolean onTouchEvent(MotionEvent event) {
		Log.d(this, "onTouchEvent: event = " + event);
		mDragHelper.processTouchEvent(event);
		return true;
	};

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mHiddenView = getChildAt(0); // 得到隐藏按钮的linearlayout
		mItemView = getChildAt(1); // 得到最上层的linearlayout
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// 测量子View的长和宽
		itemWidth = mItemView.getMeasuredWidth();
		itemHeight = mItemView.getMeasuredHeight();
		hiddenViewWidth = mHiddenView.getMeasuredWidth();
		hiddenViewHeight = mHiddenView.getMeasuredHeight();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		layout(Status.Close);
	}

}
