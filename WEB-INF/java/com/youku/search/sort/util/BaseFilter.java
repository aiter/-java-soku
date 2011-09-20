package com.youku.search.sort.util;

public abstract class BaseFilter {

	public void doFilter(char[] chars, int i) {
		if (null == chars || chars.length == 0) {
			return;
		}
		if (chars.length < i+1) {
			return;
		}
		
		innerFilter(chars, i);
	}
	
	protected abstract void innerFilter(char[] chars, int i);
	
}
