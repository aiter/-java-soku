package com.youku.soku.top.directory;

import org.json.JSONObject;

public class MovieTopResult{
	String top_date;
	JSONObject contents = new JSONObject();
//	所有满足条件的记录总数
	int total;
	
	public String getTop_date() {
		return top_date;
	}
	public void setTop_date(String topDate) {
		top_date = topDate;
	}
	
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public JSONObject getContents() {
		return contents;
	}
	public void setContents(JSONObject contents) {
		this.contents = contents;
	}
	
	
	
}
