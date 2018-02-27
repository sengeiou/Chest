package com.stur.lib.widget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stur.lib.R;
import com.stur.lib.app.ContextBase;
import com.stur.lib.app.IContext;
import com.stur.lib.os.BusProvider;

/**
 * Created by Administrator on 2016/3/4.
 */
public abstract class FragmentBase extends Fragment implements IContext {
    public static final String ARG_SECTION_TITLE = "title";
    protected String title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutResourceId(), container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getView() != null) {
            // 返回通过Action
            View actionBack = getView().findViewById(R.id.action_back);
            if (actionBack != null) {
                actionBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().finish();
                    }
                });
            }
        }

        BusProvider.getInstance().register(this);

        initView();
        initListener();
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getInstance().unregister(this);
    }

    /**
     * 获取上下文环境
     *
     * @return
     */
    @Override
    public <T extends ContextBase> T getAppContext() {
        return ContextBase.getInstance();
    }

    /**
     * 加载layout xml
     */
    protected abstract int getLayoutResourceId();

    /**
     * 加载UI
     */
    protected abstract void initView();

    /**
     * 监听控件
     */
    protected abstract void initListener();

    /**
     * 加载网络数据
     */
    protected abstract void initData();
}
