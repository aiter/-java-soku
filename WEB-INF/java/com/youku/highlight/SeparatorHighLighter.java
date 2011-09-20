package com.youku.highlight;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.youku.search.pool.net.util.Cost;
import com.youku.search.util.StringUtil;

/**
 * 带分隔符的高亮处理器
 * 
 * @author 1verge
 * 
 */
public class SeparatorHighLighter {

	private static final String DEFAULT_HL_PREFIX="<span class=\"highlight\">";
	private static final String DEFAULT_HL_SUFFIX="</span>";
	
	public static final SeparatorHighLighter DEFAULT = new SeparatorHighLighter();
	
	private Encoder encoder = new SimpleHTMLEncoder();
	
	private char separator = ','; // 词语之间的间隔符

	private SeparatorHighLighter() {
	}

	public SeparatorHighLighter(char separator) {
		this.separator = separator;
	}

	public SeparatorHighLighter(Encoder encoder) {
		this.encoder = encoder;
	}

	public SeparatorHighLighter(Encoder encoder, char separator) {
		this.encoder = encoder;
		this.separator = separator;
	}

	public String highlighter(String sentence, String keyword) {
		return highlighter(sentence, keyword, 0, null, null);
	}
	
	/**
	 * 高亮
	 * 
	 * @param sentence
	 *            被高亮的句子
	 * @param keyword
	 *            用作高亮的词语
	 * @return 高亮后的字符串
	 */
	public String highlighter(String sentence, String keyword, String prefixHL, String suffixHL) {
		return highlighter(sentence, keyword, 0, prefixHL, suffixHL);
	}

	/**
	 * 高亮
	 * 
	 * @param sentence
	 *            被高亮的句子
	 * @param keyword
	 *            用作高亮的词语
	 * @param maxLength
	 *            分词后长度，0，为不限制
	 * @return 高亮后的字符串
	 */
	public String highlighter(String sentence, String keyword, int maxLength, String prefixHL, String suffixHL) {
		final String prefixStr = ((null == prefixHL || prefixHL.length() == 0) ? DEFAULT_HL_PREFIX : prefixHL);
		final String suffixStr = ((null == suffixHL || suffixHL.length() == 0) ? DEFAULT_HL_SUFFIX : suffixHL);
		
		if (sentence == null)
			return null;
		StringBuilder result = new StringBuilder();

		String[] keys = keyword.split(separator + "");
		ArrayList<char[]> keyslist = new ArrayList<char[]>(keys.length);
		for (String s : keys) {
			keyslist.add(s.toCharArray());
		}

		char[] chars = sentence.toCharArray();
		int len = chars.length;
		int start = 0;
		int total_length = 0;
		boolean separator_find = false;
		for (int i = 0; i < len; i++) {
			char[] terms = null;
			if (chars[i] == separator) {
				separator_find = true;

				terms = new char[i - start];
				System.arraycopy(chars, start, terms, 0, i - start);

				start = i + 1;

				boolean find = false;
				for (char[] kk : keyslist) {
					if (StringUtil.equalsIgnoreCase(kk, terms)) {
						result.append(prefixStr + encoder.encodeText(new String(terms)) + suffixStr);
						find = true;
						break;
					}
				}
				if (!find) {
					result.append(terms);
				}
				result.append(separator);

				total_length += terms.length;

				if (maxLength > 0 && total_length >= maxLength)
					break;

			}
		}
		if (separator_find) {
			if (start < len) {
				char[] terms = new char[len - start];
				System.arraycopy(chars, start, terms, 0, len - start);

				boolean find = false;
				for (char[] kk : keyslist) {
					if (StringUtil.equalsIgnoreCase(kk, terms)) {
						result.append(prefixStr + encoder.encodeText(new String(terms)) + suffixStr);
						find = true;
						break;
					}
				}
				if (!find) {
					result.append(terms);
				}
			}
		} else {
			if (StringUtils.containsIgnoreCase(keyword, sentence)) {
				result.append(prefixStr + sentence + suffixStr);
			} else
				return null;
		}
		return result.toString();
	}

	public static void main(String[] args) {
		// System.out.println(Runtime.getRuntime().maxMemory());
		// System.out.println(Runtime.getRuntime().totalMemory());
		// System.out.println(Runtime.getRuntime().freeMemory());

//		 String keyword= "MM";
//		 String str = "MM,美女";

		String cutStr = "牛 人 mike   tompkins";
		String[] keywords = StringUtils.split(cutStr, ' ');
		// ArrayList<char[]> keywords =
		// AnalyzerHighLighter.getSortedKeysList(cutStr, true);
		String keyword = StringUtils.join(keywords, ',');
		String str = "潜伏,Insidious,JAMES,tomPkins,wan,Rose,Byrne,MiKe";
//		String str = "Mike";
		
		SeparatorHighLighter hl = new SeparatorHighLighter(new SimpleHTMLEncoder());

		Cost cost = new Cost();
		String s = null;
		for (int i = 0; i < 1; i++) {
			s = hl.highlighter(str, keyword, "<div class=\"hl\">", "</div>");
		}
		System.out.println(s);
		System.out.println(cost);

	}
}
