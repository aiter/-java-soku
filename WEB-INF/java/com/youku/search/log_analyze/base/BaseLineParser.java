package com.youku.search.log_analyze.base;

import static com.youku.search.util.StringUtil.parseInt;
import static java.lang.String.valueOf;

import java.util.Arrays;

import com.youku.search.sort.search.LogInfo;
import com.youku.search.sort.search.LogInfo.Item;

public class BaseLineParser {

	/**
	 * 把line中的数据解析到data中，如果解析成功，返回true，否则返回false
	 */
	public static boolean parse(String line, final BaseLogData data) {

		try {
			return do_parse(line, data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * @see LogInfo
	 */
	private static boolean do_parse(String line, final BaseLogData data) {

		if (line == null) {
			return false;
		}

		LogInfo info = LogInfo.parseLogString(line);
		if (info == null) {
			return false;
		}

		data.keyword = valueOf(info.get(Item.query));
		data.source = valueOf(info.get(Item.source));
		data.query_type = valueOf(info.get(Item.type));
		data.because = valueOf(info.get(Item.because));

		data.page = parseInt(valueOf(info.get(Item.page)), 1, 1);
		data.result_count = parseInt(valueOf(info.get(Item.total_result)), 0);
		data.query_count = 1;

		return true;
	}

	private static String[] split(String line) {
		return line.split("\\t");
	}

	public static void main(String[] args) {
		String line = "东方神起\tsource:null\tvideo\tlogic:or\t1\t77043";
		System.out.println(Arrays.toString(split(line)));

		System.out.println("===============================");
		BaseLogData logData = new BaseLogData();
		System.out.println(do_parse(line, logData));
		System.out.println(logData);
	}
}
