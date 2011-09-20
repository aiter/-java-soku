package com.youku.search.sort.search.impl;

import java.util.List;

import org.json.JSONObject;

import com.youku.search.index.entity.DefaultResponse;
import com.youku.search.index.entity.Video;
import com.youku.search.pool.net.Lock.MergedResult;
import com.youku.search.sort.core.DuplicateSearchResult;
import com.youku.search.sort.core.IndexSearchResult;
import com.youku.search.sort.core.IndexSearcher;
import com.youku.search.sort.core.SearchContext;
import com.youku.search.sort.json.VideoConverter;
import com.youku.search.sort.search.AbstractSearchTemplate;
import com.youku.search.sort.search.impl.VideoSearch.VideoSearchResult;
import com.youku.search.sort.search.resort.AbstractVideoResort;
import com.youku.search.sort.search.resort.ResortFactory;
import com.youku.search.sort.util.StoreVideoUtil;

/**
 * 只要视频搜索结果，
 * 
 */
public class VideoOnlySearch extends AbstractVideoSearch {

	public static final VideoOnlySearch I = new VideoOnlySearch();

	public VideoOnlySearch() {
		super("video_only", ShieldChannelTarget.VIDEO);
	}

	@Override
	protected SearchResult<Video> getSearchResult(SearchContext<Video> context) {

		// 以前的代码
//		DuplicateSearchResult<Video> duplicateSearchResult = IndexSearcher
//				.searchWithDuplicate(context);
//
//		SearchResult<Video> result = new SearchResult<Video>(
//				duplicateSearchResult);
		
		VideoSearchResult result = new VideoSearchResult(getIndexSearchResult(context));
		return result;
	}

	@Override
	protected JSONObject buildJSONObject(SearchResult<Video> result) {
		JSONObject jsonObject = VideoConverter.convert(result);
		return jsonObject;
	}

}
