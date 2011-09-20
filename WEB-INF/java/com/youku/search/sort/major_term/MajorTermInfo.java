package com.youku.search.sort.major_term;

import org.json.JSONObject;

public class MajorTermInfo {

	private JSONObject info = new JSONObject();

	public void update(JSONObject info) {
		this.info = info;
	}

	public JSONObject search(String keyword) {
		JSONObject tmp = info;
		if (keyword == null || tmp == null) {
			return null;
		}

		JSONObject cateObject = tmp.optJSONObject(keyword);
		return cateObject;
	}
}
