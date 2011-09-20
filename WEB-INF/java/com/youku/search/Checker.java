/**
 * 
 */
package com.youku.search;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

//import com.swabunga.spell.engine.SpellDictionaryHashMap;
//import com.swabunga.spell.engine.Word;
//import com.swabunga.spell.event.SpellChecker;

/**
 * @author 1verge
 *
 */
//public class Checker {
//
//	String dictionary = "e:/words.txt";
//	
//	private SpellChecker checker = null;
//	
//	private Checker(){
//		
//	}
//	private static Checker self = null;
//
//	public  static Checker getInstance(){
//		
//		if(self == null){
//			self = new Checker();
//			self.init();
//		}
//		return self;
//	}
//	
//	public void init()
//	{
//		checker = new SpellChecker();
//		InputStream is = null;
//		try {
//			is = new FileInputStream(dictionary);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	      try {
//	        checker.setUserDictionary(new SpellDictionaryHashMap(new InputStreamReader(is)));
//	      } catch (IOException ex) {
//	        ex.printStackTrace();
//	      }
//	      checker.setCache();
//	}
//	
//	public boolean isen(int c)
//	{
//		return (c > 64 && c< 91) ||(c > 96 && c< 123); 
//	}
//	
//	public String checkString(String s)
//	{
//		char[] chars = s.toCharArray();
//		StringBuffer result = new StringBuffer();
//		int start = -1;
//		boolean changed = false;
//		
//		for (int i=0;i<chars.length;i++)
//		{
//			char c = chars[i];
//			int b = (int)c;
//			if( isen(b) )
//			{
//				if (start == -1)
//					start = i;
//			}
//			else
//			{
//				if (start == -1)
//					result.append(c);
//				else
//				{
//					if (c == ' ' || c== '\'')
//						continue;
//					else{
//						if ( chars[i-1] == ' '){
//							String en = new String(chars,start,i-1-start);
//							String newen = checkEn(en);
//							if (newen != null){
//								changed = true;
//								result.append(newen);
//							}
//							else
//								result.append(en);
//							
//							result.append(' ').append(c);
//						}
//						else
//						{
//							String en = new String(chars,start,i-start);
//							String newen = checkEn(en);
//							if (newen != null){
//								changed = true;
//								result.append(newen);
//							}
//							else
//								result.append(en);
//							
//							result.append(c);
//						}
//						start = -1;
//					}
//				}
//			}
//		}
//		
//		if (start > -1)
//		{
//			String en = new String(chars,start,chars.length-start);
//			String newen = checkEn(en);
//			if (newen != null){
//				changed = true;
//				result.append(newen);
//			}
//			else
//				result.append(en);
//			
//			start = -1;
//		}
//		
//		return changed?result.toString():null;
//	}
//	
//	public String checkEn(String st)
//	{
//		if(st.length() < 2)
//			return null;
//		String result = null;
//		if (!checker.isCorrect(st))
//	  	  {
//	  		  List list = checker.getSuggestions(st,2);
//			      if (list != null &&list.size() > 0){
//			    	 String temp = ((Word)list.get(0)).getWord();
//			    	 System.err.println("DEBUG:直接获取到相似词组"+temp);
//			    	 if (st.indexOf(" ") > 0)
//			    	 {
//			    		 if (temp.split(" ").length == st.split(" ").length)
//			    			 result = temp;
//			    		 else
//			    			 result = checksent(st);  
//			    	 }
//			    	 else
//			    		 result = temp;
//			      }
//			      else
//			      {
//			    	  System.err.println("DEBUG:未获取到相似词组，解析单词");
//			    	  if (st.indexOf(" ")>0)
//			    		  result = checksent(st);  
//			      }
//	  	  }
//		
//		return result;
//	}
//	
//	private String checksent(String s)
//	{
//		StringTokenizer st = new StringTokenizer(s," ");
//		StringBuffer result = new StringBuffer();
//		
//		boolean changed = false;
//		while (st.hasMoreElements())
//		{
//			String token =  st.nextToken();
//			
//			if (!checker.isCorrect(token)){
//				List list = checker.getSuggestions(token,2);
//			      if (list != null &&list.size() > 0){
//			    	  changed = true;
//			    	  result.append(list.get(0)).append(" ");
//			      }
//			      else
//			    	  result.append(token).append(" ");
//			}
//			else
//				result.append(token).append(" ");
//		}
//		
//		return changed ?result.toString() :null;
//	}
//	
//	public static void main(String[] args) throws IOException
//	{
//		Checker checker = Checker.getInstance();
//		System.out.println("请输入一行字符："); 
//		String temp = null;
//		{
//			BufferedReader stdin =new BufferedReader(new InputStreamReader(System.in)); 
//			while ((temp = stdin.readLine()) != null)
//			{
//				String check = checker.checkString(temp);
//				if (check != null)
//					System.out.println(check);
//			}
//		}
//	}
//}
