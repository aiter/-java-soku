package com.youku.search.log_analyze.click;

import static java.lang.String.valueOf;

import com.youku.search.log_servlet.ClickLogInfo;
import com.youku.search.log_servlet.ClickLogInfo.Item;

public class ClickData {

	// log中的数据项
	public String keyword;// 关键词
	public String source;// 来源
	public String because;// 查询原因
	public String site;// 点击链接所属的站点
	public String type;// 数据类型、查询类型

	// 统计出来的数据项
	public int click_count;// 点击次数

	/**
	 * 用于汇总数据的主键
	 */
	public String getKey() {
		return keyword + "\t" + source + "\t" + because + "\t" + site + "\t"
				+ type;
	}

	public static ClickData parse(String line) {
		try {
			return parseMe(line);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	static ClickData parseMe(String line) {
		if (line == null) {
			return null;
		}

		ClickLogInfo logInfo = ClickLogInfo.parseLogString(line);
		if (logInfo == null) {
			return null;
		}

		ClickData data = new ClickData();
		data.keyword = valueOf(logInfo.get(Item.keyword));
		data.source = valueOf(logInfo.get(Item.source));
		data.because = valueOf(logInfo.get(Item.because));
		data.site = valueOf(logInfo.get(Item.url));
		data.type = valueOf(logInfo.get(Item.type));
		data.click_count = 1;

		return data;
	}
}
