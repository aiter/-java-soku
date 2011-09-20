/**
 * 
 */
package com.youku.search.analyzer;

import java.io.IOException;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

/**
 * @author william
 *
 */
public class FuzzyWordCaseFilter extends TokenFilter {
	public FuzzyWordCaseFilter(TokenStream in) {
	    super(in);
	  }
	
	public final Token next(Token result) throws IOException {
	    result = input.next(result);
	    if (result != null) {

	      char[] buffer = result.termBuffer();
	      
	      int length = result.termLength();
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
	      if (result.termBuffer().length != buffer.length)
	    	  result = new Token(new String(buffer),result.startOffset(),result.endOffset());
	      return result;
	    } else
	      return null;
	  }
}
