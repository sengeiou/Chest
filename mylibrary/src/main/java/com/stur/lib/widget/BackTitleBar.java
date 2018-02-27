package com.stur.lib.widget;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.stur.lib.R;


/**
 * Created by Administrator on 2016/3/4.
 */
public class BackTitleBar extends FrameLayout implements View.OnClickListener {
    private ImageView ivBackBtn;
    private TextView tvTitle;
    private OnTitleBarBackListener titleBarBackListener;

    public BackTitleBar(Context context) {
        this(context, null);
    }

    public BackTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BackTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.view_back_titlebar, this);
        initView();
        setListener();
    }

    protected void initView() {
        ivBackBtn = (ImageView) findViewById(R.id.iv_back_btn);
        tvTitle = (TextView) findViewById(R.id.tv_title);
    }

    protected void setListener() {
        ivBackBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (titleBarBackListener != null) {
            titleBarBackListener.OnBack();
        }
    }
    public void setTitle(@StringRes int titleId){
        tvTitle.setText(titleId);
    }

    public void setTitle(CharSequence title) {
        tvTitle.setText(title);
    }

    public void registerBackListener(OnTitleBarBackListener titleBarBackListener) {
        this.titleBarBackListener = titleBarBackListener;
    }

    public void unRegisterBackListener() {
        titleBarBackListener = null;
    }

    public interface OnTitleBarBackListener {
        void OnBack();
    }
}
