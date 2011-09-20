package com.youku.search.sort.search.impl;

import org.json.JSONObject;

import com.youku.search.index.entity.Video;
import com.youku.search.sort.core.DuplicateSearchResult;
import com.youku.search.sort.core.IndexSearcher;
import com.youku.search.sort.core.SearchContext;
import com.youku.search.sort.json.VideoConverter;
import com.youku.search.sort.search.AbstractSearchTemplate;

/**
 * 相关视频搜索
 */
public class VideoTitleTagSearch extends AbstractSearchTemplate<Video> {

	public static final VideoTitleTagSearch I = new VideoTitleTagSearch();

	public VideoTitleTagSearch() {
		super("video_title_tag", ShieldChannelTarget.VIDEO);
	}

	@Override
	protected SearchResult<Video> getSearchResult(SearchContext<Video> context) {

		DuplicateSearchResult<Video> duplicateSearchResult = IndexSearcher
				.searchWithDuplicate(context);

		SearchResult<Video> result = new SearchResult<Video>(
				duplicateSearchResult);

		return result;
	}

	@Override
	protected JSONObject buildJSONObject(SearchResult<Video> result) {

		JSONObject jsonObject = VideoConverter.convert(result);
		return jsonObject;
	}

}
