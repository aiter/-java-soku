package com.youku.top.topn.entity;

import java.util.HashSet;
import java.util.Set;

public class KeywordWeekVO {
	String keyword;
	String source;
	String query_type;
	int because;
	int result=0;
	int query_count=0;
	float rate ;
	int id ;
	Set<String> types = new HashSet<String>();
	
	public Set<String> getTypes() {
		return types;
	}
	public void setTypes(Set<String> types) {
		this.types = types;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public float getRate() {
		return rate;
	}
	public void setRate(float rate) {
		this.rate = rate;
	}
	public int getBecause() {
		return because;
	}
	public void setBecause(int because) {
		this.because = because;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getQuery_type() {
		return query_type;
	}
	public void setQuery_type(String queryType) {
		query_type = queryType;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public int getQuery_count() {
		return query_count;
	}
	public void setQuery_count(int queryCount) {
		query_count = queryCount;
	}
	
}
