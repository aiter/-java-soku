package com.youku.search.spider.mdbchina;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.spider.mdbchina.entity.SimpleTVDrama;

public class Writer {

	static Log logger = LogFactory.getLog(Writer.class);

	public static void write(List<SimpleTVDrama> list, String dest)
			throws Exception {

		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(dest, true), "utf-8"));

			for (SimpleTVDrama simpleTVDrama : list) {
				writer.write(simpleTVDrama.name);
				writer.newLine();
			}

		} finally {
			if (writer != null) {
				writer.close();
			}
		}

	}

}
