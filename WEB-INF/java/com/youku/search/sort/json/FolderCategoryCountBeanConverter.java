package com.youku.search.sort.json;

import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.search.sort.entity.CategoryCountBean;
import com.youku.search.util.StringUtil;

public class FolderCategoryCountBeanConverter extends AbstractConverter {

    static Log logger = LogFactory
            .getLog(FolderCategoryCountBeanConverter.class);

    public static JSONObject convert(CategoryCountBean countBean) {

        if (countBean == null) {
            return null;
        }

        try {
            JSONObject object = new JSONObject();

            object.put("cate_id", countBean.cate_id);
            object.put("cate_name", StringUtil.filterNull(countBean.cate_name));
            object.put("folder_count", countBean.count);

            return object;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public static JSONObject convert(List<CategoryCountBean> list) {

        if (list == null) {
            return null;
        }

        try {
            JSONObject jsonObject = new JSONObject();

            int cateIndex = 0;
            for (ListIterator<CategoryCountBean> i = list.listIterator(); i
                    .hasNext();) {
                CategoryCountBean bean = i.next();
                JSONObject j = FolderCategoryCountBeanConverter.convert(bean);
                if (j != null) {
                    jsonObject.put(cateIndex + "", j);
                    cateIndex++;
                }
            }

            return jsonObject;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

}
