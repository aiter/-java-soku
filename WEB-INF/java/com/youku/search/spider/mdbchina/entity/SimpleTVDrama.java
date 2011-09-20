package com.youku.search.spider.mdbchina.entity;

public class SimpleTVDrama {
	public String id;
	public String name;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("id: " + id + "; ");
		builder.append("name: " + name + "; ");

		return builder.toString();
	}
}
