package com.youku.search.sort.search.impl;

import org.json.JSONObject;

import com.youku.search.index.entity.Video;
import com.youku.search.sort.core.SearchContext;
import com.youku.search.sort.json.VideoConverter;
import com.youku.search.sort.search.impl.VideoSearch.VideoSearchResult;

public class VideoMD5Search extends AbstractVideoSearch {

	public static final VideoMD5Search I = new VideoMD5Search();

	public VideoMD5Search() {
		super("video_md5", ShieldChannelTarget.VIDEO);
	}
	
	@Override
	protected SearchResult<Video> getSearchResult(SearchContext<Video> context) {
		VideoSearchResult result = new VideoSearchResult(getIndexSearchResult(context));
		return result;
	}
	
	@Override
	protected JSONObject buildJSONObject(SearchResult<Video> result) {
		VideoSearchResult videoResult = new VideoSearchResult(result);
		JSONObject jsonObject = VideoConverter.convert(videoResult);
		return jsonObject;
	}
}
