package com.youku.search.log_analyze;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.youku.search.log_analyze.click.ClickData;

public class ClickAnalyzer {

	public static class Param {
		public String input_file;
		public String output_file;
	}

	public static final int MIN_CLICK_COUNT = 1;

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

		ClickData oldData = null;
		for (String line = reader.readLine(); line != null; line = reader
				.readLine()) {

			readCount++;

			ClickData clickData = ClickData.parse(line);

			if (clickData == null) {
				continue;
			}

			clickData.keyword = KeywordHandler.filter(clickData.keyword);
			if (clickData.keyword.equals("")) {
				continue;
			}

			analyzeCount++;

			//
			if (oldData == null) {
				oldData = clickData;// 第一次分析

			} else if (oldData.getKey().equals(clickData.getKey())) {// 分析的是同一个key，合并信息
				oldData.click_count += clickData.click_count;

			} else {// 分析的是一个新key，把已经分析好的日志写文件
				writeCount += writeData(writer, oldData);
				oldData = clickData;
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

	private static int writeData(BufferedWriter writer, ClickData clickData)
			throws Exception {

		if (clickData.click_count >= MIN_CLICK_COUNT) {

			writer.write(getOutputString(clickData));
			writer.newLine();

			return 1;

		} else {
			return 0;
		}
	}

	private static String getOutputString(ClickData clickData) {
		StringBuilder builder = new StringBuilder();

		builder.append(clickData.keyword);
		builder.append("\t");

		builder.append(clickData.source);
		builder.append("\t");

		builder.append(clickData.because);
		builder.append("\t");

		builder.append(clickData.site);
		builder.append("\t");

		builder.append(clickData.type);
		builder.append("\t");

		builder.append(clickData.click_count);

		return builder.toString();
	}

}
