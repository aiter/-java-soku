/**
 * 
 */
package com.youku.soku.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.json.JSONArray;
import org.json.JSONException;

import sun.misc.BASE64Decoder;

import com.youku.search.util.DataFormat;
import com.youku.soku.config.Config;
import com.youku.soku.index.analyzer.AnalyzerManager;
import com.youku.soku.index.analyzer.WholeWord;
import com.youku.soku.index.analyzer.WordProcessor;


/**
 * @author william
 *
 */
public class MyUtil {
	//根据码流json数组来返回清晰度数组： 超清，SD
	public static String getHD(JSONArray streamType){
		if(streamType==null || streamType.length()<=0) return "";
		Set<String> codeSet=new HashSet<String>();
		for(int i=0;i<streamType.length();i++){
			try {
				codeSet.add(streamType.getString(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
		}
		if(codeSet.contains("hd2")){
			return "超清,SD";
		}else if(codeSet.contains("hd")){
			return "高清,HD";
		}else return "";
		
		
		
	}
	
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

	public static String getBASE64(String s) {
		if (s == null)
			return null;
		return (new sun.misc.BASE64Encoder()).encode(s.getBytes());
	}

	// 将 BASE64 编码的字符串 s 进行解码
	public static String getFromBASE64(String s) {
		if (s == null)
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(s);
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
		finally
		{
			try {
				ts.close();
			} catch (IOException e) {
			}
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
		finally
		{
			try {
				ts.close();
			} catch (IOException e) {
			}
		}
		return sb.toString().trim();
		
	}
	
	
	public static String urlEncode(String s)
	{
		if (s==null)return "";
		try {
			return URLEncoder.encode(s,"utf8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
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
	
	public static String formatViewStage(String viewStage){
		if(viewStage==null || viewStage.length()!=8){
			return viewStage;
		}
		StringBuilder show_videostage_new = new StringBuilder();
		show_videostage_new.append(viewStage.substring(0, 4));
		show_videostage_new.append("-");
		show_videostage_new.append(viewStage.substring(4, 6));
		show_videostage_new.append("-");
		show_videostage_new.append(viewStage.substring(6, 8));
		return show_videostage_new.toString();
	}
	
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
	
	public static void main(String[] args)
	{
		String s = "XMTcyMzA3NTI0";
//		System.out.println(WordProcessor.analyzerPrepare(s)[0]);
//		System.out.println(WordProcessor.analyzerPrepare(s)[1]);
//		System.out.println(parseSyntax("四川-春天-故事 地震-搜索 \"毛孔的修复产品\" -美女+\"哈哈\""));
//		System.out.println( computeDocBoost(DataFormat.getNextDate(new Date(),-1), 100, 1, 0, 20,25));
			System.out.print(MyUtil.decodeVideoId("XMTgwNDkyOTc2"));
	}
}
