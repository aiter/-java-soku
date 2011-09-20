package com.youku.top.topn.entity;

public class KeywordVO {
	String keyword;
	int cate;
	int searchs;
	int web_searchs;
	int clicks;
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getCate() {
		return cate;
	}
	public void setCate(int cate) {
		this.cate = cate;
	}
	public int getSearchs() {
		return searchs;
	}
	public void setSearchs(int searchs) {
		this.searchs = searchs;
	}
	public int getWeb_searchs() {
		return web_searchs;
	}
	public void setWeb_searchs(int webSearchs) {
		web_searchs = webSearchs;
	}
	public int getClicks() {
		return clicks;
	}
	public void setClicks(int clicks) {
		this.clicks = clicks;
	}
	
}
