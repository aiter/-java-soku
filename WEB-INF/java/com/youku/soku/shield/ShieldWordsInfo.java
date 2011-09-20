package com.youku.soku.shield;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.youku.soku.manage.shield.ShieldInfo;

public class ShieldWordsInfo {

	public ShieldWordsInfo() {
		System.out.println("new ShieldWordsInfo in Thread : " + Thread.currentThread().getName());
	}

	private Map<String, ShieldInfo> accurateHitWordsMap = new HashMap<String, ShieldInfo>();

	private Map<String, ShieldInfo> containHitWordsMap = new HashMap<String, ShieldInfo>();

	private Map<String, ShieldInfo> fuzzyHitWordsMap = new HashMap<String, ShieldInfo>();
	
	private Pattern containHitPattern;	
	
	
	public void clear() {
		
		if(accurateHitWordsMap != null) {
			accurateHitWordsMap.clear();
		}
		
		if(containHitWordsMap != null) {
			containHitWordsMap.clear();
		}
		
		if(fuzzyHitWordsMap != null) {
			fuzzyHitWordsMap.clear();
		}
		
		containHitPattern = null;
	}

	

	public Map<String, ShieldInfo> getAccurateHitWordsMap() {
		return accurateHitWordsMap;
	}



	public void setAccurateHitWordsMap(Map<String, ShieldInfo> accurateHitWordsMap) {
		this.accurateHitWordsMap = accurateHitWordsMap;
	}



	public Map<String, ShieldInfo> getContainHitWordsMap() {
		return containHitWordsMap;
	}

	public void setContainHitWordsMap(Map<String, ShieldInfo> containHitWordsMap) {
		this.containHitWordsMap = containHitWordsMap;
	}

	public Map<String, ShieldInfo> getFuzzyHitWordsMap() {
		return fuzzyHitWordsMap;
	}

	public void setFuzzyHitWordsMap(Map<String, ShieldInfo> fuzzyHitWordsMap) {
		this.fuzzyHitWordsMap = fuzzyHitWordsMap;
	}
	
	public Pattern getContainHitPattern() {
		if(containHitPattern == null) {
			containHitPattern = ShieldUtil.listToRegex(containHitWordsMap.values());
		}

		return containHitPattern;
	}


}
