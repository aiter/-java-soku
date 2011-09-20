package com.youku.search.log_analyze;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.youku.search.log_analyze.QueryAnalyzer.Param;
import com.youku.search.log_analyze.base.BaseLineParser;
import com.youku.search.log_analyze.turn.Turn;

public class TurnPageAnalyzer {

	public static class Param2 extends Param {
		public String output_file_2;
	}

	public static final ThreadLocal<Map<Integer, Turn>> pageMapHolder = new ThreadLocal<Map<Integer, Turn>>();

	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.out.println("需要3个参数：input_file output_file output_file2");
			System.exit(1);
		}

		Param2 param = new Param2();
		param.input_file = args[0];
		param.output_file = args[1];
		param.output_file_2 = args[2];

		analyze(param);
	}

	private static void analyze(Param2 param) throws Exception {

		BufferedReader reader = null;
		BufferedWriter writer = null;
		BufferedWriter writer2 = null;

		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(param.input_file), "utf8"));

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(param.output_file), "utf8"));

			writer2 = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(param.output_file_2), "utf8"));

			do_analyze(reader, writer, writer2);

		} finally {
			if (reader != null) {
				reader.close();
			}

			if (writer != null) {
				writer.close();
			}

			if (writer2 != null) {
				writer2.close();
			}
		}
	}

	private static void do_analyze(BufferedReader reader,
			BufferedWriter writer, BufferedWriter writer2) throws Exception {

		System.out.println("analyze & write to file...");
		int readCount = 0;
		int analyzeCount = 0;
		int writeCount = 0;

		final int FLAG = 200 * 10000;

		String oldKey = null;
		for (String line = reader.readLine(); line != null; line = reader
				.readLine()) {

			readCount++;

			Turn turn = new Turn();
			boolean success = BaseLineParser.parse(line, turn);
			if (!success) {
				continue;
			}

			turn.keyword = KeywordHandler.filter(turn.keyword);
			if (turn.keyword.equals("")) {
				continue;
			}

			analyzeCount++;

			//
			if (oldKey == null) {
				oldKey = turn.getKey();// 第一次分析

			} else if (oldKey.equals(turn.getKey())) {// 分析的是同一个key
				// pass

			} else {// 分析的是一个新key，把已经分析好的日志写文件
				writeCount += writeTurnData(writer, writer2);

				pageMapHolder.remove();
				oldKey = turn.getKey();
			}

			addToMap(turn);

			if (readCount % FLAG == 0) {
				System.out.println(readCount + " entries are read.");
				System.out.println(analyzeCount + " entries are analyzed.");
				System.out.println(writeCount + " entries are written.");
			}
		}

		System.out.println("complete!");
		System.out.println("    read:    " + readCount + " entries.");
		System.out.println("    analyze: " + analyzeCount + " entries.");
		System.out.println("    write:   " + writeCount + " entries.");
	}

	private static int writeTurnData(BufferedWriter writer,
			BufferedWriter writer2) throws Exception {

		int writeCount = 0;

		Map<Integer, Turn> pageMap = pageMapHolder.get();

		// 统计每一页的查询次数
		QueryCount queryCount = new QueryCount();
		for (Entry<Integer, Turn> entryPage : pageMap.entrySet()) {
			final Integer page = entryPage.getKey();
			final Turn turn = entryPage.getValue();

			queryCount.page_all += turn.query_count;
			switch (page) {
			case 1:
				queryCount.page_1 = turn.query_count;
				break;

			case 2:
				queryCount.page_2 = turn.query_count;
				break;

			case 3:
				queryCount.page_3 = turn.query_count;
				break;

			case 4:
				queryCount.page_4 = turn.query_count;
				break;

			default:
				queryCount.page_5 += turn.query_count;
				break;
			}
		}

		// 大于等于第5页的汇总数据
		Turn turn_5 = new Turn();
		Turn oneTurn = pageMap.values().iterator().next();
		turn_5.keyword = oneTurn.keyword;
		turn_5.query_type = oneTurn.query_type;
		turn_5.page = 5;
		turn_5.result_count = oneTurn.result_count;
		turn_5.query_count = queryCount.page_5;

		// 将汇总数据写入数据文件
		for (Entry<Integer, Turn> entryPage : pageMap.entrySet()) {

			final Integer page = entryPage.getKey();
			final Turn turn = entryPage.getValue();

			String out = getOutputString(turn, queryCount, false);

			// 数据文件1：每一页的数据
			writer.write(out);
			writer.newLine();
			writeCount++;

			// 数据文件2：第1、2、3、4页的数据
			if (page < 5) {
				out = getOutputString(turn, queryCount, true);
				writer2.write(out);
				writer2.newLine();
				writeCount++;
			}
		}

		// 数据文件2：大于等于第5页的汇总数据
		if (turn_5.query_count > 0) {
			writer2.write(getOutputString(turn_5, queryCount, true));
			writer2.newLine();
			writeCount++;
		}

		return writeCount;
	}

	private static void addToMap(Turn turn) {

		Map<Integer, Turn> pageMap = pageMapHolder.get();

		if (pageMap == null) {
			pageMap = new HashMap<Integer, Turn>();
			pageMapHolder.set(pageMap);

			pageMap.put(turn.page, turn);

		} else {
			Turn page = pageMap.get(turn.page);
			if (page == null) {
				pageMap.put(turn.page, turn);

			} else {
				page.result_count = Math.max(page.result_count,
						turn.result_count);
				page.query_count += turn.query_count;
			}
		}

		// 是否需要刷新数据？？
		if (pageMap.size() <= 1) {// 只有一条数据
			return;
		}
		// 有多条数据，找到最大值的查询结果数，然后刷新这个数据
		// 找到最大值
		int max_result_count = 0;
		for (Map.Entry<Integer, Turn> entry : pageMap.entrySet()) {
			Turn page = entry.getValue();
			max_result_count = Math.max(max_result_count, page.result_count);
		}
		// 刷新数据
		for (Map.Entry<Integer, Turn> entry : pageMap.entrySet()) {
			Turn page = entry.getValue();
			page.result_count = max_result_count;
		}
	}

	/**
	 * @param turn
	 *            某页的统计数据
	 * @param queryCount
	 *            各页的查询数统计情况
	 * @param summary
	 *            是否是输出为汇总数据；所谓汇总数据，是指大于等于第5页的数据已经合并为一条数据
	 * @return
	 */
	private static String getOutputString(Turn turn, QueryCount queryCount,
			boolean summary) {

		StringBuilder builder = new StringBuilder();

		builder.append(turn.keyword);
		builder.append("\t");

		builder.append(turn.source);
		builder.append("\t");

		builder.append(turn.query_type);
		builder.append("\t");

		builder.append(turn.page);
		builder.append("\t");

		builder.append(turn.result_count);
		builder.append("\t");

		builder.append(turn.query_count);
		builder.append("\t");

		builder.append(queryCount.page_all);
		builder.append("\t");

		StringBuilder rate = new StringBuilder();
		switch (turn.page) {
		case 1:
			rate.append(formatRate(queryCount.getRate_1()));
			rate.append("\t");
			rate.append(formatRate(queryCount.getRate_1_remain()));
			break;

		case 2:
			rate.append(formatRate(queryCount.getRate_2()));
			rate.append("\t");
			rate.append(formatRate(queryCount.getRate_2_remain()));
			break;

		case 3:
			rate.append(formatRate(queryCount.getRate_3()));
			rate.append("\t");
			rate.append(formatRate(queryCount.getRate_3_remain()));
			break;

		case 4:
			rate.append(formatRate(queryCount.getRate_4()));
			rate.append("\t");
			rate.append(formatRate(queryCount.getRate_4_remain()));
			break;

		default:
			rate.append(formatRate(queryCount.getRate_5()));
			rate.append("\t");
			rate.append(formatRate(queryCount.getRate_5_remain()));
			break;
		}

		builder.append(rate);

		// 如果是汇总数据，则最后添加一个数据项：第一页的查询数目
		if (summary) {
			builder.append("\t");
			builder.append(queryCount.page_1);
		}

		return builder.toString();
	}

	private static String formatRate(double rate) {

		Formatter formatter = new Formatter();
		formatter.format("%.2f", rate);

		return formatter.toString();
	}
}

/**
 * keyword + "\t" + query_type + "\t" + order_field 对应的统计数据
 */
class QueryCount {
	public int page_1;// 第1页的查询次数
	public int page_2;
	public int page_3;
	public int page_4;
	public int page_5;// 大于等于第5页的总查询次数
	public int page_all;// 所有页的总查询次数

	public double getRate_1() {
		return 0;
	}

	public double getRate_1_remain() {
		return 0;
	}

	/**
	 * 1 -> 2 的比例
	 */
	public double getRate_2() {
		if (page_1 == 0) {
			return 0;
		}
		return 1.0 * page_2 / page_1;
	}

	/**
	 * 1 -> 3... 的比例
	 */
	public double getRate_2_remain() {

		if (page_1 == 0) {
			return 0;
		}
		return 1.0 * (page_3 + page_4 + page_5) / page_1;
	}

	/**
	 * 1,2 -> 3 的比例
	 */
	public double getRate_3() {
		if (page_1 + page_2 == 0) {
			return 0;
		}
		return 1.0 * page_3 / (page_1 + page_2);
	}

	/**
	 * 1,2 -> 4... 的比例
	 */
	public double getRate_3_remain() {
		if (page_1 + page_2 == 0) {
			return 0;
		}
		return 1.0 * (page_4 + page_5) / (page_1 + page_2);
	}

	/**
	 * 1,2,3 -> 4 的比例
	 */
	public double getRate_4() {
		if (page_1 + page_2 + page_3 == 0) {
			return 0;
		}
		return 1.0 * page_4 / (page_1 + page_2 + page_3);
	}

	/**
	 * 1,2,3 -> 5... 的比例
	 */
	public double getRate_4_remain() {
		if (page_1 + page_2 + page_3 == 0) {
			return 0;
		}
		return 1.0 * (page_5) / (page_1 + page_2 + page_3);
	}

	/**
	 * 1,2,3,4 -> 5 的比例
	 */
	public double getRate_5() {
		if (page_1 + page_2 + page_3 + page_4 == 0) {
			return 0;
		}
		return 1.0 * page_5 / (page_1 + page_2 + page_3 + page_4);
	}

	/**
	 * 1,2,3,4 -> 6... 的比例
	 */
	public double getRate_5_remain() {
		return 0;
	}
}