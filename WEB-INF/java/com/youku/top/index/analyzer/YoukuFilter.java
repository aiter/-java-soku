/**
 * 
 */
package com.youku.top.index.analyzer;


import java.io.IOException;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

/**
 * @author william
 *
 */
public class YoukuFilter extends TokenFilter {
	public YoukuFilter(TokenStream in) {
	    super(in);
	  }
	
	/**
	 * 针对6位的日期格式处理
	 */
	public final Token next(Token result) throws IOException {
	    result = input.next(result);
	    if (result != null) {
	      final char[] buffer = result.termBuffer();
	      if (result.termLength() == 6)
	      {
	    	  for (int i=0;i<6;i++)
	    	  {
	    		  if (i == 2)
	    		  {
	    			  if (buffer[i]!=48 && buffer[i] != 49)
	    				  return result;
	    		  }
	    		  else if (i == 4)
	    		  {
	    			  if (buffer[i]<48 || buffer[i] > 51)
	    				  return result;
	    		  }
	    		  else  
	    		  {
	    			  if (!isInt(buffer[i]))
	    				  return result;
	    		  }
	    	  }
	    	  if (buffer[0] == 48)
	    	  {
	    		  result.setTermText("20" + result.termText());
	    	  }
	    	  else if (buffer[0] > 55)
	    	  {
	    		  result.setTermText("19" + result.termText());
	    	  }
	      }
	      return result;
	    } else
	      return null;
	  }
	
	private boolean isInt(char c)
	{
		return c >= 48 && c <= 57;
	}
}
