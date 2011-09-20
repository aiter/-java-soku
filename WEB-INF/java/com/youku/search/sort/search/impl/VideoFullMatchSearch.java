package com.youku.search.sort.search.impl;

import org.json.JSONObject;

import com.youku.search.index.entity.Video;
import com.youku.search.sort.json.VideoConverter;
import com.youku.search.sort.search.SimpleAbstractSearch;
import com.youku.search.sort.search.impl.VideoSearch.VideoSearchResult;

public class VideoFullMatchSearch extends SimpleAbstractSearch<Video> {

	public static final VideoFullMatchSearch I = new VideoFullMatchSearch();

	public VideoFullMatchSearch() {
		super("video_full", ShieldChannelTarget.VIDEO);
	}

	@Override
	protected JSONObject buildJSONObject(SearchResult<Video> result) {

		VideoSearchResult searchResult = new VideoSearchResult(result);

		return VideoConverter.convert(searchResult);
	}

}
