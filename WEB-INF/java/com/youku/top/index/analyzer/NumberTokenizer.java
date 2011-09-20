/**
 * 
 */
package com.youku.top.index.analyzer;


import java.io.IOException;
import java.io.Reader;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

/**
 * @author william
 *
 */
public class NumberTokenizer extends TokenStream {

	int start=0;
	int end=0;
	String list;
	StringTokenizer st;
	Reader input = null;
	public NumberTokenizer(Reader input ){
		
		this.input = input;
		StringBuffer sb =  new StringBuffer();
		int t;
		try {
			while ((t = input.read()) > -1){
				if (t>=48 && t<=57)
					sb.append(" ");
				sb.append( (char)t);
					
			}
		} catch (IOException e) {
			
		}
		st=new StringTokenizer(sb.toString()," ");
	}
	String temp;
	Token tk;

	/**
     * */
	@Override
	public Token next() throws IOException {
		if (st == null )return null;
	   if(st.hasMoreTokens()){
	    temp=st.nextToken();
	    end=start+temp.length();
	    tk=new Token(temp,start,end);
	    start=end;
	    //end=end+temp.length();
	   }else{
	    tk=null;
	   }
	   return tk;
	}

}
