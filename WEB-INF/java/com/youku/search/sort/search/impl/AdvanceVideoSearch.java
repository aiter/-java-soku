package com.youku.search.sort.search.impl;

import java.util.List;

import org.json.JSONObject;

import com.youku.search.config.Config;
import com.youku.search.index.entity.Video;
import com.youku.search.sort.core.DuplicateSearchResult;
import com.youku.search.sort.core.IndexSearchResult;
import com.youku.search.sort.core.IndexSearcher;
import com.youku.search.sort.core.SearchContext;
import com.youku.search.sort.entity.CategoryCountBean;
import com.youku.search.sort.json.VideoConverter;
import com.youku.search.sort.search.AbstractSearchTemplate;
import com.youku.search.sort.util.VideoCategoryCount;

public class AdvanceVideoSearch extends AbstractSearchTemplate<Video> {

	public static class AdvanceVideoSearchResult extends SearchResult<Video> {

		public int[] dupCount;
		public List<CategoryCountBean> categoryCount;

		public AdvanceVideoSearchResult(
				IndexSearchResult<Video> indexSearchResult) {
			super(indexSearchResult);
		}
	}

	public static final String[] searchFields = { "title", "tags_index", "memo" };

	public static final AdvanceVideoSearch I = new AdvanceVideoSearch();

	public AdvanceVideoSearch() {
		super("advvideo", ShieldChannelTarget.VIDEO);
	}

	@Override
	protected SearchResult<Video> getSearchResult(SearchContext<Video> context) {

		boolean count = false;
		List<CategoryCountBean> categoryCount = null;

		if (Config.getDuplicateStatus() > 0) {

			DuplicateSearchResult<Video> duplicateSearchResult = IndexSearcher
					.searchWithDuplicate(context);

			AdvanceVideoSearchResult result = new AdvanceVideoSearchResult(
					duplicateSearchResult);

			categoryCount = count ? VideoCategoryCount.getInstance().count(
					duplicateSearchResult.list,
					duplicateSearchResult.totalCount) : null;

			result.dupCount = duplicateSearchResult.dupCountPage();
			result.categoryCount = categoryCount;

			return result;

		} else {

			IndexSearchResult<Video> indexSearchResult = IndexSearcher
					.search(context);

			AdvanceVideoSearchResult result = new AdvanceVideoSearchResult(
					indexSearchResult);

			categoryCount = count ? VideoCategoryCount.getInstance().count(
					indexSearchResult.list, indexSearchResult.totalCount)
					: null;

			result.dupCount = null;
			result.categoryCount = categoryCount;

			return result;
		}

	}

	@Override
	protected JSONObject buildJSONObject(SearchResult<Video> result) {
		AdvanceVideoSearchResult r = (AdvanceVideoSearchResult) result;
		JSONObject jsonObject = VideoConverter.convert(r);
		return jsonObject;
	}

}
