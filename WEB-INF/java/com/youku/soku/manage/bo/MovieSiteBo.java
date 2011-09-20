package com.youku.soku.manage.bo;

import java.util.Date;

public class MovieSiteBo {
	/** The value for the id field */
    private int id;

    /** The value for the fkMovieId field */
    private int fkMovieId;

    /** The value for the sourceSite field */
    private int sourceSite;

    /** The value for the logo field */
    private String logo;

    /** The value for the hd field */
    private int hd = 0;

    /** The value for the blocked field */
    private int blocked = 0;

    /** The value for the locked field */
    private int locked = 0;

    /** The value for the viewUrl field */
    private String viewUrl;

    /** The value for the detailUrl field */
    private String detailUrl;

    /** The value for the source field */
    private int source = 0;

    /** The value for the orderId field */
    private int orderId;

    /** The value for the updateTime field */
    private Date updateTime;

    /** The value for the createTime field */
    private Date createTime;
    
    private String names;
    
    private String siteName;
    
    

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFkMovieId() {
		return fkMovieId;
	}

	public void setFkMovieId(int fkMovieId) {
		this.fkMovieId = fkMovieId;
	}

	public int getSourceSite() {
		return sourceSite;
	}

	public void setSourceSite(int sourceSite) {
		this.sourceSite = sourceSite;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public int getHd() {
		return hd;
	}

	public void setHd(int hd) {
		this.hd = hd;
	}

	public int getBlocked() {
		return blocked;
	}

	public void setBlocked(int blocked) {
		this.blocked = blocked;
	}

	public int getLocked() {
		return locked;
	}

	public void setLocked(int locked) {
		this.locked = locked;
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

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
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
    
    
    
}
