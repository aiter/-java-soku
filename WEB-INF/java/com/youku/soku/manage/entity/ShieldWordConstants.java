package com.youku.soku.manage.entity;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ShieldWordConstants {
	
	public final static int SHIELDWORDTYPE = 1;

	public final static int DETERMINERWORDTYPE = 2;
	
	public final static int BOTHWORDTYPE = 3;
	
	public final static String MAILVALIDATECODE = "sokushieldviewlogmailcode"; 
	
	public final static int KEYWORDORDER = 1;
	
	public final static int UPDATETIMEORDER = 2;
	
	public final static int MODIFIERORDER = 3;
	
	public static Map<Integer, String> ORDERBYMAP;
	
	static {
		Map<Integer, String> orderbyMap = new LinkedHashMap<Integer, String>();
		orderbyMap.put(UPDATETIMEORDER, "更新时间");
		orderbyMap.put(KEYWORDORDER, "关键字");		
		orderbyMap.put(MODIFIERORDER, "修改人员");
		
		ORDERBYMAP = Collections.unmodifiableMap(orderbyMap);
	}
	
	public final static int ASCEDING = 1;
	
	public final static int DESCEDING = 2;
	
	public static Map<Integer, String> TRENDMAP;
	
	static {
		Map<Integer, String> trendMap = new LinkedHashMap<Integer, String>();
		trendMap.put(ASCEDING, "升序");
		trendMap.put(DESCEDING, "降序");
		
		TRENDMAP = Collections.unmodifiableMap(trendMap);
	}
}
