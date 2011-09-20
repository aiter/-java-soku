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
public class NumberAnalyzer extends Analyzer {
	public TokenStream tokenStream(String filename, Reader reader) {
		return new NumberTokenizer(reader);
	}
}
