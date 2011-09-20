/**
 * 
 */
package com.youku.search.analyzer;

import java.io.IOException;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

import com.youku.search.util.MyUtil;


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

	      String k = result.termText();
	      
		    String s = MyUtil.toLowerCase(k);
		    return new Token(s,result.startOffset(),result.endOffset());
	      
	    } else
	      return null;
	  }
}
