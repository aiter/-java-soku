package com.youku.search.spider.mdbchina.entity;

import java.util.ArrayList;
import java.util.List;

public class TVDrama {
	public String name;
	public String name_en;
	public List<String> alias = new ArrayList<String>();
	public int count;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("name: " + name + "; ");
		builder.append("name_en: " + name_en + "; ");
		builder.append("alias: " + alias + "; ");
		builder.append("count: " + count + "; ");

		return builder.toString();
	}
}
