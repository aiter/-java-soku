/**
 * 
 */
package com.youku.search.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.youku.search.analyzer.AnalyzerManager;
import com.youku.search.analyzer.Number;
import com.youku.search.analyzer.WholeWord;
import com.youku.search.analyzer.WordProcessor;
import com.youku.search.config.Config;


/**
 * @author william
 *
 */
public class MyUtil {
	
	public static String formatString(String s)
	{
		return null==s?"":s;
	}
	
	public static String getString(byte[] b)
	{
		if (b!=null && b.length > 0)
		{
			try {
				return new String(b,"utf8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	public static String getString(String s)
	{
		if (null == s)return null;
			try {
				return new String(s.getBytes("8859_1"),"utf8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		return null;
	}
	public static String getString(byte[] b,String defaultValue)
	{
		if (b!=null && b.length > 0)
		{
			try {
				return new String(b,"UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		return defaultValue;
	}
	
	
	public static String encodeVideoId (int id)
	{
		return "X"+getBASE64(String.valueOf(id << 2));
	}
	
	public static int decodeVideoId(String s)
	{
		s = getFromBASE64( s.substring(1,s.length()));
		return DataFormat.parseInt(s) >> 2;
	}
	public static BASE64Encoder base64 = new BASE64Encoder();
	public static BASE64Decoder debase64 = new BASE64Decoder();
	public static synchronized String getBASE64(String s) {
		if (s == null)
			return null;
		return base64.encode(s.getBytes());
	}

	// 将 BASE64 编码的字符串 s 进行解码
	public static synchronized String getFromBASE64(String s) {
		if (s == null)
			return null;
		try {
			byte[] b = debase64.decodeBuffer(s);
			return new String(b);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static boolean isSyntax(String expression)
	{
		return expression.charAt(0) == '{' && expression.charAt(expression.length()-1) == '}';
	}
	
	public static String formatSyntax(String expression)
	{
		return expression.substring(1,expression.length()-1);
	}
	public static boolean contains(int small,int big)
	{
		return (small & big) == small;
	}
	/**
	 * 模糊音转换
	 * @param s
	 * @return
	 */
	public static String formatFuzzyLetter(String s)
	{
		char[] buffer = s.toCharArray();
	      
	      int length = s.length();
	      for(int i=0;i<length;i++){
	    	  switch(buffer[i]) {
	    	  //0-9 
	    	  	case('z'):
	    	  	case('c'):
	    	  	case('s'):
		    		if (i < length-1 && buffer[i+1]!='h')
		    		{
		    	  		char[] tmpBuffer = new char[length+1];
		    	  		System.arraycopy(buffer,0,tmpBuffer,0,i+1);
		    	  		System.arraycopy(buffer,i+1,tmpBuffer,i+2,length-1-i);
		    	  		tmpBuffer[i+1] = 'h';
		    	  		i++;
		    	  		length++;
		    	  		buffer = tmpBuffer;
		    		}
	    	  		break;
	    	  	case('a'):
	    	  	case('e'):
	    	  	case('i'):
	    	  		if (i < length-1 && buffer[i+1]=='n' && (i<length-2?buffer[i+2]!='g':true))
	    	  		{
	    	  			char[] tmpBuffer = new char[length+1];
		    	  		System.arraycopy(buffer,0,tmpBuffer,0,i+2);
		    	  		if (i < length-2)
		    	  			System.arraycopy(buffer,i+2,tmpBuffer,i+3,length-2-i);
		    	  		tmpBuffer[i+2] = 'g';
		    	  		i++;
		    	  		length++;
		    	  		buffer = tmpBuffer;
	    	  		}
	    	  		break;
	    	  }
	      }
	      return s.length() == buffer.length? s: new String(buffer);
	}
	
	public static String parseSyntax(String s)
	{
		return parseSyntax(s ,true);
	}
	/**
	 * 解析高级搜索公式
	 * @param s
	 * @param needAnanlyze
	 * @return
	 */
	public static String parseSyntax(String s,boolean needAnanlyze)
	{

		List<Integer> list = new ArrayList<Integer>();
		list.add((int)'+');
		list.add((int)'-');
		list.add((int)' ');
		list.add((int)'(');
		list.add((int)')');
		
		StringBuffer sb = new StringBuffer();

		char[] arr = s.trim().toCharArray();
		int start = 0;
		int parentheses_left = 0;
		int parentheses_right = 0;

//		如果不是+ -号开头,自动补齐
		if (arr[0] != '+' && arr[0] != '-')
		{
			char[] newarr = new char[arr.length+1];
			newarr[0] = '+';
			System.arraycopy(arr,0,newarr,1,arr.length);
			arr = newarr;

		}
		
		int len = arr.length;
		
		for (int i = 0;i<len;i++)
		{
			if (list.contains((int)arr[i]))
			{
				if (start == 0)
				{
					start = 1;
				}
				else
				{
					if (arr[i] == '(')
						parentheses_left++;
					else if (arr[i] == ')'){
						if (parentheses_right >= parentheses_left){
							if (i - start != 0){
								String text = null;
								append(sb,arr,i,start,needAnanlyze);
								sb.append(text);
							}
							start = i+1;
							continue;
						}
						parentheses_right++;
					}
					
					if (i - start == 0){
						sb.append(arr[i]);
						start = i+1;
						continue;
					}
					append(sb,arr,i,start,needAnanlyze);

					start = i+1;

				}
				sb.append(arr[i]);


			}

		}
		if (!list.contains((int)arr[len-1]))
		{
			append(sb,arr,len,start,needAnanlyze);
		}
		if (parentheses_right < parentheses_left)
		{
			for (int i=0;i<parentheses_left - parentheses_right;i++)
			{
				sb.append(")");
			}
		}
		return sb.toString();

	}
	
	private static void append(StringBuffer sb,char[] arr,int i,int start,boolean needAnanlyze)
	{
		if (arr[start] == '\"' && arr[i-1] =='\"' && i-start>2){
			String text = new String(arr,start+1,i-start-2);
			
			//预处理第几季
			String[] array = WordProcessor.analyzerPrepare(text);
			text = array[0];
			String noAnalyze = array[1];
			if (noAnalyze != null)
			{
				if (noAnalyze.indexOf(" ") > 0)
					noAnalyze = noAnalyze.replaceAll(" ","\")AND(\"");
				
				sb.append("(\"");
				if (text != null)
				{
					if (needAnanlyze)
						sb.append(MyUtil.analyzeWord(true,text)).append("\"AND\"");
					else
						sb.append(text).append("\"AND\"");
				}
				sb.append(noAnalyze).append("\")");
			}
			else{
				if (needAnanlyze)
					sb.append("(\"").append(MyUtil.analyzeWord(true,text)).append("\")");
				else
					sb.append("(\"").append(text).append("\")");
			}
		}
		else{
			if (needAnanlyze)
				sb.append(analyzeSyntaxWord(new String(arr,start,i-start)));
			else
				sb.append(new String(arr,start,i-start));
		}

	}
	
	private static String analyzeSyntaxWord(String words)
	{
		String[] arr = WordProcessor.analyzerPrepare(words);
		words = arr[0];
		String noAnalyze = arr[1];
		StringBuffer sb = new StringBuffer();
		
		if (noAnalyze != null)
		{
			if (noAnalyze.indexOf(" ") > 0)
				noAnalyze = noAnalyze.replaceAll(" ","\\\")AND(\\\"");
			
			
			sb.append("(\"").append(noAnalyze).append("\")");
			if (words  != null) sb.append("AND");
		}
		
		if (words  != null)
		{
			TokenStream ts = AnalyzerManager.getMyAnalyzer().tokenStream("",new StringReader(words));
			
			Token t = null;
			sb.append("(");
			try {
				while ((t = ts.next()) != null)
				{
					sb.append("(");
					sb.append(t.termText());
					sb.append(")");
					sb.append("AND");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (sb.length()>3)
				return sb.substring(0,sb.length()-3)+")";
			else
				return sb.substring(0,sb.length()-1);
		}
		else
			return sb.toString();
		
	}
	
	public static String prepareAndAnalyzeWord(String words)
	{
		WholeWord wholeWord = WordProcessor.getWholeWord(words);
		if (wholeWord !=null && !wholeWord.isEmpty())
			return analyzeWord(true,words,wholeWord);
		else
			return analyzeWord(true,words);
	}
	
	
	public static String analyzeWord(boolean format,String words)
	{
		if(words == null || words.length() == 0)
			return null;
		StringBuffer sb = new StringBuffer();
		TokenStream ts = AnalyzerManager.getMyAnalyzer(format).tokenStream("",new StringReader(words));
		
		Token t = null;
		try {
			while ((t = ts.next()) != null)
			{
				sb.append(t.termText());
				sb.append(" ");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString().trim();
		
	}
	public static String analyzeWord(boolean format,String words,WholeWord wholeWord)
	{
		if(words == null || words.length() == 0)
			return null;
		StringBuffer sb = new StringBuffer();
		TokenStream ts = AnalyzerManager.getMyAnalyzer(format).tokenStream(new StringReader(words),wholeWord);
		
		Token t = null;
		try {
			while ((t = ts.next()) != null)
			{
				sb.append(t.termText());
				sb.append(" ");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString().trim();
		
	}
	/**
	 * 是否是某种类型索引服务器
	 * @param type
	 * @return
	 */
	public static boolean isIndexServer(int type)
	{
		return Config.getServerType() == 1 && ("|"+Config.getIndexType()+"|").indexOf("|"+type+"|") > -1;
	}
	
	
	public static int decodeVideoUrl(String url)
	{
			int start = url.lastIndexOf("_");
			int end = url.lastIndexOf(".");
			if (start > -1 && end > -1 && start+1 < url.length() && end > start)
				return decodeVideoId(url.substring(start+1,end));
			return 0;
	}
	
	public static String toLowerCase(String str)
	{
		 char[] buffer = str.toCharArray();
	      String s = null;
	      
	      if ((s = Number.numberMap.get(str)) != null)
	      {
	    	return  s;
	      }
	      
	      
	      int length = buffer.length;
	      for(int i=0;i<length;i++){
	    	  	if ( buffer[i] == 12288){
	    	  		buffer[i] = 32;
	    	  	}
	    	  	else{
	    	  		if (buffer[i]> 65280 && buffer[i]< 65375){ //全角转半角
	    	  			buffer[i] = (char) (buffer[i] - 65248);
	    	  		}
	    	  		else
	    	  			buffer[i] = Character.toLowerCase(buffer[i]);
	    	  	}
	    	  }
	      return new String(buffer);
	}
	
	/**
	 * startsWith忽略大小写版
	 * @param one
	 * @param anotherString
	 * @return
	 */
	public static boolean isSimilar(String one, String anotherString) {  
        
        int length = one.length();  
        if(length > anotherString.length()) {  
            //如果被期待为开头的字符串的长度大于anotherString的长度  
            return false;  
        }  
        if (one.equalsIgnoreCase(anotherString.substring(0, length))) {  
            return true;  
        } else {  
            return false;  
        }
    }  
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
//		String s = "28853206";
//		System.out.println(WordProcessor.analyzerPrepare(s)[0]);
//		System.out.println(WordProcessor.analyzerPrepare(s)[1]);
//		System.out.println(parseSyntax("四川-春天-故事 地震-搜索 \"毛孔的修复产品\" -美女+\"哈哈\""));
//		System.out.println( computeDocBoost(DataFormat.getNextDate(new Date(),-1), 100, 1, 0, 20,25));
//		for (String s:arr)
//		System.out.print(MyUtil.toLowerCase("ｓｍ＆ａｓｄ大是大非"));
		System.out.println(MyUtil.decodeVideoId("XMjQ0Nzc2OTg4"));
		
	}
}
