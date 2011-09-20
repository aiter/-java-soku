package com.youku.soku.netspeed;


public class Node{
	
	public Node(int site,String key,Count count){
		this.site = site;
		this.key  = key;
		this.count = count;
	}
	
	private int site;
	private String key;
	private Count count;
	
	
	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}


	public Count getCount() {
		return count;
	}


	public void setCount(Count count) {
		this.count = count;
	}


	public int getSite() {
		return site;
	}


	public void setSite(int site) {
		this.site = site;
	}


	public String toString(){
		return key + "\t ELEMENT:" + count;
	}
	
}
