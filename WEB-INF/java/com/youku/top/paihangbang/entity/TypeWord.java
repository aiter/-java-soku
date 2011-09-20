package com.youku.top.paihangbang.entity;

import java.util.Date;

public class TypeWord {
	int id;
	String keyword;
	int programme_id;
	String state;
	int cate;
	String pic;
	String checker;
	Date create_date;
	Date update_date;
	
	
	public TypeWord(String keyword, int cate, String pic) {
		super();
		this.keyword = keyword;
		this.cate = cate;
		this.pic = pic;
	}
	public TypeWord() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
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
	public String getChecker() {
		return checker;
	}
	public void setChecker(String checker) {
		this.checker = checker;
	}
	public Date getCreate_date() {
		return create_date;
	}
	public void setCreate_date(Date createDate) {
		create_date = createDate;
	}
	public int getProgramme_id() {
		return programme_id;
	}
	public void setProgramme_id(int programmeId) {
		programme_id = programmeId;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public int getCate() {
		return cate;
	}
	public void setCate(int cate) {
		this.cate = cate;
	}
	public Date getUpdate_date() {
		return update_date;
	}
	public void setUpdate_date(Date updateDate) {
		update_date = updateDate;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cate;
		result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
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
		TypeWord other = (TypeWord) obj;
		if (cate != other.cate)
			return false;
		if (keyword == null) {
			if (other.keyword != null)
				return false;
		} else if (!keyword.equals(other.keyword))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "TypeWord [cate=" + cate + ", checker=" + checker
				+ ", create_date=" + create_date + ", id=" + id + ", keyword="
				+ keyword + ", pic=" + pic + ", programme_id=" + programme_id
				+ ", state=" + state + ", update_date=" + update_date + "]";
	}
	
}
