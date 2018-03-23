package com.stur.lib.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class FragmentInterface {
    protected FragmentInterfaceWrapper mFragment;
    
    public Activity getActivity() {
        return getFragment().getActivity();
    }
    
    public Context getContext() {
        return getFragment().getContext();
    }
    
    public FragmentInterfaceWrapper getFragment() {
        return mFragment;
    }
    
    public FragmentInterface(FragmentInterfaceWrapper fragment) {
        mFragment = fragment;
    }
    
    public String getString(int res) {
        return getActivity().getString(res);
    }    
    public String getString(int res, Object... formatArgs) {
        return getActivity().getString(res, formatArgs);
    }
    
    public Resources getResources() {
        return getActivity().getResources();
    }
    
    abstract public void onConfigurationChanged(Configuration newConfig);

    abstract public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
    
    abstract public void onCreate(Bundle savedInstanceState);

    abstract public void onCreateOptionsMenu(Menu menu, MenuInflater inflater);

    abstract public void onDetach();
    abstract public void onAttach(Activity activity);
    abstract public void onResume();
}
