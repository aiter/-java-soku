package com.youku.top.merge;

public class LogDataDay {
	String keyword;
	int query_count;
	String src_keyword;
	
	public String getSrc_keyword() {
		return src_keyword;
	}
	public void setSrc_keyword(String srcKeyword) {
		src_keyword = srcKeyword;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getQuery_count() {
		return query_count;
	}
	public void setQuery_count(int queryCount) {
		query_count = queryCount;
	}
	
}
