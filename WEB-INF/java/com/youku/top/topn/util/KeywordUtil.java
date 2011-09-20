package com.youku.top.topn.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.youku.search.analyzer.Number;
import com.youku.search.sort.KeywordFilter;
import com.youku.search.util.Constant;
import com.youku.search.util.DataFormat;
import com.youku.search.util.StringUtil;

public class KeywordUtil {
	final static String numberRegexp = "[第]?\\d{1,3}[集话]?";
	final static String numberRegexp1 = "[^\\d ](\\d{1,})[^\\d ]";
	final static String wordRegexp = "((中文|中字|中文字幕|英文原声|中英文|中英双语|中英双字幕|双语字幕|国英|双语|国语|粤语|日语|韩语|汉语|无字幕|字幕|DVD|中文高清|高清|清晰|TV)+版*)|抢先看|美剧|日剧|韩剧|偶像剧|全集";
	private static Set<Character> numberSet = Number.numberSet;
	private static HashMap<String,String> numberMap = Number.numberMap;
	
	public static String[] ZIMU = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
	
	public static String filter(String keyword){
		keyword = stopWordsFilter(keyword);
		keyword = wordFilter(keyword);
		keyword = numberFilter(keyword);
		keyword = StringUtil.ToDBC(keyword);
		keyword = KeywordFilter.filter(keyword);
		if(!StringUtils.isBlank(keyword))
			return keyword.trim();
		else return null;
	}
	
	public static String stopWordsFilter(String keyword){
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
	
	public static String numberFilter(String keyword){
		if(DataFormat.parseInt(keyword, -1)==-1){
			String temp_keyword =analyzer(keyword);
			Pattern pattern = Pattern.compile(numberRegexp1);
			Matcher matcher = pattern.matcher(temp_keyword);
			List<String> strs = new ArrayList<String>();
			while (matcher.find()) {
				strs.add(matcher.group(1));
			}
			if(null!=strs&&strs.size()>0){
			String temp = "";
			StringBuilder sbf = new StringBuilder();
			for(String str:strs){
				sbf.append(temp);
				sbf.append(StringUtils.substringBetween(temp_keyword, temp, str).replaceAll(numberRegexp, " "));
				temp = str;
			}
			sbf.append(strs.get(strs.size()-1));
			sbf.append(StringUtils.substringAfterLast(temp_keyword, strs.get(strs.size()-1)).replaceAll(numberRegexp, " "));
			
			return sbf.toString();
			}else return temp_keyword.replaceAll(numberRegexp, " ");
		}
		else return keyword;
	}
	
	public static String wordFilter(String keyword){
		return keyword.toUpperCase().replaceAll(wordRegexp," ");
	}
	
	public static String wordFilterTopword(String keyword){
		String k = keyword.toLowerCase().replaceAll(wordRegexp,"");
		
		if(!StringUtils.isBlank(k)){
			k = stopWordsFilter(k);
		}
		if(!StringUtils.isBlank(k)){
			k = k.trim();
			if(k.contains("akb48"))
				return "akb48";
			if(!StringUtils.isBlank(k)){
				k = k.replaceAll("大结局$", "");
			}
			if(!StringUtils.isBlank(k)){
				k = k.trim();
				k = k.replaceAll("[第 ]+\\d{1,3}[集话期]?$", "");
			}
			if(!StringUtils.isBlank(k)){
				k = k.trim();
				k = k.replaceAll("[第 ]?\\d{1,3}[集话期]$", "");
			}
			if(!StringUtils.isBlank(k)){
				k = k.trim();
				if(Pattern.compile("[^\\d]\\d{2,}$").matcher(k).find()){
					k = k.replaceAll("\\d{2,}$", "");
				}
			}
		}
		if(!StringUtils.isBlank(k)){
			k = k.trim();
			boolean f = false;
			for(String c:ZIMU){
				if(c.equalsIgnoreCase(k)){
					f = true;
					break;
				}
			}
			if(!f)
				return k;
		}
		return keyword;
	}
	
	public static void main(String[] args) {
		System.out.println(wordFilterTopword("爱情公寓电视剧大结局"));
		System.out.println(wordFilterTopword("爱情公寓25"));
		System.out.println(wordFilterTopword("第5"));
	}
	
	public static String formatString(String c){
		if(!"-".equalsIgnoreCase(c)&&DataFormat.parseFloat(c,0)>100)
			return "100";
		return c;
	}
	
	public static String[] analyzerPrepare(String s)
	{
		if (s == null)
			return null;
		
		char[] array = s.toCharArray();
		
		int len = array.length;
		StringBuffer sb = null;
		StringBuffer sb_not_analyze = null;
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
								sb.append(array,last,j-last).append(" ").append(k).append(" ");
							}
							else
							{
								if (sb == null) sb = new StringBuffer();
								String n = new String(array,j+1,i-j-1);
								String k = numberMap.get(n);
								if (k == null) k = n;
								sb.append(array,last,j-last+1).append(" ").append(k).append(" ");
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
	
	return new String[]{sb!=null?sb.toString():sb_not_analyze!=null?null:s
			,sb_not_analyze!=null?sb_not_analyze.toString():null};
	}
	
	public static String analyzer(String word) {
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
}
