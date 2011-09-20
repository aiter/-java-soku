package com.youku.search.sort.search;

import com.youku.search.sort.core.IndexSearchResult;
import com.youku.search.sort.core.IndexSearcher;
import com.youku.search.sort.core.SearchContext;

public abstract class SimpleAbstractSearch<T> extends AbstractSearchTemplate<T> {

	public SimpleAbstractSearch(String search_name) {
		super(search_name);
	}

	public SimpleAbstractSearch(String search_name, ShieldChannelTarget target) {
		super(search_name, target);
	}

	@Override
	protected final SearchResult<T> getSearchResult(SearchContext<T> context) {

		IndexSearchResult<T> indexSearchResult = IndexSearcher.search(context);

		SearchResult<T> result = new SearchResult<T>(indexSearchResult);

		return result;
	}
}
