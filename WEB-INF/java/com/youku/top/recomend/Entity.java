package com.youku.top.recomend;

class Entity {

	String keyword;
	int searchTimes;
	int valueNum;
	String keyword_py;
	int type=0;
	
	public Entity() {
		super();
	}
	public Entity(String keyword, int searchTimes, int valueNum,
			String keyword_py,int type) {
		super();
		this.keyword = keyword;
		this.searchTimes = searchTimes;
		this.valueNum = valueNum;
		this.keyword_py = keyword_py;
		this.type = type;
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
	
	public int getType() {
		return type;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
		result = prime * result + type;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entity other = (Entity) obj;
		if (keyword == null) {
			if (other.keyword != null)
				return false;
		} else if (!keyword.equals(other.keyword))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
}