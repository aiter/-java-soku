package com.youku.soku.manage.bo;

import java.util.Date;
import java.util.List;

public class EpisodeLogBo {
	
	private int id;
	
	private int subjectSiteId;
	
	private int orderId;
	
	private String title;
	
	private Date updateTime;
	
	private String url;
	
	private String operator;
	
	private List<EpisodeLogBo> historyOperation;
	

	public List<EpisodeLogBo> getHistoryOperation() {
		return historyOperation;
	}

	public void setHistoryOperation(List<EpisodeLogBo> historyOperation) {
		this.historyOperation = historyOperation;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSubjectSiteId() {
		return subjectSiteId;
	}

	public void setSubjectSiteId(int subjectSiteId) {
		this.subjectSiteId = subjectSiteId;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	
	
}
