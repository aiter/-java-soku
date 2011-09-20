package com.youku.search.sort.json;

import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.search.index.entity.Folder;
import com.youku.search.sort.entity.CategoryCountBean;
import com.youku.search.sort.json.util.JSONUtil;
import com.youku.search.sort.search.AbstractSearchTemplate.SearchResult;
import com.youku.search.sort.search.impl.AdvanceFolderSearch.AdvanceFolderSearchResult;
import com.youku.search.sort.search.impl.FolderSearch.FolderSearchResult;
import com.youku.search.sort.util.DuplicateUtil;
import com.youku.search.util.StringUtil;

public class FolderConverter extends AbstractConverter {

    static Log logger = LogFactory.getLog(FolderConverter.class);

    public static JSONObject convert(Folder folder) {

        if (folder == null) {
            return null;
        }

        try {
            JSONObject object = new JSONObject();
            JSONObject videosObject = new JSONObject();

            object.put("pk_folder", folder.pk_folder);
            object.put("title", StringUtil.filterNull(folder.title));
            object.put("owner", folder.owner);
            object.put("owner_name", StringUtil.filterNull(folder.owner_name));
            object.put("cate_ids", folder.cate_ids);
            object.put("update_time", formatDate(folder.update_time));
            object.put("video_count", folder.video_count);
            object.put("total_pv", folder.total_pv);
            object.put("logo", StringUtil.filterNull(folder.logo));
            object.put("total_seconds", folder.total_seconds);
            object.put("total_comment", folder.total_comment);
            object.put("tags", StringUtil.filterNull(folder.tags));

            object.put("md5", StringUtil.filterNull(DuplicateUtil
                    .getMd5(folder)));

            // videosObject
            object.put("videos", videosObject);

            int index = 0;
            int subVideoNum = 0;
            if (folder.video_title != null) {
                subVideoNum = folder.video_title.length;
            }

            for (int k = 0; k < subVideoNum; k++) {

                if (folder.video_title[k] == null) {
                    continue;
                }

                JSONObject videoObject = new JSONObject();

                String title = folder.video_title[k];

                String seconds = null;
                if (folder.video_seconds != null
                        && k < folder.video_seconds.length) {
                    seconds = folder.video_seconds[k];
                }

                String order_no = null;
                if (folder.video_order_no != null
                        && k < folder.video_order_no.length) {
                    order_no = folder.video_order_no[k];
                }

                String md5 = null;
                if (folder.video_md5 != null && k < folder.video_md5.length) {
                    md5 = folder.video_md5[k];
                }

                videoObject.put("title", StringUtil.filterNull(title));
                videoObject.put("seconds", StringUtil.filterNull(seconds));
                videoObject.put("order_no", StringUtil.filterNull(order_no));
                videoObject.put("md5", StringUtil.filterNull(md5));

                videosObject.put(index + "", videoObject);
                index++;

            }

            return object;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public static JSONObject convert(FolderSearchResult searchResult) {

        if (searchResult == null) {
            return null;
        }

        List<Folder> page = searchResult.page;
        List<CategoryCountBean> categoryCount = searchResult.categoryCount;
        String suggestion = searchResult.suggestionWord;
        JSONObject relevant = searchResult.relevantWord;
        if (relevant == null) {
            relevant = new JSONObject();
        }

        int total = searchResult.total;

        try {
            // items
            JSONObject itemsObject = convert(page);
            itemsObject = JSONUtil.newIfNull(itemsObject);

            // categories
            JSONObject categoriesObject = FolderCategoryCountBeanConverter
                    .convert(categoryCount);
            categoriesObject = JSONUtil.newIfNull(categoriesObject);

            // ok
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("total", total);
            jsonObject.put("items", itemsObject);
            jsonObject.put("categories", categoriesObject);
            jsonObject.put(SUGGESTION, StringUtil.filterNull(suggestion));
            jsonObject.put(RELEVANT_WORDS, relevant);

            return jsonObject;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public static JSONObject convert(List<Folder> list) {

        if (list == null) {
            return null;
        }

        try {
            JSONObject jsonObject = new JSONObject();

            int folderIndex = 0;
            for (ListIterator<Folder> i = list.listIterator(); i.hasNext();) {

                JSONObject object = convert(i.next());
                if (object != null) {
                    jsonObject.put(folderIndex + "", object);
                    folderIndex++;
                }
            }

            return jsonObject;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public static JSONObject convert(AdvanceFolderSearchResult searchResult) {

        if (searchResult == null) {
            return null;
        }

        List<Folder> list = searchResult.page;
        List<CategoryCountBean> categoryCount = searchResult.categoryCount;

        int total = searchResult.total;

        try {
            // items
            JSONObject itemsObject = convert(list);
            itemsObject = JSONUtil.newIfNull(itemsObject);

            // categoriesObject
            JSONObject categoriesObject = FolderCategoryCountBeanConverter
                    .convert(categoryCount);
            categoriesObject = JSONUtil.newIfNull(categoriesObject);

            // ok
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("total", total);
            jsonObject.put("items", itemsObject);
            jsonObject.put("categories", categoriesObject);

            return jsonObject;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public static JSONObject convert(SearchResult<Folder> searchResult) {

        if (searchResult == null) {
            return null;
        }

        try {
            // items
            JSONObject itemsObject = convert(searchResult.page);
            itemsObject = JSONUtil.newIfNull(itemsObject);

            // ok
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("total", searchResult.total);
            jsonObject.put("items", itemsObject);

            return jsonObject;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }
}
