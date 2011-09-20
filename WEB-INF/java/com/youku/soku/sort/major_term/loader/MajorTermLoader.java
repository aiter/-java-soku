package com.youku.soku.sort.major_term.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.json.JSONObject;

public class MajorTermLoader {

	static Log logger = LogFactory.getLog(MajorTermLoader.class);

	public static JSONObject load() {

		File file = new File(Constant.dir, Constant.file);
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));

			StringBuilder builder = new StringBuilder();
			for (String line = reader.readLine(); line != null; line = reader
					.readLine()) {
				builder.append(line);
			}

			JSONObject json = new JSONObject(builder.toString());

			if (logger.isDebugEnabled()) {
				logger.debug("读取到的数据: \n" + json.toString(4));
			}

			return json;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			close(reader);
		}

		return null;
	}

	private static void close(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public static void main(String[] args) throws Exception {

		BasicConfigurator.configure();

		JSONObject jsonObject = load();
		System.out.println(jsonObject.toString(4));
	}

}
