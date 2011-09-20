package com.youku.search.sort.util;

import java.util.List;

import com.youku.search.index.entity.Video;
import com.youku.search.sort.entity.CategoryMap;
import com.youku.search.sort.entity.CategoryMap.Category;

public class VideoCategoryCount extends AbstractCategoryCount {

    private static final VideoCategoryCount instance = new VideoCategoryCount();

    private VideoCategoryCount() {
    }

    public static VideoCategoryCount getInstance() {
        return instance;
    }

    @Override
    protected int getCateId(Object record) {
        return ((Video) record).cate_ids;
    }

    @Override
    protected List<Category> getCategoryInfo() {
        return CategoryMap.getInstance().videoList;
    }
}
