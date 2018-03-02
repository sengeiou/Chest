package com.stur.chest.db;

import com.stur.chest.dto.CategoryDTO;
import com.stur.chest.dto.MainCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Administrator on 2016/3/19.
 */
public class CategoryListProxy {
    private CategoryList categories;

    public CategoryListProxy() {
        categories = new CategoryList();
    }

    public synchronized void addMainCategory(MainCategory category) {
        categories.addMainCategory(category);
    }

    public MainCategory getMainCategory(int mainCategoryId) {
        return categories.getMainCategory(mainCategoryId);
    }

    public synchronized void addSubCategory(CategoryDTO category) {
        categories.addSubCategory(category);
    }

    public CategoryDTO getSubCategory(int parentCategoryId, int subCategoryId) {
        return categories.getSubCategory(parentCategoryId, subCategoryId);
    }

    public final CategoryList getMainCategoryList() {
        return categories;
    }

    public int getMainCategoryCount(){
        return categories.size();
    }

    public void clearCategories(){
        categories.clear();
    }
    /**
     *
     */
    public class CategoryList extends ArrayList<MainCategory> {

        private int addMainCategory(MainCategory category) {
            if (!containsCategory(category.getId(), -1)) {
                add(category);

                Collections.sort(this, new Comparator<MainCategory>() {
                    @Override
                    public int compare(MainCategory lhs, MainCategory rhs) {
                        if (lhs.getId() < rhs.getId()) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                });
            }
            return size();
        }

        private int getMainCategoryIndex(int categoryId) {
            MainCategory catagory;
            for (int index = 0; index < size(); index++) {
                catagory = get(index);
                if (catagory.getId() == categoryId) {
                    return index;
                }
            }
            return -1;
        }

        private MainCategory getMainCategory(int categoryId) {
            MainCategory catagory;
            for (int index = 0; index < size(); index++) {
                catagory = get(index);
                if (catagory.getId() == categoryId) {
                    return catagory;
                }
            }
            return null;
        }

        private int addSubCategory(CategoryDTO category) {
            int parent_id = category.getParentId();
            int child_id = category.getId();
            if (!containsCategory(parent_id, child_id)) {
                MainCategory parentCategory = getMainCategory(parent_id);
                if (parentCategory != null) {
                    parentCategory.addSubCategory(category);
                    set(parent_id, parentCategory);
                    return parentCategory.getSubCategoryList().size();
                } else {
                    addMainCategory(new MainCategory(category.getId(), category.getName(), category.getImg()));
                }
            }
            return 0;
        }

        private CategoryDTO getSubCategory(int parentCategoryId, int subCategoryId) {
            MainCategory parentCategory = getMainCategory(parentCategoryId);
            if (parentCategory != null) {
                CategoryDTO catagory;
                ArrayList<CategoryDTO> subCategoryArrayList = parentCategory.getSubCategoryList();
                for (int index = 0; index < subCategoryArrayList.size(); index++) {
                    catagory = subCategoryArrayList.get(index);
                    if (catagory.getId() == subCategoryId) {
                        return catagory;
                    }
                }
            }
            return null;
        }

        private boolean containsCategory(int parentCategoryId, int subCategoryId) {
            MainCategory parent;
            int parent_index, child_index;
            for (parent_index = 0; parent_index < size(); parent_index++) {
                parent = get(parent_index);
                if (parent.getId() == parentCategoryId) {
                    if (subCategoryId < 0) {
                        return true;
                    } else {
                        ArrayList<CategoryDTO> children = parent.getSubCategoryList();
                        CategoryDTO child;
                        for (child_index = 0; child_index < children.size(); child_index++) {
                            child = children.get(child_index);
                            if (child.getId() == subCategoryId) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }
    }

}
