package com.youku.soku.suggest.parser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VersionNameFilter {
	
	public static List<String> ANIME_VERSION_NAME_FILTER;
	
	static {
		List<String> animeVerionNameList = Arrays.asList("tv版");
		ANIME_VERSION_NAME_FILTER = Collections.unmodifiableList(animeVerionNameList);
	}
}
