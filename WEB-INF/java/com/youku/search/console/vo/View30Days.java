package com.youku.search.console.vo;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.jsonplugin.annotations.JSON;

public class View30Days {
	private String date;
	private int sum;
	private Map<String,Integer> datemap=new HashMap<String, Integer>();
	private String rate;
	private boolean inMounth;
	
	@JSON(serialize=false)
	public boolean isInMounth() {
		return inMounth;
	}

	@JSON(serialize=false)
	public void setInMounth(boolean inMounth) {
		this.inMounth = inMounth;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getDate() {
		return date;
	}
	
	public int getSum() {
		return sum;
	}

	public void setSum(int sum) {
		this.sum = sum;
	}

	public void setDate(String date) {
		this.date = date;
	}
	public Map<String, Integer> getDatemap() {
		return datemap;
	}
	public void setDatemap(Map<String, Integer> datemap) {
		this.datemap = datemap;
	}
	
}
