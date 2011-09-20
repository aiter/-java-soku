package com.youku.search.log_analyze.query;

import com.youku.search.log_analyze.base.BaseLogData;

/**
 * 查询统计数据
 */
public class Query extends BaseLogData {

	public String keyword_py;

	@Override
	public String getKey() {
		return keyword + "\t" + source + "\t" + query_type + "\t" + because;
	}
}
