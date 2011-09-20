package com.youku.soku.manage.entity;

import java.util.Date;

public class SiteWeight {
	
	private int id;
	
	private int fkSiteId;
	
	private String siteName;
	
	private int normalWeight;
	
	private int libraryWeight;
	
	private Date updateTime;
	
	
	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFkSiteId() {
		return fkSiteId;
	}

	public void setFkSiteId(int fkSiteId) {
		this.fkSiteId = fkSiteId;
	}

	public int getNormalWeight() {
		return normalWeight;
	}

	public void setNormalWeight(int normalWeight) {
		this.normalWeight = normalWeight;
	}

	public int getLibraryWeight() {
		return libraryWeight;
	}

	public void setLibraryWeight(int libraryWeight) {
		this.libraryWeight = libraryWeight;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "SiteWeight [fkSiteId=" + fkSiteId + ", id=" + id
				+ ", libraryWeight=" + libraryWeight + ", normalWeight="
				+ normalWeight + ", siteName=" + siteName + ", updateTime="
				+ updateTime + "]";
	}

	
}
