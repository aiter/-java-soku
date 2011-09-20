package com.youku.soku.manage.entity;

import java.util.Date;

public class MajorTerm {
	
	private int id;
	
	private int cateId;
	
	private String keyword;
	
	private String urlText;
	
	private String htmlText;
	
	private int status;
	
	private String destUrl;
	
	private Date createTime;
	
	private Date updaetTime;
	
	private String label;
	
	

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getCateId() {
		return cateId;
	}

	public void setCateId(int cateId) {
		this.cateId = cateId;
	}

	public String getDestUrl() {
		return destUrl;
	}

	public void setDestUrl(String destUrl) {
		this.destUrl = destUrl;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrlText() {
		return urlText;
	}

	public void setUrlText(String urlText) {
		this.urlText = urlText;
	}

	public String getHtmlText() {
		return htmlText;
	}

	public void setHtmlText(String htmlText) {
		this.htmlText = htmlText;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdaetTime() {
		return updaetTime;
	}

	public void setUpdaetTime(Date updaetTime) {
		this.updaetTime = updaetTime;
	}
	
	

}
