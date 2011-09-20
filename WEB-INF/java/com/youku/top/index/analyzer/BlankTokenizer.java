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
 * @author 1verge
 *
 */
public class BlankTokenizer extends TokenStream {
	int start=0;
	int end=0;
	String list;
	StringTokenizer st;
	Reader input = null;
	public BlankTokenizer(Reader input ){
		
		this.input = input;
		int t;
		StringBuffer sb =  new StringBuffer();
		try {
			while ((t = input.read()) > -1){
				char tt = (char)t;
					sb.append(tt);
			}
		} catch (IOException e) {
		}
		if (sb!= null)
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
	    start=end+1;
	    //end=end+temp.length();
	   }else{
	    tk=null;
	   }
	   return tk;
	}
}
