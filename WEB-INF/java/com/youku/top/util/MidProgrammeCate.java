package com.youku.top.util;

public enum MidProgrammeCate {
	电视剧("teleplay"), 电影("movie"), 综艺("variety"), 动漫("anime"), 体育("sports"), 教育(
			"science");

	private String value;

	MidProgrammeCate(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
