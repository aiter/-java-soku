package com.youku.soku.manage.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ShieldSiteConstants {
	
	public static final int BLACKLISTS_SITE = 1;
	
	public static final int WHITELISTS_SITE = 2;
	
	public static final int NORMAL_SITE = 3;
	
	
	public static final int SITE_LEVEL_ONE = 1;
	
	public static final int SITE_LEVEL_TWO = 2;
	
	public static final int SITE_LEVEL_THREE = 3;
	
	public static Map<Integer, String> SITELEVELMAP;
	
	static {
		Map<Integer, String> siteLevelMap = new LinkedHashMap<Integer, String>();
		siteLevelMap.put(-1, "所有");
		siteLevelMap.put(SITE_LEVEL_ONE, "1级");
		siteLevelMap.put(SITE_LEVEL_TWO, "2级");
		siteLevelMap.put(SITE_LEVEL_THREE, "3级");
		
		SITELEVELMAP = Collections.unmodifiableMap(siteLevelMap);
	}
	
	public static Map<Integer, String> RADIOSITELEVELMAP;
	
	static {
		Map<Integer, String> radioSiteLevelMap = new LinkedHashMap<Integer, String>();
		radioSiteLevelMap.put(SITE_LEVEL_ONE, "1级");
		radioSiteLevelMap.put(SITE_LEVEL_TWO, "2级");
		radioSiteLevelMap.put(SITE_LEVEL_THREE, "3级");
		
		RADIOSITELEVELMAP = Collections.unmodifiableMap(radioSiteLevelMap);
	}
	
	public static Map<Integer, String> SITECATEGORYMAP;
	
	static {
		Map<Integer, String> siteCategoryMap = new LinkedHashMap<Integer, String>();
		siteCategoryMap.put(-1, "所有");
		siteCategoryMap.put(BLACKLISTS_SITE, "黑名单");
		siteCategoryMap.put(WHITELISTS_SITE, "白名单");
		siteCategoryMap.put(NORMAL_SITE, "一般站点");
		
		SITECATEGORYMAP = Collections.unmodifiableMap(siteCategoryMap);
	}
	
	public static Map<Integer, String> RADIOSITECATEGORYMAP;
	
	static {
		Map<Integer, String> radioSiteCategoryMap = new LinkedHashMap<Integer, String>();
		radioSiteCategoryMap.put(BLACKLISTS_SITE, "黑名单");
		radioSiteCategoryMap.put(WHITELISTS_SITE, "白名单");
		radioSiteCategoryMap.put(NORMAL_SITE, "一般站点");
		
		RADIOSITECATEGORYMAP = Collections.unmodifiableMap(radioSiteCategoryMap);
	}
	
	public static Map<String, Integer> SITECATEGORYVALUEMAP;
	
	static {
		Map<String, Integer> siteCategoryValueMap = new HashMap<String, Integer>();
		siteCategoryValueMap.put("白名单", WHITELISTS_SITE);
		siteCategoryValueMap.put("黑名单", BLACKLISTS_SITE);
		siteCategoryValueMap.put("一般站点", NORMAL_SITE);
		
		SITECATEGORYVALUEMAP = Collections.unmodifiableMap(siteCategoryValueMap);
	}
	
	public static Map<String, Integer> SITELEVELVALUEMAP;
	
	static {
		Map<String, Integer> siteLevelValueMap = new HashMap<String, Integer>();
		siteLevelValueMap.put("1级", SITE_LEVEL_ONE);
		siteLevelValueMap.put("2级", SITE_LEVEL_TWO);
		siteLevelValueMap.put("3级", SITE_LEVEL_THREE);
		
		SITELEVELVALUEMAP = Collections.unmodifiableMap(siteLevelValueMap);
	}
	
}
