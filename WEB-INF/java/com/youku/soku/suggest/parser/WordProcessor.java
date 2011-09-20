package com.youku.soku.suggest.parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class WordProcessor {

	private static Logger log = Logger.getLogger(WordProcessor.class);
	private int type;
	private static Set<Character> numberSet = null;
	private static char[] numberWords = new char[]{
		'0','1','2','3','4','5','6','7','8','9','一','二','三','四','五','六','七','八','九','十'
	};
	
	static {
		numberSet = new HashSet<Character>();
	    for (int i=0;i<numberWords.length;i++)
	    	numberSet.add(numberWords[i]);
	}
	
	public static HashMap<String,String> numberMap = new HashMap<String,String>();
	
	static {
		numberMap.put("01","1");
		numberMap.put("02","2");
		numberMap.put("03","3");
		numberMap.put("04","4");
		numberMap.put("05","5");
		numberMap.put("06","6");
		numberMap.put("07","7");
		numberMap.put("08","8");
		numberMap.put("09","9");
		numberMap.put("零","0");
		numberMap.put("一","1");
		numberMap.put("二","2");
		numberMap.put("三","3");
		numberMap.put("四","4");
		numberMap.put("五","5");
		numberMap.put("六","6");
		numberMap.put("七","7");
		numberMap.put("八","8");
		numberMap.put("九","9");
		numberMap.put("十","10");
		numberMap.put("一十","10");
		numberMap.put("十一","11");
		numberMap.put("十二","12");
		numberMap.put("十三","13");
		numberMap.put("十四","14");
		numberMap.put("十五","15");
		numberMap.put("十六","16");
		numberMap.put("十七","17");
		numberMap.put("十八","18");
		numberMap.put("十九","19");
		numberMap.put("二十","20");
		numberMap.put("二一","21");
		numberMap.put("二二","22");
		numberMap.put("二三","23");
		numberMap.put("二四","24");
		numberMap.put("二五","25");
		numberMap.put("二六","26");
		numberMap.put("二七","27");
		numberMap.put("二八","28");
		numberMap.put("二九","29");
		numberMap.put("三十","30");
		numberMap.put("三一","31");
		numberMap.put("三二","32");
		numberMap.put("三三","33");
		numberMap.put("三四","34");
		numberMap.put("三五","35");
		numberMap.put("三六","36");
		numberMap.put("三七","37");
		numberMap.put("三八","38");
		numberMap.put("三九","39");
		numberMap.put("四十","40");
		numberMap.put("四一","41");
		numberMap.put("四二","42");
		numberMap.put("四三","43");
		numberMap.put("四四","44");
		numberMap.put("四五","45");
		numberMap.put("四六","46");
		numberMap.put("四七","47");
		numberMap.put("四八","48");
		numberMap.put("四九","49");
		numberMap.put("五十","50");
		numberMap.put("五一","51");
		numberMap.put("五二","52");
		numberMap.put("五三","53");
		numberMap.put("五四","54");
		numberMap.put("五五","55");
		numberMap.put("五六","56");
		numberMap.put("五七","57");
		numberMap.put("五八","58");
		numberMap.put("五九","59");
		numberMap.put("六十","60");
		numberMap.put("六一","61");
		numberMap.put("六二","62");
		numberMap.put("六三","63");
		numberMap.put("六四","64");
		numberMap.put("六五","65");
		numberMap.put("六六","66");
		numberMap.put("六七","67");
		numberMap.put("六八","68");
		numberMap.put("六九","69");
		numberMap.put("七十","70");
		numberMap.put("七一","71");
		numberMap.put("七二","72");
		numberMap.put("七三","73");
		numberMap.put("七四","74");
		numberMap.put("七五","75");
		numberMap.put("七六","76");
		numberMap.put("七七","77");
		numberMap.put("七八","78");
		numberMap.put("七九","79");
		numberMap.put("八十","80");
		numberMap.put("八一","81");
		numberMap.put("八二","82");
		numberMap.put("八三","83");
		numberMap.put("八四","84");
		numberMap.put("八五","85");
		numberMap.put("八六","86");
		numberMap.put("八七","87");
		numberMap.put("八八","88");
		numberMap.put("八九","89");
		numberMap.put("九十","90");
		numberMap.put("九一","91");
		numberMap.put("九二","92");
		numberMap.put("九三","93");
		numberMap.put("九四","94");
		numberMap.put("九五","95");
		numberMap.put("九六","96");
		numberMap.put("九七","97");
		numberMap.put("九八","98");
		numberMap.put("九九","99");
		
		numberMap.put("二十一","21");
		numberMap.put("二十二","22");
		numberMap.put("二十三","23");
		numberMap.put("二十四","24");
		numberMap.put("二十五","25");
		numberMap.put("二十六","26");
		numberMap.put("二十七","27");
		numberMap.put("二十八","28");
		numberMap.put("二十九","29");
		
		numberMap.put("三十一","31");
		numberMap.put("三十二","32");
		numberMap.put("三十三","33");
		numberMap.put("三十四","34");
		numberMap.put("三十五","35");
		numberMap.put("三十六","36");
		numberMap.put("三十七","37");
		numberMap.put("三十八","38");
		numberMap.put("三十九","39");
		
		numberMap.put("四十一","41");
		numberMap.put("四十二","42");
		numberMap.put("四十三","43");
		numberMap.put("四十四","44");
		numberMap.put("四十五","45");
		numberMap.put("四十六","46");
		numberMap.put("四十七","47");
		numberMap.put("四十八","48");
		numberMap.put("四十九","49");
		
		numberMap.put("五十一","51");
		numberMap.put("五十二","52");
		numberMap.put("五十三","53");
		numberMap.put("五十四","54");
		numberMap.put("五十五","55");
		numberMap.put("五十六","56");
		numberMap.put("五十七","57");
		numberMap.put("五十八","58");
		numberMap.put("五十九","59");
		
		numberMap.put("六十一","61");
		numberMap.put("六十二","62");
		numberMap.put("六十三","63");
		numberMap.put("六十四","64");
		numberMap.put("六十五","65");
		numberMap.put("六十六","66");
		numberMap.put("六十七","67");
		numberMap.put("六十八","68");
		numberMap.put("六十九","69");
		
		numberMap.put("七十一","71");
		numberMap.put("七十二","72");
		numberMap.put("七十三","73");
		numberMap.put("七十四","74");
		numberMap.put("七十五","75");
		numberMap.put("七十六","76");
		numberMap.put("七十七","77");
		numberMap.put("七十八","78");
		numberMap.put("七十九","79");
		
		numberMap.put("八十一","81");
		numberMap.put("八十二","82");
		numberMap.put("八十三","83");
		numberMap.put("八十四","84");
		numberMap.put("八十五","85");
		numberMap.put("八十六","86");
		numberMap.put("八十七","87");
		numberMap.put("八十八","88");
		numberMap.put("八十九","89");
		
		numberMap.put("九十一","91");
		numberMap.put("九十二","92");
		numberMap.put("九十三","93");
		numberMap.put("九十四","94");
		numberMap.put("九十五","95");
		numberMap.put("九十六","96");
		numberMap.put("九十七","97");
		numberMap.put("九十八","98");
		numberMap.put("九十九","99");
		
		numberMap.put("一百","100");
		numberMap.put("一千","1000");
		numberMap.put("一千零一","1001");
		numberMap.put("一万","10000");
	}
	
	
	public static String analyzerPrepare(String s)
	{
		if (s == null)
			return null;
		
		char[] array = s.toCharArray();
		
		int len = array.length;
		StringBuffer sb = null;
		int last = 0;
		try
		{
		for (int i =0;i<len;i++)
		{
			if (i > 0 && array[i] == '集')
			{
				if (numberSet.contains(array[i-1]))
				{
					for (int j = i-1;j>=last;j--)
					{
						if (!numberSet.contains(array[j]))
						{
							if (array[j] == ' ' || array[j] == '第')
							{
								if (sb == null) sb = new StringBuffer();
								String n = new String(array,j+1,i-j-1);
								String k = numberMap.get(n);
								if (k == null) k = n;
								sb.append(array,last,j-last).append(k).append(" ");
							}
							else
							{
								if (sb == null) sb = new StringBuffer();
								String n = new String(array,j+1,i-j-1);
								String k = numberMap.get(n);
								if (k == null) k = n;
								sb.append(array,last,j-last+1).append(k).append(" ");
							}
							last = i+1;
							break;
						}
						else
							continue;
					}
				}
				else
					continue;
			}
			else if (i > 0 && (array[i] == '季' || array[i] == '部'))
			{
				if (i == len - 1 || (i < len-1 && array[i+1] != '度' && array[i+1] != '节'))
				{
					if (numberSet.contains(array[i-1]))
					{
						for (int j = i-1;j>=0;j--)
						{
							if (!numberSet.contains(array[j]))
							{
								if (array[j] == '第')
								{
									if (j>last)
									{
										if (sb == null) sb = new StringBuffer();
										sb.append(array,last,j-last);
									}
									last = i+1;
									if (sb == null)
										sb = new StringBuffer();
									else
										sb.append(" ");
									String n = new String(array,j+1,i-j-1);
									String k = numberMap.get(n);
									if (k == null) k = n;
									sb.append("第").append(k).append(array[i]);
								}
								break;
							}
							
						}
					}
					else
						continue;
				}
			
			}
			
			else 
			{
				
				
			}
		}

		if (last < s.length())
		{
			if (sb == null) sb = new StringBuffer();
				sb.append(array,last,len-last);
		}
	}catch(Throwable e)
	{
		e.printStackTrace();
	}
		return (sb == null) ? s : sb.toString();
	}
	

	
	/**
	 * 格式化字符串数字
	 * @param number
	 * @return 不在map内，原值返回
	 */
	public static String formatNumber(String number)
	{
		if (numberMap.containsKey(number))
			return numberMap.get(number);
		return number;
	}
	
	public static String formatTeleplayName(String title)
	{
		return title.replaceAll("((中字|中文字幕|英文原声|中英文|中英双语|中英双字幕|双语字幕|国英|双语|国语|日语|韩语|汉语|无字幕|字幕|DVD|中文高清|高清|清晰)+版*)|抢先看|电影|电视剧|全集|","");
	}
	
	public static String removeSpecialCharacter(String title) {
		String regex="[^a-zA-Z0-9'\u4E00-\u9FA5，,]";
		if(!isChinestWord(title)) {
			regex="[^a-zA-Z0-9'\u4E00-\u9FA5，, ]";   //英文不能去掉空格
		}
		return title.replaceAll(regex, "");
	}
	
	public static boolean isChinestWord(String word) {
		String regex="^[0-9\\u4E00-\\u9FA5\\uF900-\\uFA2D ]+$";
		
		return word.matches(regex);
	}
	
	public static String formatVideoQueryString(String keyword)
	{
		String regex = "([a-zA-Z\u4E00-\u9FA5 ]+)(\\d+)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(keyword);
		String name = null;
		int number = 0;
		if(m.matches()) {
			name = m.group(1);					
			if(!StringUtils.isBlank(m.group(2))) {
				try {
					number = Integer.valueOf(m.group(2));
				} catch (NumberFormatException e) {
					//log.error("formart number error");
					return keyword;
				}
			}
			return name.trim() + number;
		} else {
			return keyword;
		}
		
	}
	
	public static int getStringLength(String str) {
		if(str == null) {
			return 0;
		}
		/*char[] charArr = str.toCharArray();
		int length = 0;
		
		for(char c : charArr) {
			if(c > '\u4E00' && c < '\u9FA5') {
				length += 2;
			} else {
				length += 1;
			}
		}*/
		
		return str.length();
	}
	
	public static String processWord(String word) {
		return formatVideoQueryString(formatTeleplayName(analyzerPrepare(removeSpecialCharacter(word.trim()))));
	}

	public static void main(String[] args) {
		String s = "di ss 集".replace(" ", "");
		System.out.println(removeSpecialCharacter("soutd sss"));
		System.out.println(processWord("xxx sss#@!"));
		System.out.println(removeSpecialCharacter("蜗居 第一卷"));
		System.out.println(removeSpecialCharacter("快乐大本营 2011"));
		System.out.println(isChinestWord("快乐大本营 2011"));
		System.out.println(isChinestWord("xxfff  ssw我"));
		System.out.println("激情劲舞美女 美女qq：1498061805".substring(0, 12));
		System.out.println(processWord("还珠格格之燕儿翩翩飞"));
	}
}
