package com.youku.search.sort.search.resort;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.youku.search.index.entity.DefaultResponse;
import com.youku.search.index.entity.Video;
import com.youku.search.pool.net.Lock.MergedResult;
import com.youku.search.sort.core.SearchContext;
import com.youku.search.sort.util.DuplicateUtil;

public class VideoUnresort extends AbstractVideoResort<Video, DefaultResponse> {
	
	public static final VideoUnresort I = new VideoUnresort();
	
	private VideoUnresort() {
	}
	
	@Override
	public List<DefaultResponse> resort(SearchContext<Video> context, MergedResult<DefaultResponse> searchResults) {
		List<DefaultResponse> resortedList = new LinkedList<DefaultResponse>();
		Collections.sort(searchResults.list, context.comparator);
		resortedList.addAll(DuplicateUtil.resort(searchResults.list));
		return resortedList;
	}

}
