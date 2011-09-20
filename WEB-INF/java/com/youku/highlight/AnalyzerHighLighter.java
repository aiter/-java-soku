package com.youku.highlight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.youku.search.pool.net.util.Cost;
import com.youku.search.util.StringUtil;

/**
 * 先分词后高亮的分词处理器
 * 
 * @author 1verge
 * 
 */
public class AnalyzerHighLighter {

	public static final AnalyzerHighLighter DEFAULT = new AnalyzerHighLighter();

	private HtmlFormatter formatter = new SimpleHTMLFormatter();
	private Encoder encoder = new SimpleHTMLEncoder();

	private char separator = ' '; // 词语之间的间隔符
	private static final String separatorS = " ";

	private AnalyzerHighLighter() {

	}

	public AnalyzerHighLighter(char separator) {
		this.separator = separator;
	}

	public AnalyzerHighLighter(HtmlFormatter formatter, Encoder encoder) {
		this.formatter = formatter;
		this.encoder = encoder;
	}

	public AnalyzerHighLighter(HtmlFormatter formatter, Encoder encoder,
			char separator) {
		this.formatter = formatter;
		this.encoder = encoder;
		this.separator = separator;
	}

	/**
	 * 得到用作高亮的keywordList <br>
	 * 
	 * @param keyword
	 *            用作高亮的词语，已分词
	 * @param isSort
	 *            如果设置为true，则返回的List会按照keyword的长度倒序排序 <br>
	 * @return
	 */
	public static ArrayList<char[]> getSortedKeysList(String keyword,
			boolean isSort) {
		String[] keys = keyword.split(separatorS);
		ArrayList<char[]> keyslist = new ArrayList<char[]>(keys.length);
		for (String s : keys) {
			s = s.trim();
			if (s.length() > 0) {
				keyslist.add(s.toCharArray());
			}
		}

		if (isSort) {
			Collections.sort(keyslist, new Comparator<char[]>() {
				@Override
				public int compare(char[] o1, char[] o2) {
					return -1 * (new Integer(o1.length).compareTo(o2.length));
				}
			});
		}

		return keyslist;
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
	public AnalyzerHighLighterResult highlighter(String sentence, String keyword) {
		return highlighter(sentence, keyword, 0);
	}
	
	public String highlighter(String sentence, ArrayList<char[]> keyslist){
		return highlighter(sentence, keyslist, 0, false);
	}
	
	/**
	 * 高亮
	 * 
	 * @param sentence 被高亮的句子，没有分词
	 * @param keyslist 用作高亮的keywordList，为了防止keyword被最小匹配，需要先倒序排序
	 * @param maxLength 最大高亮长度，0为不限制
	 * @param isMatchMaxLength 如果为false，则允许长度超出maxLength的高亮词出现（只有 maxLength>0 时此配置才有效）
	 * 
	 * @return 高亮后的字符串
	 * @author gaosong
	 */
	public String highlighter(String sentence, ArrayList<char[]> keyslist,
			int maxLength, boolean isMatchMaxLength) {
		if (null == sentence || sentence.length() == 0) {
			return null;
		}
		if (null == keyslist || keyslist.size() == 0) {
			return sentence;
		}

		int maxKeyLen = 0;
		if (maxLength > 0) {
			for (char[] key : keyslist) {
				if (key.length > maxKeyLen) {
					maxKeyLen = key.length;
				}
			}
		}

		StringBuilder lightText = new StringBuilder();

		char[] chars = sentence.toCharArray();
		int len = chars.length;
		int span = 1;
		int total_length = 0;
		boolean light_over = false;
		char[] terms = null;
		boolean findOne = false;

		int start = 0;
		for (; start < len; start += span) {
			terms = null;
			boolean find = false;

			// 已经到达最大高亮长度，结束匹配
			if (light_over) {
				break;
			}

			for (char[] key : keyslist) {
				int keyLen = key.length;
				// 如果key长度大于sentence的剩余长度则跳过这个key
				if (keyLen > len - start) {
					continue;
				}
				// 如果限制最大高亮长度，并且当前key如果匹配则超过最大高亮长度
				// 则直接跳过这个key
				if (isMatchMaxLength && maxLength > 0 && keyLen > maxLength - start) {
					continue;
				}

				terms = new char[keyLen];
				System.arraycopy(chars, start, terms, 0, keyLen);
				if (StringUtil.equalsIgnoreCase(key, terms)) {
					lightText.append(formatter.highlightTerm(encoder.encodeText(new String(terms))));
					find = true;
					findOne = true;
					span = terms.length;
					break;
				}
			}

			if (!find) {
				lightText.append(chars[start]);
				span = 1;
			}

			total_length = start + span;
			light_over = maxLength > 0 && total_length >= maxLength;
		}
		
		if (findOne) {
			return lightText.toString();
		} else {
			return null;
		}
	}

	/**
	 * 高亮
	 * 
	 * @param sentence
	 *            被高亮的句子，已分词
	 * @param keyword
	 *            用作高亮的词语，已分词
	 * @param maxLength
	 *            分词后长度，0为不限制
	 * @return 高亮后的字符串
	 */
	public AnalyzerHighLighterResult highlighter(String sentence,
			String keyword, int maxLength) {
		AnalyzerHighLighterResult result = new AnalyzerHighLighterResult();
		if (sentence == null) {
			return result;
		}

		StringBuilder originalText = new StringBuilder();
		StringBuilder lightText = new StringBuilder();

		String[] keys = keyword.split(separatorS);
		ArrayList<char[]> keyslist = new ArrayList<char[]>(keys.length);
		for (String s : keys) {
			keyslist.add(s.toCharArray());
		}

		char[] chars = sentence.toCharArray();
		int len = chars.length;
		int start = 0;
		int total_length = 0;
		boolean separator_find = false;
		boolean light_over = false;

		for (int i = 0; i < len; i++) {
			char[] terms = null;
			if (chars[i] == separator) {
				separator_find = true;

				if (i > 0) {
					if (chars[i - 1] == AnalyzerFormatter.BLANK_NEW_CHAR) {
						terms = new char[] { AnalyzerFormatter.BLANK_OLD_CHAR };
					} else {
						terms = new char[i - start];
						System.arraycopy(chars, start, terms, 0, i - start);
					}
				} else {
					terms = new char[] { AnalyzerFormatter.BLANK_OLD_CHAR };
				}

				start = i + 1;

				originalText.append(terms);

				// 如果高亮字符数已经超过限定值，不再判断
				if (light_over) {
					continue;
				}

				boolean find = false;
				for (char[] kk : keyslist) {
					if (StringUtil.equalsIgnoreCase(kk, terms)) {
						lightText.append(formatter.highlightTerm(encoder
								.encodeText(new String(terms))));
						find = true;
						break;
					}
				}
				if (!find) {
					lightText.append(terms);
				}

				total_length += terms.length;
				light_over = maxLength > 0 && total_length >= maxLength;
			}
		}

		// 最后一段
		if (separator_find) {
			if (start < len) {
				char[] terms = new char[len - start];
				System.arraycopy(chars, start, terms, 0, len - start);
				originalText.append(terms);

				// 如果高亮字符数没达到最大值，继续往下判断
				if (!light_over) {
					boolean find = false;
					for (char[] kk : keyslist) {
						if (StringUtil.equalsIgnoreCase(kk, terms)) {
							lightText.append(formatter.highlightTerm(encoder
									.encodeText(new String(terms))));
							find = true;
							break;
						}
					}
					if (!find) {
						lightText.append(terms);
					}
				}
			}
		}
		// 没找到分隔符
		else {
			originalText.append(sentence);

			// 直接判断字符集equals
			if (StringUtil.equalsIgnoreCase(chars, keyword.toCharArray())) {
				lightText.append(formatter.highlightTerm(keyword));
			} else
				lightText = null; // 直接返回
		}

		if (originalText != null)
			result.setOriginalText(originalText.toString());
		if (lightText != null)
			result.setLightText(lightText.toString());

		return result;
	}

	public static void main(String[] args) {
		// System.out.println(Runtime.getRuntime().maxMemory());
		// System.out.println(Runtime.getRuntime().totalMemory());
		// System.out.println(Runtime.getRuntime().freeMemory());

		SimpleHTMLEncoder encoder = new SimpleHTMLEncoder();
		SimpleHTMLFormatter formatter = new SimpleHTMLFormatter();
//		String keyword= "good night";
//		String str = "Good Night ! 黄金";
//		String keyword = "all   hands   together   中岛美嘉   刘 德华";
//		String str = "刘德华-今天";
		String keyword = "牛 人 mIke   tompkins";
		String str = "MiKe";
		
		AnalyzerHighLighter hl = new AnalyzerHighLighter(formatter, encoder);

		Cost cost = new Cost();
		AnalyzerHighLighterResult s = null;
		for (int i = 0; i < 1; i++) {
			// s = hl.highlighter(sentence, keyword);
			System.out.println(hl.highlighter(str, getSortedKeysList(keyword, true), 20, false));
		}
		// System.out.println(s);
		System.out.println(cost);

	}
}
