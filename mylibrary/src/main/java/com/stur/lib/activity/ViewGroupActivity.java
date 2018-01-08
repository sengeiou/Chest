package com.stur.lib.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.stur.lib.R;

import java.util.Arrays;
import java.util.List;

public class ViewGroupActivity extends Activity
{

    private List<String> mDatas = Arrays.asList("Android", "Java");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewgroup);

        // setContentView(R.layout.activity_main);
//        setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_main,
//                R.id.id_txt, mDatas));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
