package com.youku.search.sort.search.impl;

import org.json.JSONObject;

import com.youku.search.index.entity.BarPost;
import com.youku.search.sort.json.BarPostConverter;
import com.youku.search.sort.search.SimpleAbstractSearch;

public class BarPostSearch extends SimpleAbstractSearch<BarPost> {

	public static final BarPostSearch I = new BarPostSearch();

	public BarPostSearch() {
		super("bar");
	}

	@Override
	protected JSONObject buildJSONObject(SearchResult<BarPost> result) {
		return BarPostConverter.convert(result.page, result.total);
	}

}
