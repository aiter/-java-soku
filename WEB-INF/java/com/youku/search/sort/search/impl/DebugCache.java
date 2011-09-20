package com.youku.search.sort.search.impl;

import org.json.JSONObject;

import com.youku.search.sort.Parameter;
import com.youku.search.sort.search.AbstractSearch;

// this class is for the case of having to look at whether or what is saved in memcache
public class DebugCache extends AbstractSearch {

	public static final DebugCache I = new DebugCache();

	public DebugCache() {
		super("debug");
	}

	public JSONObject search(Parameter p) {
		throw new RuntimeException("这个接口以后要重写");
	}
}
