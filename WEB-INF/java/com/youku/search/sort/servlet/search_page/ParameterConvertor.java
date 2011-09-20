package com.youku.search.sort.servlet.search_page;

import java.util.Map;

import com.youku.search.sort.ParameterNames;

public interface ParameterConvertor {
	Map<ParameterNames, String> convert(Map<String, String> web);
}
