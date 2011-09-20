package com.youku.search.util;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import sun.io.CharToByteGBK;
import sun.nio.cs.ext.GBK;

import com.youku.search.pool.net.parser.ResponseParser.ParserException;
import com.youku.search.sort.KeywordFilter;

public class StringUtil {

	private static final String DEFAULT_SPLIT = "|";

	public static int parseInt(String s, int defaultValue) {

		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
		}

		return defaultValue;
	}

	public static int parseInt(String s, int defaultValue, int min) {

		int result = parseInt(s, defaultValue);
		return Math.max(min, result);
	}

	public static int parseInt(String s, int defaultValue, int min, int max) {

		int result = parseInt(s, defaultValue, min);
		return Math.min(max, result);
	}

	public static String urlEncode(String s, String enc) {
		try {
			return URLEncoder.encode(s, enc);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String urlEncode(String s, String enc, String failValue) {
		try {
			return URLEncoder.encode(s, enc);
		} catch (Exception e) {
			return failValue;
		}
	}

	public static String urlDecode(String s, String enc) {
		try {
			return URLDecoder.decode(s, enc);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String urlDecode(String s, String enc, String failValue) {
		try {
			return URLDecoder.decode(s, enc);
		} catch (Exception e) {
			return failValue;
		}
	}

	public static String toAscii(String s) {
		if (s == null) {
			return null;
		}

		StringBuilder builder = new StringBuilder();

		char[] chars = s.toCharArray();
		for (int i = 0; i < chars.length; i++) {

			if (chars[i] >= 32 & chars[i] <= 126) {
				builder.append(chars[i]);
			} else {
				final String tmp = "000" + Integer.toHexString(chars[i]);
				builder.append("\\u");
				builder.append(tmp.substring(tmp.length() - 4));
			}
		}

		return builder.toString();
	}

	public static String filterNull(String s) {
		return s == null ? "" : s;
	}

	/**
	 * 把换行符替换成空格，把tab替换成空格，把连续的空格替换成一个空格，把其余的控制字符删除掉，把首尾的空格删除掉
	 */
	public static String filterNonetext(String s) {
		if (s == null) {
			return s;
		}

		Charset charset = Charset.forName("UTF-8");
		byte[] bytes = s.getBytes(charset);

		boolean previousSpace = true;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		for (int i = 0; i < bytes.length; i++) {
			int b = bytes[i];
			if (b > ' ') {
				outputStream.write(b);
				previousSpace = false;

			} else if (b == ' ') {
				if (previousSpace) {
					continue;
				} else {
					outputStream.write(b);
					previousSpace = true;
				}

			} else if (b == '\t' || b == '\n' || b == '\r') {
				if (previousSpace) {
					continue;
				} else {
					outputStream.write(' ');
					previousSpace = true;
				}

			} else {
				continue;
			}
		}

		byte[] result = outputStream.toByteArray();
		int lastIndex = result.length - 1;
		while (lastIndex >= 0 && result[lastIndex] == ' ') {
			lastIndex--;
		}

		if (lastIndex >= 0) {
			return new String(result, 0, lastIndex + 1);
		}

		return "";
	}

	/**
	 * 字符串编码转换
	 */
	public static String conv(String s, String inEncoding, String outEncoding) {
		if (s == null) {
			return null;
		}

		try {
			return new String(s.getBytes(inEncoding), outEncoding);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return s;
	}

	/**
	 * 全角转半角
	 * 
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == '\u3000') {
				c[i] = ' ';
			} else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
				c[i] = (char) (c[i] - 65248);
			}
		}
		String returnString = new String(c);
		return returnString;
	}

	public static boolean isNotNull(String s) {
		if (null != s && s.trim().length() > 0)
			return true;
		return false;
	}

	public static File getFileFromBytes(byte[] b, String outputFile) {
		BufferedOutputStream stream = null;
		File file = null;
		try {
			file = new File(outputFile);
			FileOutputStream fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return file;
	}

	public static File getFileFromStr(String src, String outputFile) {
		File file = new File(outputFile);
		if (file.exists())
			file.delete();
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outputFile), "utf-8"));
			bw.write(src);
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
		return new File(outputFile);
	}

	public static String[] stringToArr(String strs, String split) {
		if (isNotNull(strs)) {
			return strs.split(split);
		}
		return null;
	}

	private static Random randGen = new Random();
	private static char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz"
			+ "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();

	public static final String randomString(int length) {
		if (length < 1) {
			return null;
		}
		// Create a char buffer to put random letters and numbers in.
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
		}
		return new String(randBuffer);
	}

	public static String arrToString(String[] arr, String split) {
		String splitStr = ((null == split || split.trim().length() == 0) ? DEFAULT_SPLIT
				: split);

		if (null != arr && arr.length > 0) {
			StringBuilder strs = new StringBuilder();
			for (int i = 0; i < arr.length; i++) {
				if (isNotNull(arr[i])) {
					strs.append(arr[i]);
					if (i != arr.length - 1)
						strs.append(splitStr);
				}
			}
			return strs.toString();
		}
		return null;
	}

	public static List<String> parseStr2List(String str, String split) {
		List<String> alias = new ArrayList<String>();
		String[] aliasArr = str.split(split);
		String temp = null;
		if (null != aliasArr) {
			for (String a : aliasArr) {
				if (!StringUtils.isBlank(a)) {
					temp = stopWordsFilter(a.trim());
					if (!StringUtils.isBlank(temp))
						alias.add(temp);
				}
			}
		}
		if (alias.size() > 0)
			return alias;
		else
			return null;
	}

	public static String stopWordsFilter(String keyword) {
		Set<Character> set = Constant.StopWords.getStopSet();
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
	
	public static List<String> parseArr2List(String[] arr) {
		List<String> alias = new ArrayList<String>();
		if (null != arr) {
			for (String a : arr) {
				if (!StringUtils.isBlank(a)) {
					alias.add(a.trim());
				}
			}
		}
		if (alias.size() > 0)
			return alias;
		else
			return null;
	}

	/**
	 * 小写a-z
	 * 
	 * @return
	 */
	public static String getRandomAscii(int len) {
		char[] x = new char[len];
		for (int i = 0; i < x.length; i++) {
			x[i] = (char) NumberUtil.getRandomInt(96, 123);
		}

		return new String(x);
	}

	public static String getRandomBitStr(int len) {
		char[] x = new char[len];
		for (int i = 0; i < x.length; i++) {
			x[i] = (randGen.nextInt(2) == 0 ? '0' : '1');
		}

		return new String(x);
	}

	public static char[] getRandomBitChars(int len) {
		char[] x = new char[len];
		for (int i = 0; i < x.length; i++) {
			x[i] = (randGen.nextInt(2) == 0 ? '0' : '1');
		}

		return x;
	}

	public static int[] getRandomBitInt(int len) {
		int[] x = new int[len];
		for (int i = 0; i < x.length; i++) {
			x[i] = randGen.nextInt(2);
		}

		return x;
	}

	/**
	 * example: i=11,len=8 -> 00000011 <br>
	 * i=15,len=8 -> 00000015 <br>
	 * 
	 * @param i
	 *            报文长度数
	 * @param len
	 *            长度报头补零后的总长度
	 */
	public static String fillWithZero(int i, int len) {
		int l = stringSize(i);
		int count = len - l;
		char[] x = new char[count];

		for (int j = 0; j < count; j++) {
			x[j] = '0';
		}

		return new String(x) + i;
	}

	static final int[] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999,
			99999999, 999999999, Integer.MAX_VALUE };

	// Requires positive x
	static int stringSize(int x) {
		for (int i = 0;; i++)
			if (x <= sizeTable[i])
				return i + 1;
	}

	/**
	 * 将给定字符串截取到指定长度并返回
	 * 
	 * @param source
	 * @param maxByteLen
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String subChineseString(String source, int maxByteLen)
			throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();

		if (maxByteLen <= 0)
			return source;

		if (StringUtils.isBlank(source))
			return "";

		if (maxByteLen >= chineseByteLen(source))
			return source;

		for (int byteSize = 0, charIndex = 0; byteSize < maxByteLen; charIndex++) {
			char ch = source.charAt(charIndex);
			result.append(ch);
			if (getGBKValue(ch) > 0) {
				byteSize += 2;
			} else {
				byteSize++;
			}
		}

		return result.toString();
	}

	public static int chineseByteLen(String source)
			throws UnsupportedEncodingException {
		if (StringUtils.isEmpty(source))
			return 0;

		int strLength = source.length();
		int chineseByteLen = 0;

		for (int i = 0; i < strLength; i++) {
			if (getGBKValue(source.charAt(i)) > 0) {
				chineseByteLen = chineseByteLen + 2;
			} else {
				chineseByteLen++;
			}
		}

		return chineseByteLen;
	}

	public static int getGBKValue(Character ch)
			throws UnsupportedEncodingException {
		byte[] bytes = ch.toString().getBytes("GBK");
		if (bytes.length < 2)
			return 0;
		return (bytes[0] << 8 & 0xff00) + (bytes[1] & 0xff);
	}

	/**
	 * 为一个字符串在指定的位置添加指定的字符
	 * 
	 * @param originStr
	 *            原字符串
	 * @param targetChars
	 *            返回的新char[]
	 * @param addChar
	 *            需要增加的字符
	 * @param srcIndexs
	 *            指定在原字符串的哪些位置添加字符 (38,42,46,50)
	 */
	public static void addChars(String srcString, char[] targetChars,
			char addChar, int... srcIndexs) {
		if (targetChars.length != srcString.length() + srcIndexs.length) {
			throw new IllegalArgumentException("参数错误，srcString=" + srcString);
		}

		// srcBegin: 0,38,42,46,50
		// srcEnd: 38,42,46,50,srcString.length
		// dst: targetChars
		// dstBegin: 0,39,44,49,54

		// srcIndex: 38,42,46,50
		// targetCharIndex: 38,43,48,53

		int srcBegin = 0;
		int srcEnd = 0;
		int dstBegin = 0;
		int targetCharIndex = 0;
		for (int i = 0; i <= srcIndexs.length; i++) {
			srcBegin = srcEnd;
			srcEnd = (i < srcIndexs.length ? srcIndexs[i] : srcString.length());
			dstBegin = srcBegin + i;

			srcString.getChars(srcBegin, srcEnd, targetChars, dstBegin);

			if (i < srcIndexs.length) {
				targetCharIndex = srcIndexs[i] + i;
				targetChars[targetCharIndex] = addChar;
			}
		}
	}

	public static boolean equalsIgnoreCase(char[] a, char[] a2) {
		if (a == a2)
			return true;
		if (a == null || a2 == null)
			return false;

		int length = a.length;
		if (a2.length != length)
			return false;

		for (int i = 0; i < length; i++) {
			if (toLowerCase(a[i]) != toLowerCase(a2[i]))
				return false;
		}

		return true;
	}

	private static char toLowerCase(char c) {
		if (c > 64 & c < 91)
			return (char) ((int) c + 32);
		Character.toLowerCase(c);
		return c;
	}

	public static String[] convertFingerToMD5(String[] array)
			throws ParserException {
		String[] result = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = convertFingerToMD5(array[i]);
		}
		return result;
	}

	/**
	 * 将Response返回的finger数组过滤为正规的MD5格式（中间加-）
	 * 
	 * @param array
	 * @return
	 * @throws ParserException
	 */
	public static String convertFingerToMD5(String finger)
			throws ParserException {
		if (finger.length() != 64) {
			throw new ParserException("传入的finger长度错误：" + finger);
		}

		String originMD5 = null;
		char[] targetMD5 = null;

		originMD5 = finger.substring(0, finger.length() - 2);
		targetMD5 = new char[66];
		addChars(originMD5, targetMD5, '-', 38, 42, 46, 50);
		return String.valueOf(targetMD5);
	}

	public static String[] convertMD5ToFinger(String[] array)
			throws ParserException {
		String[] result = new String[array.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = convertMD5ToFinger(array[i]);
		}
		return result;
	}

	/**
	 * 将前端传来的66位md5(fileid)转换为C-Server需要的finger格式
	 * 
	 * @param md5
	 * @return
	 * @throws ParserException
	 */
	public static String convertMD5ToFinger(String md5) throws ParserException {
		if (md5.length() != 66) {
			throw new ParserException("传入的MD5长度错误：" + md5);
		}

		StringBuilder sb = new StringBuilder();
		sb.append(md5.substring(0, 38));
		sb.append(md5.substring(39, 43));
		sb.append(md5.substring(44, 48));
		sb.append(md5.substring(49, 53));
		sb.append(md5.substring(54));
		sb.append("00");

		return StringUtils.upperCase(sb.toString());
	}
	
	public static enum EncodingSet {
		GBK("GBK"), //
		GB2312("GB2312"), // 
		UTF8("UTF-8"), //
		ISO88591("ISO-8859-1"), // 
		UNKNOWN("UNKNOWN"); //
		
		private final String encode;
		
		private EncodingSet(String encode){
			this.encode = encode;
		}
		
		public String getEncode() {
			return encode;
		}
		
		@Override
		public String toString() {
			return getEncode();
		}
	}

	/**
	 * 判断字符串的编码
	 * 
	 * @param str
	 * @return
	 */
	public static EncodingSet getEncoding(String str) {
		EncodingSet[] set = EncodingSet.values();
		for (EncodingSet encoding : set) {
			if (encoding != EncodingSet.UNKNOWN) {
				try {
					String encode = encoding.getEncode();
					if (str.equals(new String(str.getBytes(encode), encode))) {
						return encoding;
					}
				} catch (Exception e) {
				}
			}
		}
		
		return EncodingSet.UNKNOWN;
	}

	public static void main(String[] args) throws Exception {
		// System.out.println(urlEncode("牛人", "UTF-8"));
		// testRandomBitInt();
		String testStr = "「イー・・・・・・アル・カンフーで、ラップ」";
		testStr = "「イーアルカンフーで、ラップ」";
		testStr = "lapin câlin";
//		testStr = "å¤§åº å© åº å½±è§";
		testStr = "é å ¨æ ­";
		
//		Character c = new Character(' ');
//		System.out.println(new Integer(c));
		
//		char[] chars = testStr.toCharArray();
//		CharToByteGBK ctb = new CharToByteGBK();
//		for (int i = 0; i < chars.length; i++) {
//			filterExceptGBK(ctb, chars, i);
//		}
		
//		testStr = new String(chars);
//		System.out.println("过滤后的串："+testStr+", 编码="+getEncoding(testStr));
		
//		for (int i = 0; i < testStr.length(); i++) {
//			System.out.println("["+i+"]="+getGBKValue(new Character(testStr.charAt(i))));
//			System.out.println("["+i+"]="+getEncoding());
//		}
		
	}

	/**
	 * 测试随机算法的平均度，理想情况下result为0是最平均的
	 */
	private static void testRandomBitInt() {
		int[] randomResult = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		for (int i = 0; i < 1000000; i++) {
			int[] randomBits = getRandomBitInt(8);
			for (int j = 0; j < randomBits.length; j++) {
				randomResult[j] += (randomBits[j] == 0 ? -1 : 1);
			}
			System.out.println(Arrays.toString(randomBits));
		}
		System.out.println("--- result=" + Arrays.toString(randomResult));
	}

}
