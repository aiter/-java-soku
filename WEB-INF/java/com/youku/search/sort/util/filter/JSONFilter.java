package com.youku.search.sort.util.filter;

import com.youku.search.sort.util.BaseFilter;

public class JSONFilter extends BaseFilter {

	@Override
	protected void innerFilter(char[] chars, int i) {
		if (chars[i] == '\\' || chars[i] == '/' || chars[i] == '"') {
			chars[i] = ' ';
		}
	}

}
