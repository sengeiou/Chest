package com.stur.lib.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.stur.lib.R;

import java.util.HashMap;

/**
 * SeparatedListAdapter是指一级的ListView，即包含了section和相关的ListItem为一个Item
 * 泛型参数Adapter保存了其二级ListView，即一级Item中的一个section对应的多个Item
 * ListView相关的数据保存在这里
 * @param <T>
 */
public class SeparatedListAdapter<T extends Adapter> extends BaseAdapter {
    public void clear() {
        sections.clear();
        headers.clear();
        notifyDataSetChanged();
    }
    
    protected int getListHeaderResource() {
        return R.layout.list_header;
    }

    //ListView中section与adapter的映射关系保存在这里
    private final HashMap<String, T> sections = new HashMap<String, T>();
    //ListView中Item的section布局，即headers，每次新增section时会向headers中加内容
    private final ArrayAdapter<String> headers;
    private final static int TYPE_SECTION_HEADER = 0;

    public SeparatedListAdapter(Context context) {
        headers = new ArrayAdapter<String>(context, getListHeaderResource(), android.R.id.text1);
    }

    public void addSection(String section, T adapter) {
        this.headers.add(section);
        this.sections.put(section, adapter);
        notifyDataSetChanged();
    }

    public void addSection(int index, String section, T adapter) {
        this.headers.insert(section, index);
        this.sections.put(section, adapter);
        notifyDataSetChanged();
    }
    
    public void removeSection(String section) {
        this.headers.remove(section);
        this.sections.remove(section);
        notifyDataSetChanged();
    }
    
    public T getSection(String section) {
        return sections.get(section);
    }
    
    public Iterable<T> getSections() {
        return sections.values();
    }

    @Override
    public Object getItem(int position) {
        for (int i = 0; i < headers.getCount(); i++) {
            String section = headers.getItem(i);
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            // check if position inside this section
            if (position == 0) {
                return section;
            }
            if (position < size) {
                return adapter.getItem(position - 1);
            }

            // otherwise jump into next section
            position -= size;
        }
        return null;
    }

    public int getCount() {
        // total together all sections, plus one for each section header
        int total = 0;
        for (Adapter adapter : this.sections.values())
            total += adapter.getCount() + 1;
        return total;
    }

    public int getSectionCount() {
        return headers.getCount();
    }
    
    @Override
    public int getViewTypeCount() {
        // assume that headers count as one, and that there will be at least a itemViewType.
        // then total all sections
        int total = 2;
        for (Adapter adapter : this.sections.values()) {
            total += adapter.getViewTypeCount();
        }
        return total;
    }

    @Override
    public int getItemViewType(int position) {
        int type = 1;
        for (int i = 0; i < headers.getCount(); i++) {
            String section = headers.getItem(i);
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            // check if position inside this section
            if (position == 0)
                return TYPE_SECTION_HEADER;
            if (position < size)
                return type + adapter.getItemViewType(position - 1);

            // otherwise jump into next section
            position -= size;
            type += adapter.getViewTypeCount();
        }
        return -1;
    }

    @Override
    public boolean isEnabled(int position) {
        return (getItemViewType(position) != TYPE_SECTION_HEADER);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int sectionnum = 0;
        for (int i = 0; i < headers.getCount(); i++) {
            String section = headers.getItem(i);
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            // check if position inside this section
            if (position == 0){
                return headers.getView(sectionnum, convertView, parent);
            }
            if (position < size) {
                return adapter.getView(position - 1, convertView, parent);
            }

            // otherwise jump into next section
            position -= size;
            sectionnum++;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
