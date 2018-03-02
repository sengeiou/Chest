package com.stur.lib.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.stur.lib.R;


/**
 * Created by Administrator on 2016/3/23.
 */
public class BackNextTitleBar extends FrameLayout implements View.OnClickListener {
    private ImageView ivBackBtn, ivNextBtn;
    private TextView tvTitle;
    private OnTitleBarListener titleBarListener;

    public BackNextTitleBar(Context context) {
        this(context, null);
    }

    public BackNextTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BackNextTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.view_back_next_titlebar, this);
        initView();
        setListener();
    }

    protected void initView() {
        ivBackBtn = (ImageView) findViewById(R.id.iv_back_btn);
        ivNextBtn = (ImageView) findViewById(R.id.iv_next_btn);
        tvTitle = (TextView) findViewById(R.id.tv_title);
    }

    protected void setListener() {
        ivBackBtn.setOnClickListener(this);
        ivNextBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (titleBarListener != null) {
            int id = v.getId();
            if (id == R.id.iv_back_btn) {
                titleBarListener.OnBack();
            } else {
                titleBarListener.OnNext();
            }
        }
    }

    public void setTitle(@StringRes int titleId) {
        tvTitle.setText(titleId);
    }

    public void setTitle(CharSequence title) {
        tvTitle.setText(title);
    }

    public void registerIconOnClickedListener(OnTitleBarListener titleBarListener) {
        this.titleBarListener = titleBarListener;
    }

    public void unRegisterBackListener() {
        titleBarListener = null;
    }

    public interface OnTitleBarListener {
        void OnBack();

        void OnNext();
    }
}
