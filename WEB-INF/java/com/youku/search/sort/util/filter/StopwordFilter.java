package com.youku.search.sort.util.filter;

import java.util.Set;

import com.youku.search.sort.util.BaseFilter;

public class StopwordFilter extends BaseFilter {

	private final Set<Character> stopwordSet;
	
	public StopwordFilter(Set<Character> stopwordSet){
		this.stopwordSet = stopwordSet;
	}
	
	@Override
	protected void innerFilter(char[] chars, int i) {
		if (stopwordSet.contains(chars[i])) {
			chars[i] = ' ';
		}
	}

}
