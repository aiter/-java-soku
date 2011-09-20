package com.youku.soku.suggest.trie;

public class DirectEntity {

	private String liTip = "";
	
	private String divTip = "";
	
	private String divTipYear = "";
	
	private String performers = "";
	
	private String programmeEncodeId;
	
	private String logo;
	
	private String url;
	
	private String programmeName;

	//add on 2011-06-04   站内剧集页id
	private String showIdStr;
	

	public String getShowIdStr() {
		return showIdStr;
	}

	public void setShowIdStr(String showIdStr) {
		this.showIdStr = showIdStr;
	}

	public String getDivTipYear() {
		return divTipYear;
	}

	public void setDivTipYear(String divTipYear) {
		this.divTipYear = divTipYear;
	}

	public String getProgrammeName() {
		return programmeName;
	}

	public void setProgrammeName(String programmeName) {
		this.programmeName = programmeName;
	}

	public String getLiTip() {
		return liTip;
	}

	public void setLiTip(String liTip) {
		this.liTip = liTip;
	}

	public String getDivTip() {
		return divTip;
	}

	public void setDivTip(String divTip) {
		this.divTip = divTip;
	}

	public String getPerformers() {
		return performers;
	}

	public void setPerformers(String performers) {
		this.performers = performers;
	}


	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
	public String getProgrammeEncodeId() {
		return programmeEncodeId;
	}

	public void setProgrammeEncodeId(String programmeEncodeId) {
		this.programmeEncodeId = programmeEncodeId;
	}

	@Override
	public boolean equals(Object anObject) {
		if (this == anObject) {
		    return true;
		}
		if(anObject instanceof DirectEntity) {
			DirectEntity de = (DirectEntity) anObject;
			if(de.getProgrammeEncodeId().equals(this.programmeEncodeId)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "DirectEntity [divTip=" + divTip + ", divTipYear=" + divTipYear + ", liTip=" + liTip + ", logo=" + logo + ", performers=" + performers
				+ ", programmeEncodeId=" + programmeEncodeId + ", programmeName=" + programmeName + ", url=" + url + "]";
	}
	
	
}
