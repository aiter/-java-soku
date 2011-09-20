package com.youku.search.console.vo;

import java.util.HashMap;

public class ViewTotal {
	private float totalRate1;
	private float totalRate2;
	private float totalRate3;
	private HashMap<Integer,Turn[]> turnmap=new HashMap<Integer, Turn[]>();
	private String date;
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public float getTotalRate1() {
		return totalRate1;
	}
	public void setTotalRate1(float totalRate1) {
		this.totalRate1 = totalRate1;
	}
	public float getTotalRate2() {
		return totalRate2;
	}
	public void setTotalRate2(float totalRate2) {
		this.totalRate2 = totalRate2;
	}
	public float getTotalRate3() {
		return totalRate3;
	}
	public void setTotalRate3(float totalRate3) {
		this.totalRate3 = totalRate3;
	}
	public HashMap<Integer, Turn[]> getTurnmap() {
		return turnmap;
	}
	public void setTurnmap(HashMap<Integer, Turn[]> turnmap) {
		this.turnmap = turnmap;
	}
	
}
