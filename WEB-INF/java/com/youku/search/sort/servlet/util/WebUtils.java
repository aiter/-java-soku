package com.youku.search.sort.servlet.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.JavaScriptUtils;

public class WebUtils {

	static Log logger = LogFactory.getLog(WebUtils.class);

	public static String htmlEscape(String input) {
		return htmlEscape(input, -1, "");
	}

	public static String htmlEscape(String input, int max_len, String surfix) {

		if (input == null || max_len == 0) {
			return "";
		}

		surfix = surfix == null ? "" : surfix;

		int origin_len = input.length();
		int end = max_len < 0 ? origin_len : Math.min(max_len, origin_len);

		if (end < origin_len) {
			input = input.substring(0, end) + surfix;
		}

		try {
			return HtmlUtils.htmlEscape(input);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return input;
	}

	public static String jsEscape(String input) {
		if (input == null) {
			return "";
		}

		try {
			return JavaScriptUtils.javaScriptEscape(input);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return input;
	}

	public static String urlEncode(String input) {
		return urlEncode(input, -1, "utf-8");
	}

	public static String urlEncode(String input, int max_len) {
		return urlEncode(input, max_len, "utf-8");
	}

	public static String urlEncode(String input, String charset) {
		return urlEncode(input, -1, charset);
	}

	public static String urlEncode(String input, int max_len, String charset) {
		if (input == null || max_len == 0) {
			return "";
		}

		int end = max_len < 0 ? input.length() : Math.min(max_len, input
				.length());
		input = input.substring(0, end);

		try {
			return URLEncoder.encode(input, charset);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return input;
	}

	public static String urlDecode(String input) {
		return urlDecode(input, "UTF-8");
	}

	public static String urlDecode(String input, String charset) {
		if (input == null) {
			return "";
		}

		try {
			return URLDecoder.decode(input, charset);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return input;
	}

	/**
	 * 为了简化页面输出
	 */
	public static String eq(Object left, Object right, Object true_out,
			Object false_out) {

		boolean equals = eq(left, right);
		return equals ? String.valueOf(true_out) : String.valueOf(false_out);
	}

	public static String not_eq(Object left, Object right, Object true_out,
			Object false_out) {

		boolean equals = eq(left, right);
		return !equals ? String.valueOf(true_out) : String.valueOf(false_out);
	}

	public static boolean eq(Object left, Object right) {

		boolean equals = (left == null && (right == null || right.equals(left)))
				|| (left != null && left.equals(right));

		return equals;
	}

	public static String formatNumber(Number number, String pattern) {
		if (number == null) {
			number = 0;
		}

		if (pattern == null) {
			return String.valueOf(number);
		}

		try {
			DecimalFormat format = new DecimalFormat(pattern);
			return format.format(number.doubleValue());
		} catch (Exception e) {
			return String.valueOf(number);
		}
	}

	public static String formatDate(Date date, String pattern) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			return format.format(date);
		} catch (Exception e) {
			return "";
		}
	}

	public static String formatRssDate(Date date) {
		String pattern = "EEE, dd MMM yyyy HH:mm:ss Z";
		return formatDate(date, pattern);
	}

	public static void main(String[] args) {
		System.out
				.println(formatDate(new Date(), "EEE, dd MMM yyyy HH:mm:ss Z"));
	}

}
