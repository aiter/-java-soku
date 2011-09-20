package com.youku.search.sort.search.impl;

import java.util.List;

import org.json.JSONObject;

import com.youku.search.index.entity.Folder;
import com.youku.search.sort.core.DuplicateSearchResult;
import com.youku.search.sort.core.IndexSearchResult;
import com.youku.search.sort.core.IndexSearcher;
import com.youku.search.sort.core.SearchContext;
import com.youku.search.sort.entity.CategoryCountBean;
import com.youku.search.sort.json.FolderConverter;
import com.youku.search.sort.search.AbstractSearchTemplate;
import com.youku.search.sort.util.FolderCategoryCount;

public class AdvanceFolderSearch extends AbstractSearchTemplate<Folder> {

	public static class AdvanceFolderSearchResult extends SearchResult<Folder> {
		public List<CategoryCountBean> categoryCount;

		public AdvanceFolderSearchResult(
				IndexSearchResult<Folder> indexSearchResult) {
			super(indexSearchResult);
		}
	}

	public static final String[] searchFields = { "folder_name", "tags_index",
			"description" };

	public static final AdvanceFolderSearch I = new AdvanceFolderSearch();

	public AdvanceFolderSearch() {
		super("advfolder", ShieldChannelTarget.FOLDER);
	}

	@Override
	protected SearchResult<Folder> getSearchResult(SearchContext<Folder> context) {

		DuplicateSearchResult<Folder> searchResult = IndexSearcher
				.searchWithDuplicate(context);

		boolean count = false;

		List<CategoryCountBean> countBeanList = count ? FolderCategoryCount
				.getInstance()
				.count(searchResult.list, searchResult.totalCount) : null;

		AdvanceFolderSearchResult result = new AdvanceFolderSearchResult(
				searchResult);

		result.categoryCount = countBeanList;

		return result;
	}

	@Override
	protected JSONObject buildJSONObject(SearchResult<Folder> result) {
		AdvanceFolderSearchResult r = (AdvanceFolderSearchResult) result;
		JSONObject jsonObject = FolderConverter.convert(r);
		return jsonObject;
	}

}
