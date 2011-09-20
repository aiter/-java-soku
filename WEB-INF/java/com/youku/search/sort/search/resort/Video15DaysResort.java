package com.youku.search.sort.search.resort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.youku.search.index.entity.DefaultResponse;
import com.youku.search.index.entity.Video;
import com.youku.search.pool.net.Lock.MergedResult;
import com.youku.search.sort.core.SearchContext;
import com.youku.search.sort.util.DuplicateUtil;

public class Video15DaysResort extends AbstractVideoResort<Video, DefaultResponse> {

	public static final Video15DaysResort I = new Video15DaysResort();
	
	private Video15DaysResort(){}
	
	@Override
	public List<DefaultResponse> resort(SearchContext<Video> context,
			MergedResult<DefaultResponse> searchResults) {
		List<DefaultResponse> resortedList = new LinkedList<DefaultResponse>();
		
		// 归类
		List<DefaultResponse> results = new ArrayList<DefaultResponse>();
		List<DefaultResponse> blurResults = new ArrayList<DefaultResponse>();
		
		for (DefaultResponse response : searchResults.list) {
			switch (response.type) {
			case NEW:
				results.add(response);
				break;
			case HOT:
			case CLASSICAL:
			case OTHER:
			default:
				if (!response.isBlur) {
					results.add(response);
				} else {
					blurResults.add(response);
				}
				break;
			}
		}
		
		Comparator<DefaultResponse> hotComparator = ResortUtil.getHotComparator(context.type);
		
		// 精确匹配的结果
		Collections.sort(results, hotComparator);
		resortedList.addAll(DuplicateUtil.resort(results));
		
		// 模糊匹配的结果
		Collections.sort(blurResults, hotComparator);
		resortedList.addAll(DuplicateUtil.resort(blurResults));
		
		return resortedList;
	}

}
