package com.youku.search.sort.util;

import java.util.List;

import com.youku.search.index.entity.Folder;
import com.youku.search.sort.entity.CategoryMap;
import com.youku.search.sort.entity.CategoryMap.Category;

public class FolderCategoryCount extends AbstractCategoryCount {

    private static final FolderCategoryCount instance = new FolderCategoryCount();

    private FolderCategoryCount() {
    }

    public static FolderCategoryCount getInstance() {
        return instance;
    }

    @Override
    protected int getCateId(Object record) {
        return ((Folder) record).cate_ids;
    }

    @Override
    protected List<Category> getCategoryInfo() {
        return CategoryMap.getInstance().folderList;
    }
}
