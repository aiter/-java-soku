package com.youku.search.sort;

public enum ParameterNames {

	// ==== 供查询使用的参数 ======================================================
	// 第零部分：普通查询、高级查询共同使用的参数
	keyword,
	type, 
	curpage, 
	pagesize, 
	orderfield, 
	order, 
	ftype, 
	timeless, 
	timemore, 
	limit_date, 
	hl, 
	hl_prefix, 
	hl_suffix, 
	limit_level, 
	exclude_cates,

	// 第一部分：普通查询使用的参数
	logic, 
	cateid, 
	partner, 
	na, 
	md5, 
	relnum, 
	video_options,

	// 剧集搜索扩充
	callback, 
	feedback,

	// 第二部分：高级查询使用的参数
	advance, 
	categories, 
	fields, 
	pv, 
	comments, 
	hd,

	// ==== 供管理、修饰、统计等杂项使用的参数 =======================================
	admin, h, _source, query_url

}
