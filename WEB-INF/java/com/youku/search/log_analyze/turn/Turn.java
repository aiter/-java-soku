package com.youku.search.log_analyze.turn;

import com.youku.search.log_analyze.base.BaseLogData;

/**
 * 翻页统计数据
 */
public class Turn extends BaseLogData {

	@Override
	public String getKey() {
		return keyword + "\t" + source + "\t" + query_type;
	}
}
