/**
 * 
 */
package com.youku.search.analyzer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Token;

import com.youku.search.analyzer.WholeWord.Word;
import com.youku.search.index.db.SynonymManager;
import com.youku.search.util.Constant;

/**
 * @author william
 * 分词前处理
 */
public class WordProcessor {
	
	private static Set<Character> numberSet = Number.numberSet;
	private static HashMap<String,String> numberMap = Number.numberMap;
	
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
									if (sb_not_analyze == null)
										sb_not_analyze = new StringBuffer();
									else
										sb_not_analyze.append(" ");
									String n = new String(array,j+1,i-j-1);
									String k = numberMap.get(n);
									if (k == null) k = n;
									sb_not_analyze.append("第").append(k).append(array[i]);
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
	
	return new String[]{sb!=null?sb.toString():sb_not_analyze!=null?null:s
			,sb_not_analyze!=null?sb_not_analyze.toString():null};
	}
	

	/**
	 * 
	 * @param s
	 * @return
	 */
	public static WholeWord getWholeWord(String s)
	{
		WholeWord tokens = new WholeWord();
		if (s == null)
			return tokens;
		
		char[] array = s.toCharArray();
		
		int len = array.length;
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
								String k ;
								int start;
								if (array[j] == ' ' || array[j] == '第')
								{
									start = j;
									
									String n = new String(array,j+1,i-j-1);
									k = numberMap.get(n);
									if (k == null) k = n;
									
									if (j>last)
										tokens.add(s.substring(last,j));
								}
								else
								{
									start = j+1;
									String n = new String(array,j+1,i-j-1);
									k = numberMap.get(n);
									if (k == null) k = n;
									
									if (j>last)
										tokens.add(s.substring(last,j+1));
									
								}
								
								Token token  = new Token();
								token.setStartOffset(start);
								token.setEndOffset(i+1);
								token.setTermText(k);
								tokens.add(token);
								
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
										String n = new String(array,j+1,i-j-1);
										String k = numberMap.get(n);
										if (k == null) k = n;
										
										if (j>last)
											tokens.add(s.substring(last,j));
										
										Token token  = new Token();
										token.setStartOffset(j);
										token.setEndOffset(i+1);
										token.setTermText("第"+k+array[i]);
										tokens.add(token);
										
										last = i+1;
										
									}
									break;
								}
								
							}
						}
						else
							continue;
					}
				
				}
			}
		}
	catch(Throwable e)
	{
		e.printStackTrace();
	}
	if (!tokens.isEmpty())
	{
		if (last < s.length())
			tokens.add(s.substring(last));
	}
	
	return tokens;
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
		return title.replaceAll("((中字|中文字幕|英文原声|中英文|中英双语|中英双字幕|双语字幕|国英|双语|国语|日语|韩语|汉语|无字幕|字幕|DVD|中文高清|高清|清晰)+版*)|抢先看|美剧|日剧|韩剧|偶像剧","");
	}
	
	public static String formatVideoQueryString(String keyword)
	{
		if (keyword!= null)
		{
			if ( keyword.endsWith("全集") && keyword.length() >2)
			{
				return keyword.substring(0,keyword.length()-2);
			}
		}
		
		return keyword;
	}
	
	/**
	 * 获取同义词
	 * @param s
	 * @return
	 */
	public static String getSynonym(String s)
	{
		char[] array = s.toCharArray();
		StringBuffer sb = new StringBuffer();
		int len = array.length;
		try
		{
			for (int i =0;i<len;i++)
			{
				for (int j=s.length();j>i+1;j--)
				{
					String t = s.substring(i,j);
					String[] words = SynonymManager.getInstance().getWords(t);
//					String[] words = getWords(t);
					if (words != null){
						for (String word:words){
							if (!word.equals(t)){
								sb.append(word).append(" ");
							}
						}
						i = j;
						break;
					}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		if (sb.length() > 0)
			return sb.substring(0,sb.length()-1);
		return null;
	}
//	private static String[] getWords(String s)
//	{
//		if(s.equals("周杰伦") || s.equals("杰伦"))
//		return new String[]{"周杰伦","杰伦","周董"};
//		return null;
//	}
	
	public static boolean checkKeywordIsValid(String keyword)
	{
		if (keyword != null && keyword.trim().length()==1)
		{
			if (Constant.StopWords.getStopSet().contains(keyword.charAt(0)))
				return false;
		}
		return true;
	}
	
	public static void main(String[] args)
	{
		WholeWord word = getWholeWord("宠物小精灵第一集第一季");
		if (word!=null && !word.isEmpty()){
			List<Word> words = word.getTokens();
			for (Word w: words){
				if (!w.isToken)
					System.out.println(w.text);
				else
					System.out.println(w.token.termText());
			}
		}
		
		String[] arr = analyzerPrepare("宠物小精灵第一集");
		System.out.println(arr[0]);
		System.out.println(arr[1]);
	}
	
}
