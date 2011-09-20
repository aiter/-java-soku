package com.youku.soku.suggest.entity;

import java.io.Serializable;
import java.util.Date;

public class PersonWork implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6644439768782542182L;

	private String workName;
	
	private Date releaseTime;
	
	private int cate;
	

	public int getCate() {
		return cate;
	}

	public void setCate(int cate) {
		this.cate = cate;
	}

	public String getWorkName() {
		return workName;
	}

	public void setWorkName(String workName) {
		this.workName = workName;
	}

	public Date getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(Date releaseTime) {
		this.releaseTime = releaseTime;
	}

	@Override
	public String toString() {
		return "PersonWork [releaseTime=" + releaseTime + ", workName="
				+ workName + "]";
	}
	
	
}
