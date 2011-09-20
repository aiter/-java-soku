package com.youku.search.spider.mdbchina;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainOfTVDramaIDNamePostHandle {

	public static void main(String[] args) throws Exception {
		String srcFileName = "/home/jiabaozhen/tmp/spider/simple_tv_drama_name.txt";
		String destFileName = "/home/jiabaozhen/tmp/spider/simple_tv_drama_name_processed.txt";
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(srcFileName), "utf-8"));

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(destFileName), "utf-8"));

			Set<String> names = new HashSet<String>();
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}

				String name = line.trim();
				if (name.length() == 0) {
					continue;
				}

				String regex = ".*(第[\\d一二三四五六七八九拾零]+季).*";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(name);

				if (matcher.matches()) {
					System.out.println("matchs: " + name);
					name = name.substring(0, matcher.start(1));
					System.out.println("matchs: new: " + name);
				}

				names.add(name);
			}

			for (String name : names) {
				writer.write(name);
				writer.newLine();
			}

		} finally {
			if (reader != null) {
				reader.close();
			}
			if (writer != null) {
				writer.close();
			}
		}

	}
}
