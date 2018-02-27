package com.stur.lib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stur.lib.R;


/**
 * Created by Administrator on 2016/3/4.
 */
public class SimpleTitleBar extends RelativeLayout{
    private TextView tvTitle;

    public SimpleTitleBar(Context context) {
        this(context, null);
    }

    public SimpleTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.view_simple_titlebar, this);
        initView();
    }

    protected void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
    }

    public void setTitle(CharSequence title) {
        tvTitle.setText(title);
    }
}
