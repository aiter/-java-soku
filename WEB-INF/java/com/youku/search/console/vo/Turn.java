package com.youku.search.console.vo;

public class Turn {
//	private int id;
	private String keyword;
	private String query_type;
//	private int page;
//	private int query_result;
//	private int query_count;
//	private int total_query_count;
	private float rate;
	private float rate_remain;

	public float getRate_remain() {
		return rate_remain;
	}
	public void setRate_remain(float rate_remain) {
		this.rate_remain = rate_remain;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public String getQuery_type() {
		return query_type;
	}
	public void setQuery_type(String query_type) {
		this.query_type = query_type;
	}
	public float getRate() {
		return rate;
	}
	public void setRate(float rate) {
		this.rate = rate;
	}
	
}
