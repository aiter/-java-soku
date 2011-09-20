/**
 * 
 */
package com.youku.search.analyzer;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

import com.youku.search.analyzer.WholeWord.Word;
import com.youku.search.util.Constant;

/**
 * @author william
 */
public class MyTokenizer extends TokenStream {
	
	int start=0;
	int end=0;
	StringTokenizer st;
	Reader input = null;
	List<Token> tokens = new ArrayList<Token>();
	Iterator<Token> it = null;
	
	public MyTokenizer(boolean format,Reader input ){
		this(format,input ,null);
	}
	
	public MyTokenizer(boolean format,Reader input ,WholeWord wholeWord){
		if(wholeWord != null && !wholeWord.isEmpty()){
			
			List<Word> words = wholeWord.getTokens();
			for(Word word:words)
			{
				if(word.isToken)
				{
					tokens.add(word.token);
					start = word.token.endOffset();
				}
				else
				{
					StringBuffer sb = new StringBuffer();
					
					char[] chars = word.text.toCharArray();
					for (char c:chars)
					{
						sb.append(format(format,c));
					}
					if (sb.toString().isEmpty()){
						start += word.text.length();
						continue;
					}
					
					String text = AnalyzerManager.analyzeWord(sb.toString());
					st=new StringTokenizer(text," ");
					while(st.hasMoreTokens())
					{
						String temp =st.nextToken();
						
					    end=start+temp.length();
					    
					    tokens.add(new Token(temp,start,end));
					    start = end;
					}
				}
			}
			
		}
		else
		{
			this.input = input;
			StringBuffer sb =  new StringBuffer();
			int t;
			try {
				while ((t = input.read()) > -1){
					char tt = (char)t;
					sb.append(format(format,tt));
				}
			} catch (IOException e) {
				
			}
			String s = AnalyzerManager.analyzeWord(sb.toString());
			if (s!= null)
				st=new StringTokenizer(s," ");
			
			if (st != null){
				while(st.hasMoreTokens())
				{
					String temp =st.nextToken();
				    end=start+temp.length();
				    tokens.add(new Token(temp,start,end));
				    
				    
				    start = end;
				}
			}
			it = tokens.iterator();
		}
		
		it = tokens.iterator();
	}

	private char format(boolean format,char tt)
	{
		if (format){
			if (Constant.StopWords.getStopSet().contains(tt))
				return ' ';
			else
				return tt;
		}
		else{
			if (tt == ' '||Constant.StopWords.getStopSet().contains(tt))
				return '`';
			else
				return tt;
		}
	}
	
	/**
     * 实现的核心方法
     * */
	public Token next() throws IOException {
		if (it!= null && it.hasNext())
			return it.next();
		else 
			return null;
	}

}
