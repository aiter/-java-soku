package com.youku.search.sort.search.resort;

import java.util.Collections;
import java.util.List;

import com.youku.search.pool.net.Lock.MergedResult;
import com.youku.search.sort.core.SearchContext;

/**
 * 只排序，不去重
 * 
 * @author gaosong
 *
 * @param <Video>
 * @param <DefaultResponse>
 */
public class VideoNoMergeResort<Video, DefaultResponse> extends
		AbstractVideoResort<Video, DefaultResponse> {

	public static final VideoNoMergeResort I = new VideoNoMergeResort();

	private VideoNoMergeResort() {
	}

	@Override
	public List<DefaultResponse> resort(SearchContext<Video> context, MergedResult<DefaultResponse> searchResults) {
		Collections.sort(searchResults.list, context.comparator);
		return searchResults.list;
	}
	
}
