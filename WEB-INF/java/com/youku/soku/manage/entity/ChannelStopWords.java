package com.youku.soku.manage.entity;

import java.util.Date;

public class ChannelStopWords {

	private int id;
	
	private String fkChannelName;
	
	private String keywords;
	
	private Date createTime;
	
	private Date updateTime;
	
	private String channelLabel;
	
	private String deleteWords;
	
	

	public String getDeleteWords() {
		return deleteWords;
	}

	public void setDeleteWords(String deleteWords) {
		this.deleteWords = deleteWords;
	}

	public String getChannelLabel() {
		return channelLabel;
	}

	public void setChannelLabel(String channelLabel) {
		this.channelLabel = channelLabel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFkChannelName() {
		return fkChannelName;
	}

	public void setFkChannelName(String fkChannelName) {
		this.fkChannelName = fkChannelName;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "ChannelStopWords [channelLabel=" + channelLabel
				+ ", createTime=" + createTime + ", fkChannelName="
				+ fkChannelName + ", id=" + id + ", keywords=" + keywords
				+ ", updateTime=" + updateTime + "]";
	}
	
	
}
