package com.youku.search.sort.servlet.search_page.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import sun.misc.BASE64Encoder;

import com.youku.search.sort.servlet.util.WebUtils;

public class MiscUtils {

	public static String timeLength(String length) {
		double len = 0;

		try {
			len = Double.parseDouble(length);
		} catch (Exception e) {
		}

		return timeLength((long) len);
	}

	public static String timeLength(long length) {

		if (length < 0) {
			return "00:00";
		}

		StringBuilder builder = new StringBuilder();

		long seconds = length % 60;

		length = length / 60;
		long minutes = length % 60;

		length = length / 60;
		long hours = length;

		if (hours > 0) {
			builder.append(hours);
			builder.append(":");
		}

		if (minutes < 10) {
			builder.append("0");
		}
		builder.append(minutes);
		builder.append(":");

		if (seconds < 10) {
			builder.append("0");
		}
		builder.append(seconds);

		return builder.toString();
	}

	public static String size(int bytes) {
		if (bytes <= 0) {
			return "0KB";
		}

		String pattern = "##.##";
		DecimalFormat format = new DecimalFormat(pattern);

		double result;
		if (bytes >= (1024 * 1024)) {
			// MB
			result = bytes * 1.0 / 1024 / 1024;
			return format.format(result) + "MB";
		} else {
			// KB
			result = bytes * 1.0 / 1024;
			return format.format(result) + "KB";
		}
	}

	public static String createTime(String input) {
		try {
			String pattern = "yyyy-MM-dd HH:mm:ss";
			Date date = new SimpleDateFormat(pattern).parse(input);

			long dateSeconds = date.getTime() / 1000;
			long nowSeconds = System.currentTimeMillis() / 1000;
			long diff = nowSeconds - dateSeconds;
			if (diff < 0) {
				return "未知";
			}

			long unit = 60;// 60秒
			if (diff / unit == 0) {
				return (diff % unit) + "秒钟前";
			}

			diff = diff / 60;// 分钟数
			unit = 60;// 60分钟
			if (diff / unit == 0) {
				return (diff % unit) + "分钟前";
			}

			diff = diff / 60;// 小时数
			unit = 24;// 24小时
			if (diff / unit == 0) {
				return (diff % unit) + "小时前";
			}

			diff = diff / 24;// 天数
			unit = 30;// 30天
			if (diff / unit == 0) {
				return (diff % unit) + "天前";
			}

			diff = diff / 30;// 月数
			unit = 12;// 12月
			if (diff / unit == 0) {
				return (diff % unit) + "月前";
			}

			return (diff / unit) + "年前";

		} catch (Exception e) {
			return "未知";
		}
	}

	public static String rssCreateTimeTime(String input) {
		try {
			String pattern = "yyyy-MM-dd HH:mm:ss";
			Date date = new SimpleDateFormat(pattern).parse(input);

			return WebUtils.formatRssDate(date);

		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 判断视频是否为高清
	 */
	public static boolean hd(String ftype) {
		if (ftype == null) {
			return false;
		}

		String[] ftypes = ftype.split(",");
		for (String f : ftypes) {
			if ("1".equals(f.trim())) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * 判断视频是否为超清
	 */
	public static boolean shd(String ftype) {
		if (ftype == null) {
			return false;
		}

		String[] ftypes = ftype.split(",");
		for (String f : ftypes) {
			if ("6".equals(f.trim())) {
				return true;
			}
		}

		return false;
	}


	public static String userid_encode(String id) {
		try {
			long idValue = Long.parseLong(id);
			idValue = idValue << 2;
			BASE64Encoder encoder = new BASE64Encoder();
			String result = encoder.encode(String.valueOf(idValue).getBytes());
			result = "U" + result;
			return result;

		} catch (Exception e) {
			return "";
		}
	}

	public static String barid_encode(String id) {
		try {
			long idValue = Long.parseLong(id);
			idValue = idValue << 4;
			BASE64Encoder encoder = new BASE64Encoder();
			String result = encoder.encode(String.valueOf(idValue).getBytes());
			result = "D2" + result;
			return result;

		} catch (Exception e) {
			return "";
		}
	}

	public static void main(String[] args) {
		System.out.println(barid_encode("379"));
	}

}
