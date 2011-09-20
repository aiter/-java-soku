package com.youku.soku.manage.bo;

import java.util.Date;
import java.util.List;

public class VarietyBo {
	
	 /** The value for the id field */
    private int id;

    /** The value for the fkNamesId field */
    private int fkNamesId;

    /** The value for the contentId field */
    private int contentId;

    /** The value for the cates field */
    private String cates;

    /** The value for the name field */
    private String name;

    /** The value for the area field */
    private String area;

    /** The value for the brief field */
    private String brief;

    /** The value for the haveRight field */
    private int haveRight = 0;

    /** The value for the createTime field */
    private Date createTime;

    /** The value for the updateTime field */
    private Date updateTime;
    
    /** The value of the Variety Name  */
    private String names;
    
    private String searchkeys;

	private String namesAlias;

	private String haibao;

	private List<String> cateList;
	
	private String hostName;
	
	private String tvsite;
	
	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	private int orderId;
    
    public String getSearchkeys() {
		return searchkeys;
	}

	public void setSearchkeys(String searchkeys) {
		this.searchkeys = searchkeys;
	}

	public String getNamesAlias() {
		return namesAlias;
	}

	public void setNamesAlias(String namesAlias) {
		this.namesAlias = namesAlias;
	}

	public String getHaibao() {
		return haibao;
	}

	public void setHaibao(String haibao) {
		this.haibao = haibao;
	}

	public List<String> getCateList() {
		return cateList;
	}

	public void setCateList(List<String> cateList) {
		this.cateList = cateList;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getTvsite() {
		return tvsite;
	}

	public void setTvsite(String tvsite) {
		this.tvsite = tvsite;
	}

	private List<PersonSubjectBo> perSubList;

	public List<PersonSubjectBo> getPerSubList() {
		return perSubList;
	}

	public void setPerSubList(List<PersonSubjectBo> perSubList) {
		this.perSubList = perSubList;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFkNamesId() {
		return fkNamesId;
	}

	public void setFkNamesId(int fkNamesId) {
		this.fkNamesId = fkNamesId;
	}

	public int getContentId() {
		return contentId;
	}

	public void setContentId(int contentId) {
		this.contentId = contentId;
	}

	public String getCates() {
		return cates;
	}

	public void setCates(String cates) {
		this.cates = cates;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public int getHaveRight() {
		return haveRight;
	}

	public void setHaveRight(int haveRight) {
		this.haveRight = haveRight;
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
}
