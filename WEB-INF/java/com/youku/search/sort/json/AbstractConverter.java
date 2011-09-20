package com.youku.search.sort.json;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AbstractConverter {

	public static final String SUGGESTION = "suggestion";
	public static final String RELEVANT_WORDS = "relevantwords";

	static Log logger = LogFactory.getLog(AbstractConverter.class);

	private final static String pattern = "yyyy-MM-dd HH:mm:ss";

	protected static String formatDate(long time) {
		return formatDate(new Date(time));
	}

	protected static String formatDate(Date date) {
		return new SimpleDateFormat(pattern).format(date);
	}

	public static void main(String[] args) throws Exception {

	}

}
