package com.stur.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.stur.chest.R;
import com.stur.lib.activity.BetterListActivity;
import com.stur.lib.view.ListItem;


public class BetterListDemoActivity extends BetterListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState, View view) {
        super.onCreate(savedInstanceState, view);
        
        initView();
    }

    private void initView() {
        addItem("Content", new ListItem(getFragment(), "List Content", "i am summary", R.drawable.ic_launcher) {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                startActivity(new Intent(BetterListDemoActivity.this, ListContentDemoActivity.class));
            }
        });
        //这里只要section名称相同，会自动添加同一个section下
        addItem("Content", new ListItem(getFragment(), "List Content2", null, R.drawable.ic_launcher));

        /*addItem("Theme", new ListItem(getFragment(), "Dark Theme", null, R.drawable.ic_launcher) {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                //之前显示的内容是在fragment中，所以即使拉起一个空的Activity显示的内容却是不变的
                startActivity(new Intent(MainActivity.this, MainActivityDark.class));
            }
        });*/

        addItem("Theme", new ListItem(getFragment(), "Content", "Dark Theme", R.drawable.ic_launcher) {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                startActivity(new Intent(BetterListDemoActivity.this, ListContentDemoActivity.class));
            }
        });
        addItem("Devices", new ListItem(getFragment(), "Nexus S", null, R.drawable.ic_launcher)).setCheckboxVisible(true).setChecked(true);
        addItem("Devices", new ListItem(getFragment(), "Nexus One", null, R.drawable.ic_launcher)).setCheckboxVisible(true);
        addItem("Devices", new ListItem(getFragment(), "Nexus 4", null, R.drawable.ic_launcher)).setCheckboxVisible(true);
        addItem("Devices", new ListItem(getFragment(), "Nexus 7", null, R.drawable.ic_launcher)).setCheckboxVisible(true).setChecked(true);;
        addItem("Devices", new ListItem(getFragment(), "Nexus 10", null, R.drawable.ic_launcher)).setCheckboxVisible(true);
    }
}
