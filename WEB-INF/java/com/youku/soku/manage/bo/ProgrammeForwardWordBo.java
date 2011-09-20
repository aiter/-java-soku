package com.youku.soku.manage.bo;

import java.util.Date;

public class ProgrammeForwardWordBo {
	
	private int programmeId;
	
	private String programmeName;
	
	private String forwardWords;
	
	private Date startTime;
	
	private Date expireTime;
	
	private String cateName;
	

	public String getCateName() {
		return cateName;
	}

	public void setCateName(String cateName) {
		this.cateName = cateName;
	}

	public int getProgrammeId() {
		return programmeId;
	}

	public void setProgrammeId(int programmeId) {
		this.programmeId = programmeId;
	}

	public String getProgrammeName() {
		return programmeName;
	}

	public void setProgrammeName(String programmeName) {
		this.programmeName = programmeName;
	}

	public String getForwardWords() {
		return forwardWords;
	}

	public void setForwardWords(String forwardWords) {
		this.forwardWords = forwardWords;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}
	
	
}
