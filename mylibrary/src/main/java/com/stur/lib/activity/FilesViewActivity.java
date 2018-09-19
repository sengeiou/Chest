/******************************************************************************
 * Copyright (C), 2018-2028, Sturmegezhutz private property right
 * PROPRIETARY RIGHTS of Sturmegezhutz are involved in the
 * subject matter of this material.  All manufacturing, reproduction, use,
 * and sales rights pertaining to this subject matter are governed by the
 * license agreement.  The recipient of this software implicitly accepts
 * the terms of the license.

 * File name:   FilesViewActivity2.java
 * Description:
 * Others:

 * Author:      Sturmegezhutz
 * Version:     V1.00.01
 * Date:        2018.09.18

 * Function List:
 1. ...

 * History:
 1. Author:     Sturmegezhutz
 Date:          2018.09.18
 Modification:  Create file

 *******************************************************************************/
package com.stur.lib.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.stur.lib.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class FilesViewActivity extends Activity {
    private SimpleAdapter adapter;
    ArrayList<HashMap<String, Object>> mListItem;  //SimpleAdapter适配器使用的数据结构是HashMap,
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_view);

        Toast.makeText(getApplicationContext(), "选择要发送的文件", Toast.LENGTH_SHORT).show();
        ListView fileView = (ListView)findViewById(R.id.lv_fileView);
        mListItem = new ArrayList<HashMap<String, Object>>();
        //ImageView image = (ImageView)findViewById(R.id.image);
        //image.setImageResource();
        readDirectory(Environment.getExternalStorageDirectory().getPath());
        adapter = new SimpleAdapter(
                getApplicationContext(),
                mListItem,  //listItem 不仅仅是数据，而是一个与界面耦合的数据混合体
                R.layout.list_item_view,
                new String[] {"image", "name", "path", "type", "parent"},  //from 从哪里来
                new int[]{R.id.iv_list_item_image, R.id.tv_list_item_file_name,
                        R.id.tv_list_item_file_path, R.id.tv_list_item_file_type, R.id.tv_list_item_file_parent}  //to 到那里去
        );
        fileView.setAdapter(adapter);
        fileView.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView isDirectory = (TextView)view.findViewById(R.id.tv_list_item_file_type);
                TextView path = (TextView)view.findViewById(R.id.tv_list_item_file_parent);
                TextView name = (TextView)view.findViewById(R.id.tv_list_item_file_name);

                if (Boolean.parseBoolean(isDirectory.getText().toString())){
                    readDirectory(path.getText().toString());
                    adapter.notifyDataSetChanged();
                }else{
                    Intent intent = new Intent();
                    intent.putExtra("FileName", name.getText().toString());
                    intent.putExtra("FilePath", path.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    /**
     * 读取目录并填充 mListItem
     * @param selectedPath
     */
    private void readDirectory(String selectedPath){
        File selectedFile = new File(selectedPath);
        if (selectedFile.canRead()){
            File[] file = selectedFile.listFiles();
            mListItem.clear();
            for (int i = 0; i < file.length; i++){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("image", file[i].isDirectory()?R.drawable.folder:R.drawable.file);
                map.put("name", file[i].getName());
                map.put("path", file[i].getPath());
                map.put("type", file[i].isDirectory());
                map.put("parent", file[i].getParent());

                mListItem.add(map);
            }
            //判断有无父目录，增加返回上一级目录菜单
            if (selectedFile.getParent() != null){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("name", "返回上一级目录");
                map.put("path", selectedFile.getParent());
                map.put("type", true);
                map.put("parent", selectedFile.getParent());
                mListItem.add(0, map);
            }
        }else{

            Toast.makeText(getApplicationContext(), "该目录不能读取", Toast.LENGTH_SHORT).show();
        }
    }

}
