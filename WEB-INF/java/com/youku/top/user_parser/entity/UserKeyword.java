package com.youku.top.user_parser.entity;

public class UserKeyword {
	String user_key;
	String keyword;
	int query_count;
	public String getUser_key() {
		return user_key;
	}
	public void setUser_key(String userKey) {
		user_key = userKey;
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
