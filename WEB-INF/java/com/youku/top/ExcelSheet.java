package com.youku.top;

import java.util.ArrayList;
import java.util.List;

public class ExcelSheet {

	String channel;
	int content_id;
	String keyword;
	String search_key;
	int total;
	int avg;
	List<Integer> list = new ArrayList<Integer>();
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public int getContent_id() {
		return content_id;
	}
	public void setContent_id(int contentId) {
		content_id = contentId;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getSearch_key() {
		return search_key;
	}
	public void setSearch_key(String searchKey) {
		search_key = searchKey;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getAvg() {
		return avg;
	}
	public void setAvg(int avg) {
		this.avg = avg;
	}
	public List<Integer> getList() {
		return list;
	}
	public void setList(List<Integer> list) {
		this.list = list;
	}
	
	
	
}
