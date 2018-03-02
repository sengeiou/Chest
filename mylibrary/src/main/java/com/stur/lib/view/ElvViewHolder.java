package com.stur.lib.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stur.lib.R;
import com.stur.lib.web.GImageLoader;


public abstract class ElvViewHolder<T> {
    protected View contentView;
    public ImageView ivBackground;
    TextView tvGroupName;
    TextView tvListItemName;

    public T groupItem;
    public T childItem;

    public ElvViewHolder(View v) {
        contentView = v;
        this.ivBackground = (ImageView) contentView.findViewById(R.id.iv_group_background);
        this.tvGroupName = (TextView) contentView.findViewById(R.id.tv_group_name);
        this.tvListItemName = (TextView) contentView.findViewById(R.id.tv_list_item_name);
    }

    public View getView() {
        return contentView;
    }

    protected void setIvBackground(String imagUrl) {
        GImageLoader.getInstance().displayImage(imagUrl, this.ivBackground);
    }

    protected void setGroupName(String groupName){
        tvGroupName.setText(groupName);
    }

    protected void setTvListItemName(String listItemName) {
        tvListItemName.setText(listItemName);
    }

    protected void setChildOnClickedListener(View.OnClickListener listener) {
        this.tvListItemName.setOnClickListener(listener);
    }
}
