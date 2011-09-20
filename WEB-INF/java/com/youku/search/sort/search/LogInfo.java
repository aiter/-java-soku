package com.youku.search.sort.search;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LogInfo {
	public enum Item {
		query, source, type, logic, order_field, order_reverse, page, cache, total_result, page_result, cost, miss, cacheKey, url, because, others,
	}

	Map<Item, Object> items = new HashMap<Item, Object>();

	public LogInfo() {
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

	public static LogInfo parseLogString(String line) {
		if (line == null) {
			return null;
		}

		String[] items = line.split("\t");
		LogInfo info = new LogInfo();
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
