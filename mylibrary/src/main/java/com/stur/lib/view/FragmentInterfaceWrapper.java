package com.stur.lib.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

public interface FragmentInterfaceWrapper {
    public Activity getActivity();
    public Context getContext();
    public FragmentInterface getInternal();
    public void setHasOptionsMenu(boolean options);
    
    void setArguments(Bundle bundle);
    Bundle getArguments();
    View getView();
    
    int getId();
}
