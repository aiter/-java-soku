/**
 * 
 */
package com.youku.soku.index.query;

import org.apache.lucene.search.highlight.Highlighter;

/**
 * @author 1verge
 *
 */
public class HighlightCondition {
	
	Highlighter highlighter = null;
	
	public HighlightCondition(int[] fields,Highlighter highlighter)
	{
		this.highlighter  = highlighter;
		if (fields != null && fields.length > 0)
		{
			for (int i:fields)
			{
				switch (i)
				{
					case TITLE:
						highTitle = true;
						break;
					case TAGS:
						highTag = true;
						break;
					case MEMO:
						highMemo = true;
						break;
					case USERNAME:
						highUsername = true;
						break;
				}
			}
		}
	}
	public final static int TITLE =1;
	public final static int TAGS =2;
	public final static int MEMO =3;
	public final static int USERNAME =4;
	
	private boolean highTitle ;
	private boolean highTag ;
	private boolean highMemo ;
	private boolean highUsername ;
	

	public boolean isHighMemo() {
		return highMemo;
	}

	public boolean isHighTag() {
		return highTag;
	}

	public boolean isHighTitle() {
		return highTitle;
	}

	public boolean isHighUsername() {
		return highUsername;
	}

	public Highlighter getHighlighter() {
		return highlighter;
	}
	
	
	
}
