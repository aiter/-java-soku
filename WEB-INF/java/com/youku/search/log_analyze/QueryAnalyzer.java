package com.youku.search.log_analyze;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.youku.search.log_analyze.base.BaseLineParser;
import com.youku.search.log_analyze.query.Query;

public class QueryAnalyzer {

	public static class Param {
		public String input_file;
		public String output_file;
	}

	private static final int MIN_RESULT_VALUE = 1;

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.println("需要2个参数：input_file output_file");
			System.exit(1);
		}

		Param param = new Param();
		param.input_file = args[0];
		param.output_file = args[1];

		analyze(param);
	}

	private static void analyze(Param param) throws Exception {

		BufferedReader reader = null;
		BufferedWriter writer = null;

		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(param.input_file), "utf8"));

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(param.output_file), "utf8"));

			do_analyze(reader, writer);

		} finally {
			if (reader != null) {
				reader.close();
			}

			if (writer != null) {
				writer.close();
			}
		}
	}

	private static void do_analyze(BufferedReader reader, BufferedWriter writer)
			throws Exception {

		System.out.println("analyze & write to file...");
		int readCount = 0;
		int analyzeCount = 0;
		int writeCount = 0;

		final int FLAG = 200 * 10000;

		Query oldQuery = null;
		for (String line = reader.readLine(); line != null; line = reader
				.readLine()) {

			readCount++;

			Query query = new Query();
			boolean success = BaseLineParser.parse(line, query);

			if (!success) {
				continue;
			}

			query.keyword = KeywordHandler.filter(query.keyword);
			if (query.keyword.equals("")) {
				continue;
			}

			analyzeCount++;

			//
			if (oldQuery == null) {
				oldQuery = query;// 第一次分析

			} else if (oldQuery.getKey().equals(query.getKey())) {// 分析的是同一个key，合并信息
				oldQuery.query_count += query.query_count;
				oldQuery.result_count = Math.max(oldQuery.result_count,
						query.result_count);

			} else {// 分析的是一个新key，把已经分析好的日志写文件
				writeCount += writeQueryData(writer, oldQuery);
				oldQuery = query;
			}

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

	private static int writeQueryData(BufferedWriter writer, Query query)
			throws Exception {

		if (query.query_count >= MIN_RESULT_VALUE) {

			query.keyword_py = KeywordHandler.convertPY(query.keyword);
			writer.write(getOutputString(query));
			writer.newLine();

			return 1;

		} else {
			return 0;
		}
	}

	private static String getOutputString(Query query) {
		StringBuilder builder = new StringBuilder();

		builder.append(query.keyword);
		builder.append("\t");

		builder.append(query.keyword_py);
		builder.append("\t");

		builder.append(query.source);
		builder.append("\t");

		builder.append(query.query_type);
		builder.append("\t");

		builder.append(query.because);
		builder.append("\t");

		builder.append(query.result_count);
		builder.append("\t");

		builder.append(query.query_count);

		return builder.toString();
	}

}
