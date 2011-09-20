package com.youku.search.sort.search.resort;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.youku.search.index.entity.DefaultResponse;
import com.youku.search.index.entity.Video;
import com.youku.search.sort.core.SearchContext;
import com.youku.search.sort.util.DuplicateUtil;
import com.youku.search.sort.util.DuplicateUtil.DuplicateResult;
import com.youku.search.sort.util.bridge.SearchUtil;
import com.youku.search.sort.util.bridge.comparator.impl.ResponseSortComparator;

public class ResortUtil {

	private ResortUtil() {
	}
	
	/**
	 * 返回第一页的非重复结果，返回的List最多有getCount个 <br>
	 * 多出来的重复或者非重复的数据会放入otherPageResults中
	 * 
	 * @param duplicateResults 去重对象
	 * @param getCount
	 *            需要从duplicateResults中取出的个数，如果实际去重后个数小于getCount，则返回实际个数；
	 *            如果实际个数大于getCount，则返回getCount个数
	 * @param otherResults 
	 * 
	 * @return 返回LinkedList，以方便进行后处理（从头/尾进行操作）
	 */
	public static <T> LinkedList<T> getFirstPageMergedResults(
			DuplicateResult<T> duplicateResults, int getCount,
			List<T> otherResults) {
		
		List<T> meregedResults = duplicateResults.result;
		int actualGetCount = (meregedResults.size() < getCount ? meregedResults.size() : getCount);
		
		LinkedList<T> firstPageMergedResults = new LinkedList<T>();
		if (meregedResults.size() > 0) {
			firstPageMergedResults.addAll(meregedResults.subList(0, actualGetCount));
		}
		
		if (meregedResults.size() > getCount) {
			otherResults.addAll(meregedResults.subList(getCount, meregedResults.size()));
		}
		
		otherResults.addAll(duplicateResults.getDuplicateList());
		
		return firstPageMergedResults;
	}

	public static <T> List<T> sortedByScore(
			Comparator<T> comparator, List<T> results) {
		Collections.sort(results, comparator);
		List<T> otherPageResultList = DuplicateUtil.resort(results);
		return otherPageResultList;
	}
	
	public static <T> List<T> sortedByScore(
			Comparator<T> comparator,
			List<T> results, int getCount,
			List<T> otherPageResults) {
		Collections.sort(results, comparator);
		DuplicateResult<T> mergedResult = DuplicateUtil.remove(results);
		
		List<T> mergedResultList = ResortUtil.getFirstPageMergedResults(
				mergedResult, getCount, otherPageResults);
		
		return mergedResultList;
	}
	
	public static <T> Comparator<T> getHotComparator(int type){
		return SearchUtil.getComparator(type, "resort_15days", true);
	}
	
	public static <T> Comparator<T> getCreatedTimeComparator(int type) {
		return SearchUtil.getComparator(type, "resort_createtime", true);
	}
	
}
