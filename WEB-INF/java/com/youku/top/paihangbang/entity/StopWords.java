package com.youku.top.paihangbang.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class StopWords {
	int id;
	int cate;
	Set<String> deleted_words = new HashSet<String>();
	Set<String> blocked_words = new HashSet<String>();
	Date create_date;
	Date update_date;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCate() {
		return cate;
	}
	public void setCate(int cate) {
		this.cate = cate;
	}
	public Set<String> getDeleted_words() {
		return deleted_words;
	}
	public void setDeleted_words(Set<String> deletedWords) {
		deleted_words = deletedWords;
	}
	public Set<String> getBlocked_words() {
		return blocked_words;
	}
	public void setBlocked_words(Set<String> blockedWords) {
		blocked_words = blockedWords;
	}
	public Date getCreate_date() {
		return create_date;
	}
	public void setCreate_date(Date createDate) {
		create_date = createDate;
	}
	public Date getUpdate_date() {
		return update_date;
	}
	public void setUpdate_date(Date updateDate) {
		update_date = updateDate;
	}
	
}
