package com.youku.soku.suggest.entity;

import java.io.Serializable;

public class EpisodeViewUrl implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2558649575163734357L;

	private int orderNumber;
	
	private String viewUrl;
	
	public EpisodeViewUrl(int orderNumber, String viewUrl) {
		this.orderNumber = orderNumber;
		this.viewUrl = viewUrl;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getViewUrl() {
		return viewUrl;
	}

	public void setViewUrl(String viewUrl) {
		this.viewUrl = viewUrl;
	}
	
	
}
