package com.youku.search.sort;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import sun.io.CharToByteGBK;

import com.youku.search.sort.util.BaseFilter;
import com.youku.search.util.StringUtil;

/**
 * 对查询关键词进行过滤处理
 */
public class KeywordFilter {

	public static String filter(String keyword) {
		return filter(keyword, Integer.MAX_VALUE);
	}
	
	public static String filter(String keyword,boolean lowerCase) {
		return filter(keyword, Integer.MAX_VALUE,lowerCase);
	}

	public static String filter(String keyword, int maxWordCount) {
		return filter(keyword,maxWordCount,true);
	}
	
	/**
	 * 过滤keyword，去掉无效的codePoint，去掉前后空格，去掉中间连续的空格
	 * 
	 * @param keyword
	 * @param maxWordCount keyword最大字符长度，超过则截断
	 * @param lowerCase 返回的串是否需要转为小写
	 * @param filters 自定义的过滤器数组，可以不填此参数或者填0长度数组<br>
	 * @return
	 */
	public static String filter(String keyword, int maxWordCount, boolean lowerCase, BaseFilter... filters) {

		if (keyword == null || maxWordCount <= 0) {
			return "";
		}

		keyword = keyword.trim();
		if (keyword.length() == 0) {
			return "";
		}

		List<Integer> codeList = new LinkedList<Integer>();

		// 把无效的code point值过滤掉：没有定义的，不完整的
		char[] chars = keyword.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			
			for (BaseFilter filter : filters) {
				filter.doFilter(chars, i);
			}
			
			if (Character.isHighSurrogate(chars[i])) {
				if (i + 1 < chars.length
						&& Character.isLowSurrogate(chars[i + 1])) {
					int code = Character.toCodePoint(chars[i], chars[i + 1]);
					if (Character.isDefined(code)) {
						codeList.add(code);
						i++;
					}
				}
			} else if (!Character.isLowSurrogate(chars[i])) {
				if (Character.isDefined(chars[i])) {
					codeList.add(new Integer(chars[i]));
				}
			}
		}

		// 把非文本字符的code point过滤掉，把首尾空格过滤，把中间连续的空格过滤
		List<Integer> validCodePoints = new ArrayList<Integer>(codeList.size());
		int lastValidCode = -1;

		for (ListIterator<Integer> i = codeList.listIterator(); i.hasNext()
				&& validCodePoints.size() < maxWordCount;) {

			final int code = i.next();
			
			if (!Character.isISOControl(code) && !Character.isWhitespace(code)) {
				validCodePoints.add(code);
				lastValidCode = code;
				continue;
			}

			if (lastValidCode == -1 || lastValidCode == 32) {
				continue;
			}

			validCodePoints.add(32);
			lastValidCode = 32;
		}
		if (lastValidCode == 32) {
			validCodePoints.remove(validCodePoints.size() - 1);
		}

		// 构造字符串

		StringBuilder builder = new StringBuilder();
		for (Integer code : validCodePoints) {
			builder.append(Character.toChars(code));
		}

		if(lowerCase){
			return builder.toString().toLowerCase();
		}else {
			return builder.toString();
		}
	}
}
