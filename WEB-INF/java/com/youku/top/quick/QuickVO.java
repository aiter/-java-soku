package com.youku.top.quick;

public class QuickVO {
	String keyword;
	int query_count1;
	int order_number;
	int cate;
	String url;
	int has_directory;
	int query_count2;
	int query_count_sub;
	int visible = 1;
	
	
	public int getVisible() {
		return visible;
	}
	public void setVisible(int visible) {
		this.visible = visible;
	}
	public int getQuery_count1() {
		return query_count1;
	}
	public void setQuery_count1(int queryCount1) {
		query_count1 = queryCount1;
	}
	public int getQuery_count2() {
		return query_count2;
	}
	public void setQuery_count2(int queryCount2) {
		query_count2 = queryCount2;
	}
	public int getQuery_count_sub() {
		return query_count_sub;
	}
	public void setQuery_count_sub(int queryCountSub) {
		query_count_sub = queryCountSub;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getOrder_number() {
		return order_number;
	}
	public void setOrder_number(int orderNumber) {
		order_number = orderNumber;
	}
	public int getCate() {
		return cate;
	}
	public void setCate(int cate) {
		this.cate = cate;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getHas_directory() {
		return has_directory;
	}
	public void setHas_directory(int hasDirectory) {
		has_directory = hasDirectory;
	}
	@Override
	public String toString() {
		return "QuickVO [cate=" + cate + ", has_directory=" + has_directory
				+ ", keyword=" + keyword + ", order_number=" + order_number
				+ ", query_count1=" + query_count1 + ", query_count2="
				+ query_count2 + ", query_count_sub=" + query_count_sub
				+ ", url=" + url + ", visible=" + visible + "]";
	}
	
}
