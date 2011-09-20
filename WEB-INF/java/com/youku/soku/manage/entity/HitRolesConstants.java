package com.youku.soku.manage.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HitRolesConstants {

	public static final int ACCURATE_HITS = 1;

	public static final int CONTAINS_HITS = 2;

	public static final int FUZZY_HITS = 3;

	public static Map<Integer, String> HITROLEMAP;

	static {
		Map<Integer, String> hitRoleMap = new LinkedHashMap<Integer, String>();
		hitRoleMap.put(-1, "所有");
		hitRoleMap.put(ACCURATE_HITS, "精确命中");
		hitRoleMap.put(CONTAINS_HITS, "包含命中");
		hitRoleMap.put(FUZZY_HITS, "模糊命中");
		
		HITROLEMAP = Collections.unmodifiableMap(hitRoleMap);
	}

	public static Map<Integer, String> RADIOHITROLEMAP;

	static {
		Map<Integer, String> radioHitRoleMap = new LinkedHashMap<Integer, String>();
		radioHitRoleMap.put(ACCURATE_HITS, "精确命中");
		radioHitRoleMap.put(CONTAINS_HITS, "包含命中");
		radioHitRoleMap.put(FUZZY_HITS, "模糊命中");
		
		RADIOHITROLEMAP = Collections.unmodifiableMap(radioHitRoleMap);
	}
	
	public static Map<String, Integer> HITROLENAMEMAP = new HashMap<String, Integer>();
	
	static {
		Map<String, Integer> hitRoleNameMap = new LinkedHashMap<String, Integer>();
		hitRoleNameMap.put("精确命中", ACCURATE_HITS);
		hitRoleNameMap.put("包含命中", CONTAINS_HITS);
		hitRoleNameMap.put("模糊命中", FUZZY_HITS);
		
		HITROLENAMEMAP = Collections.unmodifiableMap(hitRoleNameMap);
	}
}
