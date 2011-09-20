/**
 * 
 */
package com.youku.top;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

import com.youku.search.util.DataFormat;
import com.youku.top.config.Config;
import com.youku.top.index.analyzer.AnalyzerManager;
import com.youku.top.index.analyzer.WholeWord;
import com.youku.top.index.analyzer.WordProcessor;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;



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
	
	/**
	 * 解析公式
	 * @param s 
	 * @return 返回 size=2的数组,0=包含 1不包含
	 */
//	public static String[] parseSyntax2(String s)
//	{
//
//		List<Integer> list = new ArrayList<Integer>();
//		list.add((int)'+');
//		list.add((int)'-');
//		list.add((int)' ');
//		list.add((int)'(');
//		list.add((int)')');
//		
//		StringBuffer sb = new StringBuffer();
//		StringBuffer nosb = new StringBuffer();
//		char[] arr = s.trim().toCharArray();
//		int start = 1;
//		int parentheses_left = 0;
//		int parentheses_right = 0;
//		int parentheses_left_no = 0;
//		boolean startNotin = false;
//		int len = arr.length;
//		
////		如果不是+ -号开头,自动补齐
//		if (arr[0] != '+' && arr[0] != '-')
//		{
//			char[] newarr = new char[len+1];
//			newarr[0] = '+';
//			System.arraycopy(arr,0,newarr,1,len);
//			arr = newarr;
//			len++;
//		}
//		
//		for (int i = 0;i<len;i++)
//		{
//			if (i == 0){
//				if (arr[i] == '-'){
//					startNotin =true;
//					nosb.append(arr[i]);
//				}
//				else
//					sb.append(arr[i]);
//				
//				continue;
//			}
//			if (list.contains((int)arr[i]))
//			{
//				if (arr[i] == '('){
//					parentheses_left++;
//					if (startNotin) parentheses_left_no++;
//				}
//				else if (arr[i] == ')'){
//					if (parentheses_right >= parentheses_left){
//						if (i - start > 0){
//							append(sb,nosb,arr,i,start,startNotin);
//						}
//						start = i+1;
//						continue;
//					}
//					//如果-号中的括号
//					
//					parentheses_right++;
//				}
//				else if (arr[i] == '-'){
//					
//					if (i - start > 0)
//					{
//						append(sb,nosb,arr,i,start,startNotin);
//					}
//						//nosb.append(arr[i]);	
//					startNotin = true;
//					start = i+1;
//					continue;
//				}
//				
//				if (i - start == 0){
//					if (startNotin)
//						nosb.append(arr[i]);
//					else{
//						sb.append(arr[i]);
//					}
//				}
//				else{
//					append(sb,nosb,arr,i,start,startNotin);
//				}
//				
//				//判断是否关闭-号操作符判断
//				if (startNotin)
//				{
//					if (arr[i] == ')')
//					{
//						if (parentheses_left_no > 0){
//							parentheses_left_no--;
//							if (parentheses_left_no == 0)
//								startNotin = false;
//						}
//						else
//							startNotin = false;
//					}
//				}
//				
//				start = i+1;
//			}
//			
//		}
//		if (!list.contains((int)arr[len-1]))
//		{
//			String text = null;
//			if (arr[start] == '\"' && arr[len-1] =='\"' && len-start>2){
//				text = new String(arr,start+1,len-start-2);
//				if (startNotin)
//					nosb.append('\"').append(AnalyzerManager.analyzeStandardWord(text)).append('\"');
//				else
//					sb.append('\"').append(text).append('\"');
//			}
//			else{
//				text = new String(arr,start,len-start);
//				if (startNotin)
//					nosb.append(AnalyzerManager.analyzeStandardWord(text));
//				else
//					sb.append(analyzeSyntaxWord(text));
//			}
//		}
//		if (parentheses_right < parentheses_left)
//		{
//			for (int i=0;i<parentheses_left - parentheses_right;i++)
//			{
//				sb.append(")");
//			}
//		}
//		return new String[]{sb.toString(),nosb.length()>0?nosb.toString():null};
//	
//	}
	
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
//		return prepareAndAnalyzeWord(true,words);
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
	
	public static String toLowerCase(String s)
	{
		 char[] buffer = s.toCharArray();
		 for(int i=0;i<buffer.length;i++){
	    	  switch(buffer[i]) {
	    	  //0-9 
	    	  	case(65296):
		    		buffer[i] = 48;
	    	  		break;
	    	  	case(65297):
		    		buffer[i] = 49;
	    	  		break;
	    	  	case(65298):
	    	  		buffer[i] = 50;
   	  			break;
	    	  	case(65299):
	    	  		buffer[i] = 51;
	    	  		break;
	    	  	case(65300):
	    	  		buffer[i] = 52;
   	  			break;
	    	  	case(65301):
	    	  		buffer[i] = 53;
   	  			break;
	    	  	case(65302):
	    	  		buffer[i] = 54;
   	  			break;
	    	  	case(65303):
	    	  		buffer[i] = 55;
   	  			break;
	    	  	case(65304):
	    	  		buffer[i] = 56;
   	  			break;
	    	  	case(65305):
	    	  		buffer[i] = 57;
   	  			break;
	    	  	default:
		    		buffer[i] = Character.toLowerCase(buffer[i]);
	    	  }
	      }
		 return new String(buffer);
	}
	
	public static int decodeVideoUrl(String url)
	{
		return decodeVideoId(url.substring(url.lastIndexOf("_")+1,url.lastIndexOf(".")));
	}
	
	public static double getBoost(int number)
	{
		if (number == 0)
			return 0;
		return Math.log(number);
	}
}
