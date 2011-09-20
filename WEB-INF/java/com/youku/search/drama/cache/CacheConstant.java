package com.youku.search.drama.cache;

import com.youku.search.drama.Drama;

public class CacheConstant {

	private static final String KEY_PREFIX = "DRAMA_CACHE_KEY_";

	public static final String CACHE_KEY_DRAMA = KEY_PREFIX + "DRAMA";
	public static final String CACHE_KEY_ZONGYI = KEY_PREFIX + "ZONGYI";

	public static final int CACHE_SECONDS = 3600 * 3;// 3个小时

	public static String CACHE_KEY(Drama drama) {
		return CACHE_KEY(drama.getType());
	}

	public static String CACHE_KEY(Drama.Type type) {
		switch (type) {
		case DRAMA:
			return CACHE_KEY_DRAMA;

		case ZONGYI:
			return CACHE_KEY_ZONGYI;

		default:
			throw new IllegalArgumentException();
		}
	}
}
