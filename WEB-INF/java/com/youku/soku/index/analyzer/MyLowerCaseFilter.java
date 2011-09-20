/**
 * 
 */
package com.youku.soku.index.analyzer;


import java.io.IOException;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

/**
 * @author william
 *	格式化全角半角以及大小写
 */
public class MyLowerCaseFilter extends TokenFilter {
	public MyLowerCaseFilter(TokenStream in) {
	    super(in);
	  }

	  public final Token next(Token result) throws IOException {
	    result = input.next(result);
	    if (result != null) {

	      char[] buffer = result.termBuffer();
	      String s = null;
	      String k = result.termText();
	      
	      if ((s = WordProcessor.numberMap.get(k)) != null)
	      {
	    	  return new Token(s,result.startOffset(),result.endOffset());
	      }
	      
	      boolean hasChange = false;
	      
	      int length = result.termLength();
	      for(int i=0;i<length;i++){
	    	  switch(buffer[i]) {
	    	  //0-9 
	    	  	case(65296):
		    		buffer[i] = 48;
	    	  		hasChange = true;
	    	  		break;
	    	  	case(65297):
		    		buffer[i] = 49;
	    	  		hasChange = true;
	    	  		break;
	    	  	case(65298):
	    	  		buffer[i] = 50;
	    	  		hasChange = true;
    	  			break;
	    	  	case(65299):
	    	  		buffer[i] = 51;
	    	  		hasChange = true;
	    	  		break;
	    	  	case(65300):
	    	  		buffer[i] = 52;
	    	  		hasChange = true;
    	  			break;
	    	  	case(65301):
	    	  		buffer[i] = 53;
	    	  		hasChange = true;
    	  			break;
	    	  	case(65302):
	    	  		buffer[i] = 54;
	    	  		hasChange = true;
    	  			break;
	    	  	case(65303):
	    	  		buffer[i] = 55;
	    	  		hasChange = true;
    	  			break;
	    	  	case(65304):
	    	  		buffer[i] = 56;
	    	  		hasChange = true;
    	  			break;
	    	  	case(65305):
	    	  		buffer[i] = 57;
	    	  		hasChange = true;
    	  			break;
	    	  	default:
		    		buffer[i] = Character.toLowerCase(buffer[i]);
	    	  }
	      }
	      if (hasChange)
	      {
	    	  k = result.termText();
		      if ((s = WordProcessor.numberMap.get(k)) != null)
		      {
		    	  return new Token(s,result.startOffset(),result.endOffset());
		      }
	      }
	      
	      return result;
	    } else
	      return null;
	  }
}
