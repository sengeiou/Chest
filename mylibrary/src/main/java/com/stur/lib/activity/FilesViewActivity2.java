/******************************************************************************
 * Copyright (C), 2018-2028, Sturmegezhutz private property right
 * PROPRIETARY RIGHTS of Sturmegezhutz are involved in the
 * subject matter of this material.  All manufacturing, reproduction, use,
 * and sales rights pertaining to this subject matter are governed by the
 * license agreement.  The recipient of this software implicitly accepts
 * the terms of the license.

 * File name: FilesViewActivity2.java
 * Description: 使用 BaseAdapter 构建文件管理器
 * Others:

 * Author:      Sturmegezhutz
 * Version:     V1.00.01
 * Date:        2018.09.18

 * Function List:
 1. ...

 * History:
 1. Author:    Sturmegezhutz
    Date:         2018.09.18
    Modification: Create file

 *******************************************************************************/
package com.stur.lib.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.stur.lib.Log;
import com.stur.lib.R;
import com.stur.lib.file.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilesViewActivity2 extends Activity {
    private FileAdapter mAdapter = new FileAdapter();
    //这里简单地以 FileItemDto 示例，实际应用中会封装自己复杂的Module结构
    private List<FileItemDto> mListItem = new ArrayList<FileItemDto>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_view);
        Toast.makeText(getApplicationContext(), "选择要发送的文件", Toast.LENGTH_SHORT).show();
        ListView fileView = (ListView)findViewById(R.id.lv_fileView);
        readDirectory(Environment.getExternalStorageDirectory().getPath());
        fileView.setAdapter(mAdapter);  //简单理解为VC绑在一起
        // item点击后的处理事件
        fileView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // position为item的位置，可以通过position访问mListItem来获取数据
                String path = mListItem.get(position).filePath;
                if (mListItem.get(position).isDirectory) {
                    readDirectory(path);  //点击后再次读取目录并更新 mListItem
                } else {
                    //打开文件还有问题，后面再调试
                    FileUtils.openFile(FilesViewActivity2.this, path);
                }

                mAdapter.notifyDataSetChanged();
            }
        }) ;

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
                FileItemDto dto = new FileItemDto(file[i].getName(), file[i].getAbsolutePath(),
                        file[i].isDirectory() ? R.drawable.folder : R.drawable.file,
                        file[i].isDirectory());
                mListItem.add(dto);
            }
            //判断有无父目录，增加返回上一级目录菜单
            if (selectedFile.getParent() != null){
                // 这里直接将mListItem[0]当成一个文件，且path赋值为上一级，即可实现返回上一级功能
                mListItem.add(0, new FileItemDto("返回上一级目录", selectedFile.getParent(), 0, false));
            }
        }else{
            Toast.makeText(getApplicationContext(), "该目录不能读取", Toast.LENGTH_SHORT).show();
        }
    }

    private class FileAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mListItem.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View itemView = null;
            //布局不变，数据变
            //如果缓存为空，我们生成新的布局作为1个item
            if(convertView == null){
                Log.i(this, "没有缓存，重新生成 "+position);
                LayoutInflater inflater = FilesViewActivity2.this.getLayoutInflater();
                //因为getView()返回的对象，adapter会自动赋给ListView
                itemView = inflater.inflate(R.layout.list_item_view, null);
            }else{
                Log.i("info:", "有缓存，不需要重新生成"+position);
                itemView = convertView;
            }
            ImageView imageView = (ImageView)itemView.findViewById(R.id.iv_list_item_image);
            TextView nameText   = (TextView)itemView.findViewById(R.id.tv_list_item_file_name);
            imageView.setImageResource(mListItem.get(position).imgSourceId);
            nameText.setText(mListItem.get(position).fileName);
            return itemView;
        }
    }

    private class FileItemDto {
        public FileItemDto(String name, String path, int imgId, boolean dir) {
            fileName = name;
            filePath = path;
            imgSourceId = imgId;
            isDirectory = dir;
        }

        public String fileName = "";
        public String filePath = "";
        public int imgSourceId = 0;
        public boolean isDirectory = false;
    }
}
