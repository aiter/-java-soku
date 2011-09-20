package com.youku.search.sort.util;

import java.util.List;
import java.util.Map;

import com.youku.search.sort.entity.CategoryMap;
import com.youku.search.sort.entity.CategoryMap.Category;

public class FolderMapVideo {

    public static int getFolderCateIdByVideo(int video_cate_id) {

        Map<Integer, Category> cateMap = CategoryMap.getInstance().map;
        List<Category> folderList = CategoryMap.getInstance().folderList;

        if (!cateMap.containsKey(video_cate_id)) {// 无效的id
            return 0;
        }

        String video_cate_name = cateMap.get(video_cate_id).name;
        if (video_cate_name == null) {
            return 0;
        }

        for (Category folderCategory : folderList) {
            if (video_cate_name.equals(folderCategory.name)) {
                return folderCategory.id;
            }
        }

        return 0;
    }
}
