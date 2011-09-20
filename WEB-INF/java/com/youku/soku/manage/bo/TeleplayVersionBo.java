package com.youku.soku.manage.bo;

import java.util.Date;
import java.util.List;

public class TeleplayVersionBo {
	/** The value for the id field */
	private int id;

	/** The value for the fkNamesId field */
	private int fkNamesId;

	/** The value for the contentId field */
	private int contentId;

	/** The value for the name field */
	private String name;

	/** The value for the alias field */
	private String alias;

	/** The value for the cates field */
	private String cates;

	/** The value for the orderId field */
	private int orderId;

	/** The value for the episodeTotal field */
	private int episodeTotal = 0;

	/** The value for the releaseTime field */
	private String releaseTime;

	/** The value for the area field */
	private String area;

	/** The value for the brief field */
	private String brief;

	/** The value for the haveRight field */
	private int haveRight = 0;

	/** The value for the viewUrl field */
	private String viewUrl;

	/** The value for the detailUrl field */
	private String detailUrl;

	/** The value for the released field */
	private int released = 0;

	/** The value for the createTime field */
	private Date createTime;

	/** The value for the updateTime field */
	private Date updateTime;

	/** the value of the fknamesid */
	private String names;

	private String namesAlias;

	private String searchkeys;

	private String directorsName;

	private String performersName;

	private String producersName;

	private String scenaristName;
	
	private int defaultDisplaySite;
	
	private List<String> cateList;

	private String haibao;
	
	private String firstLogo;	
	

	public String getFirstLogo() {
		return firstLogo;
	}

	public void setFirstLogo(String firstLogo) {
		this.firstLogo = firstLogo;
	}

	public int getDefaultDisplaySite() {
		return defaultDisplaySite;
	}

	public void setDefaultDisplaySite(int defaultDisplaySite) {
		this.defaultDisplaySite = defaultDisplaySite;
	}

	public String getDirectorsName() {
		return directorsName;
	}

	public void setDirectorsName(String directorsName) {
		this.directorsName = directorsName;
	}

	public String getPerformersName() {
		return performersName;
	}

	public void setPerformersName(String performersName) {
		this.performersName = performersName;
	}

	public String getProducersName() {
		return producersName;
	}

	public void setProducersName(String producersName) {
		this.producersName = producersName;
	}

	public String getScenaristName() {
		return scenaristName;
	}

	public void setScenaristName(String scenaristName) {
		this.scenaristName = scenaristName;
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

	private List<PersonSubjectBo> perSubList;

	public List<PersonSubjectBo> getPerSubList() {
		return perSubList;
	}

	public void setPerSubList(List<PersonSubjectBo> perSubList) {
		this.perSubList = perSubList;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getCates() {
		return cates;
	}

	public void setCates(String cates) {
		this.cates = cates;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getEpisodeTotal() {
		return episodeTotal;
	}

	public void setEpisodeTotal(int episodeTotal) {
		this.episodeTotal = episodeTotal;
	}

	public String getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(String releaseTime) {
		this.releaseTime = releaseTime;
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

	public int getReleased() {
		return released;
	}

	public void setReleased(int released) {
		this.released = released;
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

}
