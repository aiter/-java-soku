package com.youku.search.hanyupinyin;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

public class Converter {

	private static Map<Character, String> map = new ConcurrentHashMap<Character, String>();
	private static final HanyuPinyinOutputFormat format;

	static {
		format = new HanyuPinyinOutputFormat();

		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
	}

	/**
	 * 将输入的字符串转化为拼音，不能转的保持不变
	 */
	public static String convert(String input) {
		return convert(input, false);
	}

	/**
	 * 将输入的字符串转化为拼音，不能转的保持不变；最后，如果toLowerCase为true，再将结果转化为小写。
	 */
	public static String convert(String input, boolean toLowerCase) {

		String result = doConvert(input);

		if (toLowerCase) {
			return result.toLowerCase();
		}

		return result;
	}

	private static String doConvert(String input) {

		char[] chars = input.toCharArray();

		StringBuilder builder = new StringBuilder();
		for (char c : chars) {

			if (map.containsKey(c)) {
				builder.append(map.get(c));
				continue;
			}

			String py = c + "";
			try {
				String[] s = PinyinHelper.toHanyuPinyinStringArray(c, format);
				if (s != null && s.length > 0) {
					py = s[0];
					map.put(c, py);
				}
			} catch (Exception e) {
			}
			builder.append(py);
		}

		return builder.toString();
	}

	public static Set<String> convertReturnFirstLetter(String input,
			boolean toLowerCase) {

		if (StringUtils.isBlank(input))
			return null;

		char[] chars = input.toCharArray();
		char c = chars[0];

		String result = "";
		StringBuilder builder = new StringBuilder();
		if (c > 128) {
			try {
				String[] s = PinyinHelper.toHanyuPinyinStringArray(c,format);
				if (s != null && s.length > 0) {
					for (String a : s) {
						if (!StringUtils.isBlank(a)) {
							builder.append(a.charAt(0));
						}
					}
				}
			} catch (Exception e) {
			}
		}else
			builder.append(c);

		result = builder.toString();
		
		Set<String> letters = new HashSet<String>();
		char[] cs = result.toCharArray();
		for(char cc:cs){
			if(toLowerCase)
				letters.add((""+cc).toLowerCase());
			else
				letters.add(""+cc);
		}
		return letters;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(convertReturnFirstLetter("谁都有秘密ABCD", true));
		System.out.println(convertReturnFirstLetter("谁都有秘密ABCD", false));
		System.out.println(convertReturnFirstLetter("uns", true));
	}
}
