package com.youku.search.sort.search.impl;

import org.json.JSONObject;

import com.youku.search.index.entity.Pk;
import com.youku.search.sort.json.PKConverter;
import com.youku.search.sort.search.SimpleAbstractSearch;

public class PkSearch extends SimpleAbstractSearch<Pk> {

	public static final PkSearch I = new PkSearch();

	public PkSearch() {
		super("pk");
	}

	@Override
	protected JSONObject buildJSONObject(SearchResult<Pk> result) {

		JSONObject jsonObject = PKConverter.convert(result.page, result.total);
		return jsonObject;
	}

}
