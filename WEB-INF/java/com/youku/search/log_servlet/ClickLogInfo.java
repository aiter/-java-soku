package com.youku.search.log_servlet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 视频点击log
 */
public class ClickLogInfo {

	public enum Item {
		url, // 目标url
		source, // 点击来源，取值：youku，soku
		type, // 目标类型
		keyword, // 用户提交的关键词
		because, // 查询原因
		category, // 分类
		wait, // 停留时间
		programmeid, //点击直达区的节目id
		siteid //点击直达区的站点id
	}

	Map<Item, Object> items = new HashMap<Item, Object>();

	public ClickLogInfo() {
	}

	public void set(Item item, Object value) {
		items.put(item, value);
	}

	public Object get(Item item) {
		return items.get(item);
	}

	/**
	 * 返回时间戳字符串
	 */
	public static String getNow() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return dateFormat.format(new Date());
	}

	/**
	 * 返回一条log字符串信息，不以\n结尾
	 */
	public String getLogString() {

		StringBuilder builder = new StringBuilder();

		builder.append(getNow());
		builder.append("\t");

		for (Item item : Item.values()) {
			builder.append(item.name());
			builder.append(":");
			builder.append(items.get(item));
			builder.append("\t");
		}

		builder.deleteCharAt(builder.length() - 1);

		return builder.toString();
	}

	public static ClickLogInfo parseLogString(String line) {
		if (line == null) {
			return null;
		}

		String[] items = line.split("\t");
		ClickLogInfo info = new ClickLogInfo();
		for (String i : items) {
			for (Item item : Item.values()) {
				final String prefix = item.name() + ":";
				if (i.startsWith(prefix)) {
					info.set(item, i.substring(prefix.length()));
					break;
				}
			}
		}

		if (info.items.size() == 0) {
			info = null;
		}

		return info;
	}
}