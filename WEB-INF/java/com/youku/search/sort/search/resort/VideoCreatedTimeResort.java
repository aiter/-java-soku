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

/**
 * 
 * @author gaosong
 */
public class VideoCreatedTimeResort extends AbstractVideoResort<Video, DefaultResponse> {

	public static final VideoCreatedTimeResort I = new VideoCreatedTimeResort();
	
	private VideoCreatedTimeResort() {
	}
	
	@Override
	public List<DefaultResponse> resort(SearchContext<Video> context, MergedResult<DefaultResponse> searchResults) {
		List<DefaultResponse> resortedList = new LinkedList<DefaultResponse>();
		
//		// 归类
		List<DefaultResponse> newResults = new ArrayList<DefaultResponse>();
		List<DefaultResponse> otherBlurResults = new ArrayList<DefaultResponse>();
		List<DefaultResponse> otherResults = new ArrayList<DefaultResponse>();
		
		for (DefaultResponse response : searchResults.list) {
			switch (response.type) {
			case NEW:
				newResults.add(response);
				break;
			case HOT:
			case CLASSICAL:
			case OTHER:
			default:
				if (!response.isBlur) {
					otherResults.add(response);
				} else {
					otherBlurResults.add(response);
				}
				break;
			}
		}
		
//		// 最新的
		Collections.sort(newResults, context.comparator);
		resortedList.addAll(newResults);
		
		Comparator createTimeComparator = ResortUtil.getCreatedTimeComparator(context.p.type);
//		// 除最新以外的精确匹配结果
		Collections.sort(otherResults, createTimeComparator);
		resortedList.addAll(otherResults);
//		
//		// 除最新以外的模糊匹配结果
		Collections.sort(otherBlurResults, createTimeComparator);
		resortedList.addAll(otherBlurResults);
		
		return resortedList;
	}
	
}
