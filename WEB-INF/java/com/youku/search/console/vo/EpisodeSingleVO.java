package com.youku.search.console.vo;

public class EpisodeSingleVO {
	String name;
	String url;
	int id=-1;
	int subcate;
	int isLock=0;
	int order;
	
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public int getIsLock() {
		return isLock;
	}
	public void setIsLock(int isLock) {
		this.isLock = isLock;
	}
	public int getSubcate() {
		return subcate;
	}
	public void setSubcate(int subcate) {
		this.subcate = subcate;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
