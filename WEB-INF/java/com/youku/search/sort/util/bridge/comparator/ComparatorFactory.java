package com.youku.search.sort.util.bridge.comparator;

import java.util.Comparator;

import com.youku.search.sort.util.bridge.SearchUtil;

public class ComparatorFactory {

	/**
	 * @param type
	 *            web前端提交的查询类型
	 * @param orderField
	 *            后台lucene的排序方式
	 * @param reverse
	 *            是否反序
	 * @return 返回一个适当的Comparator
	 */
	public static <T> Comparator<T> getComparator(int type,
			int luceneOrderField, boolean reverse) {

		return SearchUtil.getComparator(type, luceneOrderField, reverse);
	}
}
