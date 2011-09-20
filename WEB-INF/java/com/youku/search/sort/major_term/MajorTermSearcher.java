package com.youku.search.sort.major_term;

import org.json.JSONObject;

public class MajorTermSearcher {

	static MajorTermInfo info = new MajorTermInfo();

	public static JSONObject search(String keyword) {
		return info.search(keyword);
	}

	public static void update(JSONObject map) {
		info.update(map);
	}

}
