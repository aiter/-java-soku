package com.youku.soku.manage.entity;

import java.util.Date;

public class ChannelNavigation {
	private int id;

	private String fkChannelName;

	private String navigationText;

	private Date createTime;

	private Date updateTime;

	private String channelLabel;
	
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

	public String getNavigationText() {
		return navigationText;
	}

	public void setNavigationText(String navigationText) {
		this.navigationText = navigationText;
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

	public String getChannelLabel() {
		return channelLabel;
	}

	public void setChannelLabel(String channelLabel) {
		this.channelLabel = channelLabel;
	}
	
	
}
