/**
 * 
 */
package com.youku.soku.index.analyzer;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

/**
 * @author 1verge
 *
 */
public class BlankAnalyzer extends Analyzer{
	public TokenStream tokenStream(String filename, Reader reader) {
		
		return new MyLowerCaseFilter(new BlankTokenizer(reader));
	}
}
