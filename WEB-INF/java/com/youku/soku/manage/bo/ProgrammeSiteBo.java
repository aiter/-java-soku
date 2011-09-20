package com.youku.soku.manage.bo;

import java.util.Date;

public class ProgrammeSiteBo {
	
	 /** The value for the id field */
    private int id;

    /** The value for the fkProgrammeId field */
    private int fkProgrammeId;

    /** The value for the sourceSite field */
    private int sourceSite;

    /** The value for the orderId field */
    private int orderId;

    /** The value for the firstLogo field */
    private String firstLogo;

    /** The value for the completed field */
    private int completed = 0;

    /** The value for the blocked field */
    private int blocked = 0;

    /** The value for the midEmpty field */
    private int midEmpty = 0;

    /** The value for the episodeCollected field */
    private int episodeCollected = 0;

    /** The value for the source field */
    private int source = 0;

    /** The value for the otherSiteCompleted field */
    private int otherSiteCompleted = 0;

    /** The value for the updateTime field */
    private Date updateTime;

    /** The value for the createTime field */
    private Date createTime;

    private String siteName;
    
    private String name;
    
    

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFkProgrammeId() {
		return fkProgrammeId;
	}

	public void setFkProgrammeId(int fkProgrammeId) {
		this.fkProgrammeId = fkProgrammeId;
	}

	public int getSourceSite() {
		return sourceSite;
	}

	public void setSourceSite(int sourceSite) {
		this.sourceSite = sourceSite;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getFirstLogo() {
		return firstLogo;
	}

	public void setFirstLogo(String firstLogo) {
		this.firstLogo = firstLogo;
	}

	public int getCompleted() {
		return completed;
	}

	public void setCompleted(int completed) {
		this.completed = completed;
	}

	public int getBlocked() {
		return blocked;
	}

	public void setBlocked(int blocked) {
		this.blocked = blocked;
	}

	public int getMidEmpty() {
		return midEmpty;
	}

	public void setMidEmpty(int midEmpty) {
		this.midEmpty = midEmpty;
	}

	public int getEpisodeCollected() {
		return episodeCollected;
	}

	public void setEpisodeCollected(int episodeCollected) {
		this.episodeCollected = episodeCollected;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getOtherSiteCompleted() {
		return otherSiteCompleted;
	}

	public void setOtherSiteCompleted(int otherSiteCompleted) {
		this.otherSiteCompleted = otherSiteCompleted;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
    
    

}
