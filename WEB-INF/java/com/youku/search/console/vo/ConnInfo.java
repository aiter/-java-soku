package com.youku.search.console.vo;

import java.util.HashMap;
import java.util.Map;

public class ConnInfo {
	private Map<String,Integer> total=new HashMap<String, Integer>();
	private Detail[] detail;
	public Map<String, Integer> getTotal() {
		return total;
	}
	public void setTotal(Map<String, Integer> total) {
		this.total = total;
	}
	public Detail[] getDetail() {
		return detail;
	}
	public void setDetail(Detail[] detail) {
		this.detail = detail;
	}
	
	
}
