package com.youku.search.recomend;

public class Entity {

	String keyword;
	int searchTimes;
	int valueNum;
	String keyword_py;
	
	public Entity() {
		super();
	}
	public Entity(String keyword, int searchTimes, int valueNum,
			String keyword_py) {
		super();
		this.keyword = keyword;
		this.searchTimes = searchTimes;
		this.valueNum = valueNum;
		this.keyword_py = keyword_py;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getSearchTimes() {
		return searchTimes;
	}
	public void setSearchTimes(int searchTimes) {
		this.searchTimes = searchTimes;
	}
	public int getValueNum() {
		return valueNum;
	}
	public void setValueNum(int valueNum) {
		this.valueNum = valueNum;
	}
	public String getKeyword_py() {
		return keyword_py;
	}
	public void setKeyword_py(String keyword_py) {
		this.keyword_py = keyword_py;
	}
	public boolean equals(Object entity){
		if(entity!=null && entity instanceof Entity){
			return ((Entity)entity).getKeyword().equals(keyword);
		}
		return false;
		
	}

}