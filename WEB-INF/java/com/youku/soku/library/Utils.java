package com.youku.soku.library;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.analyzer.Number;
import com.youku.search.analyzer.WordProcessor;
import com.youku.search.util.DataFormat;
import com.youku.search.util.Request;

public class Utils {

	private static Set<Character> numberSet = Number.numberSet;
	private static HashMap<String, String> numberMap = Number.numberMap;

	public static int getSeriesCates(Map<String, Integer> cateMap, String cates) {
		if (StringUtils.isBlank(cates))
			return 0;
		cates = cates.trim().intern();
		return DataFormat.parseInt(cateMap.get(cates), 0);
	}

	public static boolean isHd(JSONArray jarr) {
		String streamtypes = parseToStr(jarr);
		if (null != streamtypes) {
			String[] types = streamtypes.split("\\|");
			if (null != types) {
				for (String type : types) {
					if (!StringUtils.isBlank(type)
							&& type.trim().toLowerCase().equalsIgnoreCase("hd"))
						return true;
				}
			}
		}
		return false;
	}

	public static String parsePersonToStr(JSONArray ja) {
		if (ja != null) {
			StringBuilder str = new StringBuilder();
			JSONObject json = null;
			for (int i = 0; i < ja.length(); i++) {
				json = ja.optJSONObject(i);
				if (null != json) {
					if (str.length() > 0 && '|' != str.charAt(str.length() - 1))
						str.append("|");
					str.append(json.optString("name"));
					str.append(":");
					str.append(json.optString("type"));
				}
			}
			return str.length() > 0 ? str.toString() : null;
		}
		return null;
	}

	public static String parseToStr(JSONArray ja) {
		if (ja != null) {
			StringBuilder str = new StringBuilder();
			for (int i = 0; i < ja.length(); i++) {
				if (!ja.isNull(i)) {
					if (str.length() > 0 && '|' != str.charAt(str.length() - 1))
						str.append("|");
					str.append(ja.optString(i));
				}
			}
			return str.length() > 0 ? str.toString() : null;
		}
		return null;
	}

	public static String buildYoukuUrl(String encorder_id) {
		StringBuilder url = new StringBuilder("http://v.youku.com/v_show/id_");
		url.append(encorder_id);
		url.append(".html");
		return url.toString();
	}

	public static List<String> parseStr2List(String str, String split) {
		List<String> alias = new ArrayList<String>();
		String[] aliasArr = str.split(split);
		if (null != aliasArr) {
			for (String a : aliasArr) {
				if (!StringUtils.isBlank(a))
					alias.add(a.trim());
			}
		}
		if (alias.size() > 0)
			return alias;
		else
			return null;
	}

	public static Set<String> parseStr2Set(String str, String split) {
		Set<String> alias = new HashSet<String>();
		String[] aliasArr = str.split(split);
		if (null != aliasArr) {
			for (String a : aliasArr) {
				if (!StringUtils.isBlank(a))
					alias.add(a.trim());
			}
		}
		if (alias.size() > 0)
			return alias;
		else
			return null;
	}

	public static void appendToFile(String fileName, String s) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fileName, true), "utf-8"));
			bw.write(s);
			bw.write("\r\n");
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception ie) {
				}
			}
		}
	}

	private static JSONObject buildJson(String res) {
		JSONObject json = null;
		try {
			json = new JSONObject(res);
		} catch (JSONException e) {
			e.printStackTrace();
			System.err.println("errjson:" + res);
		}
		return json;
	}

	public static JSONObject requestGet(String url) {
		try {
			String res = Request.requestGet(url);
			return Utils.buildJson(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static long getDelayTime(long d, double hour, boolean f) {
		Calendar calCurrent = Calendar.getInstance();
		long now;
		if (f)
			now = calCurrent.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000
					+ calCurrent.get(Calendar.MINUTE) * 60 * 1000
					+ calCurrent.get(Calendar.SECOND) * 1000;
		else
			now = calCurrent.get(Calendar.HOUR) * 60 * 60 * 1000
					+ calCurrent.get(Calendar.MINUTE) * 60 * 1000
					+ calCurrent.get(Calendar.SECOND) * 1000;
		long run = (long) (hour * 60 * 60 * 1000);

		if (run < now) {
			run = d - (now - run);
		} else {
			run = run - now;
		}
		return run;
	}

	public static String parse2Str(List<String> list) {
		if (null == list || list.size() == 0)
			return null;
		StringBuilder strs = new StringBuilder();
		int i = 0;
		for (String str : list) {
			if (!StringUtils.isBlank(str)) {
				if (i != 0)
					strs.append("|");
				strs.append(str.trim());
				i++;
			}
		}
		if (strs.length() > 0)
			return strs.toString();
		else
			return null;
	}

	public static String parse2Str(Set<String> set) {
		if (null == set || set.size() == 0)
			return null;
		StringBuilder strs = new StringBuilder();
		int i = 0;
		for (String str : set) {
			if (!StringUtils.isBlank(str)) {
				if (i != 0)
					strs.append("|");
				strs.append(str.trim());
				i++;
			}
		}
		if (strs.length() > 0)
			return strs.toString();
		else
			return null;
	}

	public static String formatTeleplayName(String title) {
		return title
				.replaceAll(
						"((中字|中文字幕|英文原声|中英文|中英双语|中英双字幕|双语字幕|国英|双语|国语|日语|韩语|汉语|无字幕|字幕|DVD|中文高清|高清|清晰|VCD)+版*)|抢先看|美剧|日剧|韩剧|偶像剧|连续剧|电视剧|电影",
						"");
	}

	public static Date formatDate(String strDate) {
		SimpleDateFormat fmtDate = null;
		fmtDate = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return fmtDate.parse(strDate);
		} catch (ParseException e) {
		}
		fmtDate = new SimpleDateFormat("yyyy_MM_dd");
		try {
			return fmtDate.parse(strDate);
		} catch (ParseException e) {
		}
		fmtDate = new SimpleDateFormat("yyyy/MM/dd");
		try {
			return fmtDate.parse(strDate);
		} catch (ParseException e) {
		}
		fmtDate = new SimpleDateFormat("yyyy.MM.dd");
		try {
			return fmtDate.parse(strDate);
		} catch (ParseException e) {
		}
		fmtDate = new SimpleDateFormat("yyyy年MM月dd日");
		try {
			return fmtDate.parse(strDate);
		} catch (ParseException e) {
		}
		try {
			return new Date(strDate);
		} catch (Exception e) {

		}
		System.out.println("日期格式化错误：" + strDate);
		return null;
	}

	public static String analyzer(String word) {
		if (null == word)
			return "";
		else {
			String[] arr = WordProcessor.analyzerPrepare(word);
			if (null != arr) {
				return trimNUll(arr[0]) + trimNUll(arr[1]);
			} else
				return "";
		}
	}

	public static String analyzerForSearch(String word) {
		if (null == word)
			return "";
		else {
			String[] arr = analyzerPrepare(word);
			if (null != arr) {
				return trimNUll(arr[0]) + trimNUll(arr[1]);
			} else
				return "";
		}
	}

	public static String trimNUll(String word) {
		if (null == word)
			return "";
		else
			return word.trim();
	}

	public static String[] analyzerPrepare(String s) {
		if (s == null)
			return null;

		char[] array = s.toCharArray();

		int len = array.length;
		StringBuffer sb = null;
		StringBuffer sb_not_analyze = null;
		int last = 0;
		try {
			for (int i = 0; i < len; i++) {
				if (i > 0 && array[i] == '集') {
					if (numberSet.contains(array[i - 1])) {
						for (int j = i - 1; j >= last; j--) {
							if (!numberSet.contains(array[j])) {
								if (array[j] == ' ' || array[j] == '第') {
									if (sb == null)
										sb = new StringBuffer();
									String n = new String(array, j + 1, i - j
											- 1);
									String k = numberMap.get(n);
									if (k == null)
										k = n;
									sb.append(array, last, j - last).append("")
											.append(k).append("");
								} else {
									if (sb == null)
										sb = new StringBuffer();
									String n = new String(array, j + 1, i - j
											- 1);
									String k = numberMap.get(n);
									if (k == null)
										k = n;
									sb.append(array, last, j - last + 1)
											.append("").append(k).append("");
								}
								last = i + 1;
								break;
							} else
								continue;
						}
					} else
						continue;
				} else if (i > 0 && (array[i] == '季' || array[i] == '部')) {
					if (i == len - 1
							|| (i < len - 1 && array[i + 1] != '度' && array[i + 1] != '节')) {
						if (numberSet.contains(array[i - 1])) {
							for (int j = i - 1; j >= 0; j--) {
								if (!numberSet.contains(array[j])) {
									if (array[j] == '第') {
										if (j > last) {
											if (sb == null)
												sb = new StringBuffer();
											sb.append(array, last, j - last);
										}
										last = i + 1;
										if (sb_not_analyze == null)
											sb_not_analyze = new StringBuffer();
										else
											sb_not_analyze.append(" ");
										String n = new String(array, j + 1, i
												- j - 1);
										String k = numberMap.get(n);
										if (k == null)
											k = n;
										sb_not_analyze.append("第").append(k)
												.append(array[i]);
									}
									break;
								}

							}
						} else
							continue;
					}

				}

				else {

				}
			}

			if (last < s.length()) {
				if (sb == null)
					sb = new StringBuffer();
				sb.append(array, last, len - last);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return new String[] {
				sb != null ? sb.toString() : sb_not_analyze != null ? null : s,
				sb_not_analyze != null ? sb_not_analyze.toString() : null };
	}

	public static String parseDomain(String url) {
		String domain = StringUtils.substringBetween(url, "://", "/");
		if (StringUtils.isBlank(domain))
			domain = StringUtils.substringBetween(url, "www.", "/");
		return domain;
	}

	public static String stopWordsFilter(String keyword) {
		Set<Character> set = com.youku.search.util.Constant.StopWords
				.getStopSet();
		StringBuilder builder = new StringBuilder();
		char[] chars = keyword.toCharArray();
		for (char c : chars) {
			if (set.contains(c)) {
				builder.append(" ");
			} else {
				builder.append(c);
			}
		}
		return builder.toString();
	}
}
