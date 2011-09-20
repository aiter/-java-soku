package com.youku.search.sort.search.impl;

import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import com.youku.search.index.entity.Bar;
import com.youku.search.sort.core.IndexSearchResult;
import com.youku.search.sort.core.IndexSearcher;
import com.youku.search.sort.core.SearchContext;
import com.youku.search.sort.json.BarConverter;
import com.youku.search.sort.search.AbstractSearchTemplate;

public class BarSearch extends AbstractSearchTemplate<Bar> {

	public static final BarSearch I = new BarSearch();

	public BarSearch() {
		super("bar_name");
	}

	@Override
	protected SearchResult<Bar> getSearchResult(SearchContext<Bar> context) {

		final String query = context.p.query;

		// 需要从查询结果中把全匹配的删除掉
		final int oldPageSize = context.p.pageSize;
		final int newPageSize = oldPageSize + 1;
		context.p.pageSize = newPageSize;

		SearchContext<Bar> sc = new SearchContext<Bar>(context.p,
				context.indexPageSize);
		IndexSearchResult<Bar> indexSearchResult = IndexSearcher.search(sc);
		SearchResult<Bar> result = new SearchResult<Bar>(indexSearchResult);

		// 删除...
		final List<Bar> page = result.page;
		if (page != null && !page.isEmpty()) {
			for (Iterator<Bar> i = page.iterator(); i.hasNext();) {
				Bar bar = i.next();
				if (query.equals(bar.bar_name)) {
					i.remove();
					result.total--;
					break;
				}
			}

			while (page.size() > oldPageSize) {
				page.remove(page.size() - 1);
			}

			result.zero_result = page.isEmpty();
		}

		return result;
	}

	@Override
	protected JSONObject buildJSONObject(SearchResult<Bar> result) {
		return BarConverter.convert(result.page, result.total);
	}

}
