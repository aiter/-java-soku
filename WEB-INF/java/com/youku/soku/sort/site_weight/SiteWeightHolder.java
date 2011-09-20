package com.youku.soku.sort.site_weight;

import java.util.LinkedHashMap;

import com.youku.soku.manage.entity.SiteWeight;

public class SiteWeightHolder {

	LinkedHashMap<String, SiteWeight> map = new LinkedHashMap<String, SiteWeight>();

	public LinkedHashMap<String, SiteWeight> get() {
		return map;
	}

	public static String getKey(LinkedHashMap<String, SiteWeight> map) {

		if (map == null) {
			return "null";
		}

		StringBuilder builder = new StringBuilder();
		builder.append("{");
		for (String site : map.keySet()) {
			builder.append(site);
			builder.append(":");
			builder.append(map.get(site).getNormalWeight());
			builder.append("; ");
		}
		builder.append("}");
		return builder.toString();
	}

	public void update(LinkedHashMap<String, SiteWeight> map) {
		this.map = map;
	}

}
