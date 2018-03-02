package com.stur.chest.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Administrator on 2016/3/20.
 */
public class MainCategory {
    private int id;
    private String name;
    private String imgUrl;
    private ArrayList<CategoryDTO> subCategoryList = new ArrayList<>();

    public MainCategory(int id, String name, String imgUrl) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getImgUrl() {
        return this.imgUrl;
    }

    public void addSubCategory(CategoryDTO subCategory) {
        subCategoryList.add(subCategory);
        Collections.sort(subCategoryList, new Comparator<CategoryDTO>() {
            @Override
            public int compare(CategoryDTO lhs, CategoryDTO rhs) {
                if (lhs.getId() < rhs.getId()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }

    public ArrayList<CategoryDTO> getSubCategoryList() {
        return subCategoryList;
    }
}
