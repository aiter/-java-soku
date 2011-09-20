package com.youku.soku.manage.shield;

public class SearchParameter {
	
	private String keyword;
	
	private int type;
	
	private int channel;
	
	private int wordCategory;
	
	private int siteLevel;
	
	private int siteCategory;
	
	private int hitRole;
	
	private String expireDate;
	
	private String modifier;
	
	
	private int trend;;
	
	private int orderby;
	
	
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getWordCategory() {
		return wordCategory;
	}

	public void setWordCategory(int wordCategory) {
		this.wordCategory = wordCategory;
	}


	public int getSiteLevel() {
		return siteLevel;
	}

	public void setSiteLevel(int siteLevel) {
		this.siteLevel = siteLevel;
	}

	public int getHitRole() {
		return hitRole;
	}

	public void setHitRole(int hitRole) {
		this.hitRole = hitRole;
	}

	public String getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public int getSiteCategory() {
		return siteCategory;
	}

	public void setSiteCategory(int siteCategory) {
		this.siteCategory = siteCategory;
	}
	
	

	public int getTrend() {
		return trend;
	}

	public void setTrend(int trend) {
		this.trend = trend;
	}

	public int getOrderby() {
		return orderby;
	}

	public void setOrderby(int orderby) {
		this.orderby = orderby;
	}

	@Override
	public String toString() {
		return "SearchParameter [channel=" + channel + ", expireDate="
				+ expireDate + ", hitRole=" + hitRole + ", keyword=" + keyword
				+ ", modifier=" + modifier + ", siteCategory=" + siteCategory
				+ ", siteLevel=" + siteLevel + ", type=" + type
				+ ", wordCategory=" + wordCategory + "]";
	}


	
	
}
