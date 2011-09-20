package com.youku.search.sort.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.youku.search.sort.entity.CategoryCountBean;
import com.youku.search.sort.entity.CategoryMap;
import com.youku.search.sort.entity.CategoryMap.Category;

public abstract class AbstractCategoryCount {

    public List<CategoryCountBean> count(List<?> resultList, int total) {

        List<CategoryCountBean> beanList = buildCategoryCountBeanList(resultList);

        estimateCount(beanList, total);

        return beanList;
    }

    protected List<CategoryCountBean> buildCategoryCountBeanList(
            List<?> resultList) {

        List<CategoryCountBean> beanList = new ArrayList<CategoryCountBean>();

        if (resultList == null || resultList.size() == 0) {
            return beanList;
        }

        Map<Integer, CategoryCountBean> beanMap = new HashMap<Integer, CategoryCountBean>();
        Map<Integer, Category> cateMap = CategoryMap.getInstance().map;

        for (Object record : resultList) {
            final int cate_id = getCateId(record);
            
            if (cateMap.containsKey(cate_id)) {
                if (beanMap.containsKey(cate_id)) {
                    CategoryCountBean bean = beanMap.get(cate_id);
                    bean.count++;
                } else {
                    CategoryCountBean bean = new CategoryCountBean();
                    bean.cate_id = cate_id;
                    bean.cate_name = cateMap.get(cate_id).name;
                    bean.count = 1;
                    beanMap.put(cate_id, bean);
                }
            }
        }

        // ok 开始排序
        for (Category category : getCategoryInfo()) {
            CategoryCountBean bean = beanMap.get(category.id);
            if (bean != null) {
                beanList.add(bean);
            }
        }

        return beanList;
    }

    protected abstract int getCateId(Object record);

    protected abstract List<Category> getCategoryInfo();

    protected void estimateCount(List<CategoryCountBean> beanList, int total) {

        if (beanList == null) {
            return;
        }

        int thisTotal = 0;
        for (CategoryCountBean bean : beanList) {
            thisTotal += bean.count;
        }

        if (thisTotal >= total) {
            return;
        }

        double rate = (1.0 * total) / thisTotal;

        for (CategoryCountBean bean : beanList) {
            int count = (int) (bean.count * rate);
            bean.count = count;
        }
    }

}
