package com.youku.soku.netspeed;

import java.util.HashMap;
import java.util.Map;


public class Speed {
	private static Map<Integer, Speed> cache = new HashMap<Integer, Speed>();
	
	double quotiety;
	Count count;
	
	private int normalValue ;
	
	public Speed(int value){
		this.normalValue = value;
	}
	
	public static Speed valueOf(int value) {
		
		Speed speed = cache.get(value);
		if (speed == null){
			speed = new Speed(value);
			cache.put(value, speed);
		}
		return speed;
	}
	
	public int getValue() {
		return normalValue;
	}
	
	public String toString(){
		return normalValue + "";
	}

	public double getQuotiety() {
		return quotiety;
	}

	public void setQuotiety(double quotiety) {
		this.quotiety = quotiety;
	}

	public Count getCount() {
		return count;
	}

	public void setCount(Count count) {
		this.count = count;
	}
	
}
