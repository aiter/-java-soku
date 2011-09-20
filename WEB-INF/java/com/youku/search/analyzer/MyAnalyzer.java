/**
 * 
 */
package com.youku.search.analyzer;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

/**
 * @author william
 * 
 */
public class MyAnalyzer extends Analyzer {
	
	protected boolean format = false;
	public MyAnalyzer(boolean format)
	{
		this.format = format;
	}
	
	public TokenStream tokenStream(String filename, Reader reader) {
		
		return new YoukuFilter (new MyLowerCaseFilter(new MyTokenizer(format,reader)));
	}
	public TokenStream tokenStream(Reader reader,WholeWord words) {
		
		return new YoukuFilter (new MyLowerCaseFilter(new MyTokenizer(format,reader,words)));
	}
}
