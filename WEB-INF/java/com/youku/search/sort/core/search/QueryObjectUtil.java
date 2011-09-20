package com.youku.search.sort.core.search;

import com.youku.search.index.entity.BasePageQuery;
import com.youku.search.pool.net.QueryHolder;
import com.youku.search.sort.core.entity.Span;

public class QueryObjectUtil {

	public static void errorType(Object queryObject) {

		StringBuilder builder = new StringBuilder();

		builder.append("错误的查询对象类型： ");
		builder.append(String.valueOf(queryObject));

		if (queryObject != null) {
			builder.append("; class: " + queryObject.getClass());
		}

		throw new IllegalArgumentException(builder.toString());
	}

	public static void nextPage(Object queryObject) {

		if (queryObject instanceof BasePageQuery) {
			BasePageQuery query = (BasePageQuery) queryObject;

			final int page_size = query.end - query.start;
			query.start += page_size;
			query.end += page_size;
			
			// 每次移动IndexServer的一页，当调用C-Server时用page_no
			// gaosong 2011-05-31
			if (null != query.indexPage) {
				query.indexPage.page_no++;
			}
		} else {
			errorType(queryObject);
		}
	}
	
	public static void updateSpan(Object queryObject, Span span) {

		if (queryObject instanceof BasePageQuery) {
			BasePageQuery query = (BasePageQuery) queryObject;

			query.start = span.start;
			query.end = span.end;

		} else {
			errorType(queryObject);
		}
	}

}
