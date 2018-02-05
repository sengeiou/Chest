package com.stur.lib.bt;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TabHost;

public class StAnimationTabHost extends TabHost {

	private int mCurrentTabID = 0;
	private final long durationMillis = 400;

	public StAnimationTabHost(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public StAnimationTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setCurrentTab(int index) {

		if(index > mCurrentTabID)
		{
			TranslateAnimation translateAnimation = new TranslateAnimation
					(      // x和y轴的起始和结束位置
							Animation.RELATIVE_TO_SELF, 0f,
							Animation.RELATIVE_TO_SELF, -1.0f,
							Animation.RELATIVE_TO_SELF, 0f,
							Animation.RELATIVE_TO_SELF, 0f
					);
			translateAnimation.setDuration(durationMillis);
			getCurrentView().startAnimation(translateAnimation);
		}
		else if(index < mCurrentTabID)
		{
			TranslateAnimation translateAnimation = new TranslateAnimation
					(      //x和y轴的起始和结束位置
							Animation.RELATIVE_TO_SELF, 0f,
							Animation.RELATIVE_TO_SELF, 1.0f,
							Animation.RELATIVE_TO_SELF, 0f,
							Animation.RELATIVE_TO_SELF, 0f
					);
			translateAnimation.setDuration(durationMillis);
			getCurrentView().startAnimation(translateAnimation);
		}
		else
		{
			//when first entry, getCurrentView()==null, must notify it.
		}

		super.setCurrentTab(index);

		//View target=(View)findViewById(android.R.id.tabcontent);//android.R.id.tabhost
		if(index > mCurrentTabID)
		{
			TranslateAnimation translateAnimation = new TranslateAnimation
					(      // x和y轴的起始和结束位置
							Animation.RELATIVE_TO_PARENT, 1.0f,//RELATIVE_TO_SELF
							Animation.RELATIVE_TO_PARENT, 0f,
							Animation.RELATIVE_TO_PARENT, 0f,
							Animation.RELATIVE_TO_PARENT, 0f
					);
			translateAnimation.setDuration(durationMillis);
			getCurrentView().startAnimation(translateAnimation);
			//target.startAnimation(translateAnimation);
		}
		else if(index < mCurrentTabID)
		{
			TranslateAnimation translateAnimation = new TranslateAnimation
					(      // x和y轴的起始和结束位置
							Animation.RELATIVE_TO_PARENT, -1.0f,
							Animation.RELATIVE_TO_PARENT, 0f,
							Animation.RELATIVE_TO_PARENT, 0f,
							Animation.RELATIVE_TO_PARENT, 0f
					);
			translateAnimation.setDuration(durationMillis);
			getCurrentView().startAnimation(translateAnimation);
			//target.startAnimation(translateAnimation);
		}
		mCurrentTabID = index;
	}
}
