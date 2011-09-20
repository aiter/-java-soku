package com.youku.search.sort.search.impl;

import org.json.JSONObject;

import com.youku.search.index.entity.Ring;
import com.youku.search.sort.json.RingConverter;
import com.youku.search.sort.search.SimpleAbstractSearch;

public class RingSearch extends SimpleAbstractSearch<Ring> {

	public static final RingSearch I = new RingSearch();

	public RingSearch() {
		super("ring");
	}

	@Override
	protected JSONObject buildJSONObject(SearchResult<Ring> result) {
		return RingConverter.convert(result.page, result.total);
	}
}
