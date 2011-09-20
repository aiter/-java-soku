package com.youku.soku.sort.ext;

import java.util.Map;

public class MiscUtil {
	public static <K, V> V putIfAbsent(Map<K, V> map, K k, V v) {
		if (map.containsKey(k)) {
			return null;
		}

		return map.put(k, v);
	}
	
}
