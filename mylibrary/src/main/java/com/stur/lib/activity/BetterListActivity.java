/*
 * Copyright (C) 2013 Koushik Dutta (@koush)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stur.lib.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.stur.lib.R;
import com.stur.lib.view.BetterListFragment;
import com.stur.lib.view.BetterListFragmentInternal;
import com.stur.lib.view.ListItem;


public class BetterListActivity extends FragmentActivity implements BetterListFragmentInternal.ActivityBaseFragmentListener {
    private Class<? extends BetterListFragment> mClazz;
    private BetterListFragment mFragment;

    public BetterListActivity(Class<? extends BetterListFragment> clazz) {
        super();
        this.mClazz = clazz;
    }
    
    public BetterListActivity() {
        super();
        this.mClazz = BetterListFragment.class;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int cv = getContentView();
        if (0 != cv) {
            setContentView(cv);
        }

        try {
            mFragment = (BetterListFragment)mClazz.getConstructors()[0].newInstance();
            mFragment.setArguments(getIntent().getExtras());
            mFragment.getInternal().setListener(this);
            getSupportFragmentManager().beginTransaction().replace(getListContainerId(), mFragment, "betterlist").commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState, View view) {
    }
    
    public BetterListFragmentInternal getFragment() {
        return mFragment.getInternal();
    }
    
    public View getView() {
        return mFragment.getView();
    }
    
    protected int getListContainerId() {
        return R.id.activity_content;
    }
    
    protected int getContentView() {
        return R.layout.activity_container;
    }

    protected ListItem addItem(int sectionName, ListItem item) {
        return getFragment().addItem(getString(sectionName), item);
    }

    protected ListItem addItem(int sectionName, ListItem item, int index) {
        return getFragment().addItem(getString(sectionName), item, index);
    }
    
    protected ListItem addItem(String sectionName, ListItem item) {
        return getFragment().addItem(sectionName, item, -1);
    }
    
    public void setEmpty(int res) {
        getFragment().setEmpty(res);
    }

    public boolean isDestroyedLegacy() {
        return mDestroyed;
    }
    
    private boolean mDestroyed = false;
    @Override
    protected void onDestroy() {
        mDestroyed = true;
        super.onDestroy();
    }
}
