package com.youku.soku.manage.bo;

import java.util.Date;

public class VarietyEpisodeBo {

	  /** The value for the id field */
    private int id;

    /** The value for the fkVarietySubId field */
    private int fkVarietySubId;

    /** The value for the logo field */
    private String logo;

    /** The value for the locked field */
    private int locked = 0;

    /** The value for the hd field */
    private int hd = 0;

    /** The value for the title field */
    private String title;

    /** The value for the sourceSite field */
    private int sourceSite;

    /** The value for the orderId field */
    private int orderId;

    /** The value for the seconds field */
    private String seconds;

    /** The value for the url field */
    private String url;

    /** The value for the readyUrl field */
    private String readyUrl;

    /** The value for the createTime field */
    private Date createTime;

    /** The value for the updateTime field */
    private Date updateTime;
    
    private String names;
    
    private String subName;
    
    private String epDate;
    
    private String varietySubName;
    
    private int orderIdType;
    
    public int getOrderIdType() {
		return orderIdType;
	}

	public void setOrderIdType(int orderIdType) {
		this.orderIdType = orderIdType;
	}

	public String getEpDate() {
		return epDate;
	}

	public void setEpDate(String epDate) {
		this.epDate = epDate;
	}

	public String getVarietySubName() {
		return varietySubName;
	}

	public void setVarietySubName(String varietySubName) {
		this.varietySubName = varietySubName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFkVarietySubId() {
		return fkVarietySubId;
	}

	public void setFkVarietySubId(int fkVarietySubId) {
		this.fkVarietySubId = fkVarietySubId;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getSeconds() {
		return seconds;
	}

	public void setSeconds(String seconds) {
		this.seconds = seconds;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getReadyUrl() {
		return readyUrl;
	}

	public void setReadyUrl(String readyUrl) {
		this.readyUrl = readyUrl;
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

	public String getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names;
	}

	public String getSubName() {
		return subName;
	}

	public void setSubName(String subName) {
		this.subName = subName;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	private String siteName;

}
