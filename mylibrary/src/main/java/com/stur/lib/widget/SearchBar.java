package com.stur.lib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.stur.lib.R;


/**
 * Created by Administrator on 2016/3/6.
 */
public class SearchBar extends RelativeLayout {

    public SearchBar(Context context) {
        this(context, null);
    }

    public SearchBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.view_searchbar, this);
        initView();
    }

    protected void initView() {
    }

    public void setTitle(CharSequence title) {
    }
}