package com.youku.soku.manage.bo.paihangbang;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ChannelStopWordsVO {
	private int id;
	private int cate ;
	private Date createTime;
	private Set<String> blocked_words = new HashSet<String>();
	private Set<String> deleted_words = new HashSet<String>();
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
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Set<String> getBlocked_words() {
		return blocked_words;
	}
	public void setBlocked_words(Set<String> blockedWords) {
		blocked_words = blockedWords;
	}
	public Set<String> getDeleted_words() {
		return deleted_words;
	}
	public void setDeleted_words(Set<String> deletedWords) {
		deleted_words = deletedWords;
	}
	
}
