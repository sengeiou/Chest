package com.stur.lib.view;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;

import com.stur.lib.R;
import com.stur.lib.exception.ParameterException;

import java.util.ArrayList;


public abstract class ELVAdapter<T> extends BaseExpandableListAdapter implements ExpandableListAdapter {
    protected static final int GROUP_ITEM_RESOURCE = R.layout.list_group;
    protected static final int CHILD_ITEM_RESOURCE = R.layout.list_item;

    protected LayoutInflater vi;
    protected View v;
    protected BounceInterpolator bounceInterpolator;
    private ArrayList<T> groupsData;
    private Context context;

    public ELVAdapter(Context context, Activity activity, @NonNull ArrayList<T> groups) {
        this.context = context;
        this.groupsData = groups;

        vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        bounceInterpolator = new BounceInterpolator();
    }

    public T getGroupData(int groupPosition) throws ParameterException {
        if (groupPosition < groupsData.size()) {
            return groupsData.get(groupPosition);
        } else {
            // TODO: 2016/3/20 wait to throw an exception.
            throw new ParameterException("group position is out of bound!");
        }
    }

    public abstract Object getChildData(int groupPosition, int childPosition);

    public abstract String getGroupBackgroundImgUrl(int groupPosition);

    public abstract String getGroupName(int groupPosition);

    public int getGroupCount() {
        return groupsData.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return false;
    }

    public String getGroup(int groupPosition) {
        return "group-" + groupPosition;
    }

}
