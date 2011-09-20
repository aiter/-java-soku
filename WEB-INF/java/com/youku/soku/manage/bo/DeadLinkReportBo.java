package com.youku.soku.manage.bo;

import java.util.Date;

public class DeadLinkReportBo {
	
	 /** The value for the id field */
    private int id;

    /** The value for the url field */
    private String url;

    /** The value for the programmeId field */
    private int programmeId = 1;

    /** The value for the programmeSiteId field */
    private int programmeSiteId = 1;

    /** The value for the orderStage field */
    private int orderStage = 1;
    
    private String programmeName;
    
    private String siteName;
    
    private Date updateTime;
    
    private int status;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getProgrammeId() {
		return programmeId;
	}

	public void setProgrammeId(int programmeId) {
		this.programmeId = programmeId;
	}

	public int getProgrammeSiteId() {
		return programmeSiteId;
	}

	public void setProgrammeSiteId(int programmeSiteId) {
		this.programmeSiteId = programmeSiteId;
	}

	public int getOrderStage() {
		return orderStage;
	}

	public void setOrderStage(int orderStage) {
		this.orderStage = orderStage;
	}

	public String getProgrammeName() {
		return programmeName;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public void setProgrammeName(String programmeName) {
		this.programmeName = programmeName;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
    
    
}
