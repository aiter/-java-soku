package com.youku.search.log_analyze.base;

public class BaseLogData {

	// log中的数据项
	public String keyword;// 关键词
	public String source;// 来源
	public String query_type;// 查询类型
	public String because;// 查询原因
	public int page;// 页码

	public int result_count;// 查询总结果数

	// 统计出来的数据项
	public int query_count;// 查询次数

	/**
	 * 用于汇总数据的主键
	 */
	public String getKey() {
		throw new RuntimeException("请实现此方法");
	}
}