package com.youku.soku.manage.bo;

import java.util.Date;

public class AnimeSiteVersionBo {
	 /** The value for the id field */
    private int id;

    /** The value for the fkAnimeVersionId field */
    private int fkAnimeVersionId;

    /** The value for the sourceSite field */
    private int sourceSite;

    /** The value for the orderId field */
    private int orderId;

    /** The value for the firstLogo field */
    private String firstLogo;
    

    /** The value for the anime name */
    private String names;
    
    /** The value for the anime version */
    private String versionName;
    
    /** The value for the taleplay version alias */
    private String alias;
    
    public String getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	/** The value for the source site field */
    private String siteName;

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

	public int getFkAnimeVersionId() {
		return fkAnimeVersionId;
	}

	public void setFkAnimeVersionId(int fkAnimeVersionId) {
		this.fkAnimeVersionId = fkAnimeVersionId;
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

	public int getLocked() {
		return locked;
	}

	public void setLocked(int locked) {
		this.locked = locked;
	}

	public int getHd() {
		return hd;
	}

	public void setHd(int hd) {
		this.hd = hd;
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

	public int getEpisodeCollected() {
		return episodeCollected;
	}

	public void setEpisodeCollected(int episodeCollected) {
		this.episodeCollected = episodeCollected;
	}

	public String getViewUrl() {
		return viewUrl;
	}

	public void setViewUrl(String viewUrl) {
		this.viewUrl = viewUrl;
	}

	public String getDetailUrl() {
		return detailUrl;
	}

	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
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

	/** The value for the locked field */
    private int locked = 0;

    /** The value for the hd field */
    private int hd = 0;

    /** The value for the completed field */
    private int completed = 0;

    /** The value for the blocked field */
    private int blocked = 0;

    /** The value for the episodeCollected field */
    private int episodeCollected = 0;

    /** The value for the viewUrl field */
    private String viewUrl;

    /** The value for the detailUrl field */
    private String detailUrl;

    /** The value for the createTime field */
    private Date createTime;

    /** The value for the updateTime field */
    private Date updateTime;
}
