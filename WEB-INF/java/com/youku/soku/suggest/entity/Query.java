package com.youku.soku.suggest.entity;

public class Query {
	
	private int id;
	
	private String keyword;
	
	private String keywordPy;
	
	private String source;
	
	private String queryType;
	
	private String because;
	
	private int result;
	
	private int queryCount;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getKeywordPy() {
		return keywordPy;
	}

	public void setKeywordPy(String keywordPy) {
		this.keywordPy = keywordPy;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public String getBecause() {
		return because;
	}

	public void setBecause(String because) {
		this.because = because;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public int getQueryCount() {
		return queryCount;
	}

	public void setQueryCount(int queryCount) {
		this.queryCount = queryCount;
	}
	
	
}
