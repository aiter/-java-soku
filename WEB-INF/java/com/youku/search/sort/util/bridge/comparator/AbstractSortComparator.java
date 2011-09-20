package com.youku.search.sort.util.bridge.comparator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AbstractSortComparator<T> {

	protected Log logger = LogFactory.getLog(AbstractSortComparator.class);

	/**
	 * 排序字段的文本描述 --> 后台lucene的排序常量
	 */
	protected Map<String, Integer> orderFields = new HashMap<String, Integer>();

	/**
	 * @param orderField
	 *            web前端指定的排序方式
	 * @return 后台lucene排序常量
	 * 
	 * @see com.youku.search.util.Constant
	 */
	public int getOrderField(String orderField) {

		if (orderFields.containsKey(orderField)) {
			return orderFields.get(orderField);
		}

		throw new IllegalArgumentException("当前的排序类："+this.getClass().getSimpleName()+", 未知的排序字段！ orderField = "
				+ orderField);
	}

	/**
	 * @param orderField
	 *            后台lucene排序常量
	 * @param reverse
	 *            是否反序
	 * @return Comparator，供前台聚合查询结果时排序使用。返回null表示java默认排序
	 */
	public Comparator<T> getComparator(int orderField, boolean reverse) {
		return null;
	}

}
