package com.youku.search.sort.search.impl;

import org.json.JSONObject;

import com.youku.search.index.entity.Folder;
import com.youku.search.sort.core.DuplicateSearchResult;
import com.youku.search.sort.core.IndexSearcher;
import com.youku.search.sort.core.SearchContext;
import com.youku.search.sort.json.FolderConverter;
import com.youku.search.sort.search.AbstractSearchTemplate;

/**
 * 相关专辑搜索
 */
public class FolderTitleTagSearch extends AbstractSearchTemplate<Folder> {

	public static final FolderTitleTagSearch I = new FolderTitleTagSearch();

	public FolderTitleTagSearch() {
		super("folder_title_tag", ShieldChannelTarget.FOLDER);
	}

	@Override
	protected SearchResult<Folder> getSearchResult(SearchContext<Folder> context) {

		DuplicateSearchResult<Folder> duplicateSearchResult = IndexSearcher
				.searchWithDuplicate(context);

		SearchResult<Folder> result = new SearchResult<Folder>(
				duplicateSearchResult);

		return result;
	}

	@Override
	protected JSONObject buildJSONObject(SearchResult<Folder> result) {

		JSONObject jsonObject = FolderConverter.convert(result);
		return jsonObject;
	}

}
