package com.youku.search.util.boost.util;

public class BadWords {
	public static boolean isBad(String s) {
		if (s == null) {
			return false;
		}

		if (s.contains("http://") || s.contains("www.") || s.contains("观看地址")
				|| s.contains("观看网址")) {
			return true;
		}

		return false;
	}
}
