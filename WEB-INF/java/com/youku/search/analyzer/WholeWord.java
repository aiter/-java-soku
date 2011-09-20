/**
 * 
 */
package com.youku.search.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Token;

/**
 * @author 1verge
 *
 */
public class WholeWord {

	private List<Word> tokens = null;
	
	public void add(Token token)
	{
		if (tokens == null)
			tokens = new ArrayList<Word>();
		
		tokens.add(new Word(token));
	}
	
	public void add(String word)
	{
		if (tokens == null)
			tokens = new ArrayList<Word>();
		
		tokens.add(new Word(word));
	}
	
	public List<Word> getTokens() {
		return tokens;
	}
	
	public int size()
	{
		return tokens == null ? 0 : tokens.size();
	}
	
	public boolean isEmpty()
	{
		return tokens == null || tokens.size() == 0;
	}

	class Word{
		
		String text = null;
		Token token = null;
		boolean isToken = true;
		
		Word(Token t)
		{
			this.token = t;
		}
		
		Word(String s)
		{
			this.text = s;
			isToken = false;
		}
		
	}
}
