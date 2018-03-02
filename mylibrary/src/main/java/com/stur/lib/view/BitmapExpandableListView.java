package com.stur.lib.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * Created by Administrator on 2016/3/7.
 */
public class BitmapExpandableListView extends ExpandableListView {
    private int lastClickedGroupPosition = -1;

    public BitmapExpandableListView(Context context) {
        super(context);
    }

    public BitmapExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BitmapExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public BitmapExpandableListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public abstract class ElvGroupClickListener implements OnGroupClickListener {
        protected int lastGroupIndex = -1;
    }

    public void updateLastClieckedPosition(int lastClickedGroupPosition) {
        if (this.lastClickedGroupPosition != lastClickedGroupPosition) {
            if (this.lastClickedGroupPosition >= 0) {
                // TODO: 2016/3/20 collapse other group
            }
            this.lastClickedGroupPosition = lastClickedGroupPosition;
        }
    }
}
