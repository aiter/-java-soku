package com.youku.search.sort.search.resort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.youku.search.index.entity.DefaultResponse;
import com.youku.search.index.entity.DefaultResponse.TYPE_ENUM;
import com.youku.search.index.entity.Video;
import com.youku.search.pool.net.Lock.MergedResult;
import com.youku.search.sort.core.SearchContext;
import com.youku.search.sort.util.DuplicateUtil;

public class VideoResort extends AbstractVideoResort<Video, DefaultResponse> {
	
	public static final VideoResort I = new VideoResort();
	
	private VideoResort() {
	}
	
	private static final int TOP_HOT_COUNT_PER_PAGE = 4;	// 在每一页最前面放最热的个数
	private static final int NEW_COUNT_PER_PAGE = 2;		// 在每一页放最新的个数
	private static final int CLASSICAL_COUNT = 2;	// 在第一页放经典的个数
	
	@Override
	public List<DefaultResponse> resort(SearchContext<Video> context, MergedResult<DefaultResponse> searchResults) {
		// 根据最热做预排序（为了放第一页前四个最热的位置）
		Comparator<DefaultResponse> hotComparator = ResortUtil.getHotComparator(context.type);
		List<DefaultResponse> topHotResults = new ArrayList<DefaultResponse>();
		List<DefaultResponse> results = new ArrayList<DefaultResponse>();
		for (DefaultResponse response : searchResults.list) {
			if ((response.type == TYPE_ENUM.HOT || response.type == TYPE_ENUM.CLASSICAL || response.type == TYPE_ENUM.OTHER) && 
					!response.isBlur && response.score15Days > DefaultResponse.MIN_SCORE15) {
				topHotResults.add(response);
			} else {
				results.add(response);
			}
		}
		Collections.sort(topHotResults, hotComparator);
		List<DefaultResponse> topHotMergedResults = ResortUtil.getFirstPageMergedResults(
				DuplicateUtil.remove(topHotResults), TOP_HOT_COUNT_PER_PAGE, results);
		
		// 归类
		List<DefaultResponse> newResult = new ArrayList<DefaultResponse>();
		List<DefaultResponse> classicalResult = new ArrayList<DefaultResponse>();
		List<DefaultResponse> otherPageResults = new ArrayList<DefaultResponse>();
		HotVideoCategoryResort videoCategoryResort = new HotVideoCategoryResort();
		
		for (DefaultResponse response : results) {
			switch (response.type) {
			case NEW:
				newResult.add(response);
				break;
			case HOT:
			case OTHER:
				if (!response.isBlur && response.score15Days > DefaultResponse.MIN_SCORE15) {
					videoCategoryResort.addCategoryResponse(response);
				} else {
					otherPageResults.add(response);
				}
				break;
			case CLASSICAL:
				if (!response.isBlur) {
					classicalResult.add(response);
				} else {
					otherPageResults.add(response);
				}
				break;
			default:
				break;
			}
		}
		
		// 最新、经典按语义排序
		LinkedList<DefaultResponse> newOtherPageMergedResults = new LinkedList<DefaultResponse>();
		List<DefaultResponse> newMergedResult = ResortUtil.sortedByScore(context.comparator,
				newResult, NEW_COUNT_PER_PAGE, newOtherPageMergedResults);
		
		List<DefaultResponse> classicalMergedResult = ResortUtil.sortedByScore(context.comparator, 
				classicalResult, CLASSICAL_COUNT, otherPageResults);
		
		// 按分类排序最热结果
		int topResultCount = topHotMergedResults.size() + newMergedResult.size() + classicalMergedResult.size();
		int maxHotResultCount = context.frontPage.page_size - topResultCount;
		List<DefaultResponse> hotMergedResults = videoCategoryResort.resort(maxHotResultCount, hotComparator, otherPageResults);
		
		// 其他模糊匹配的数据按语义做大排序
		Collections.sort(otherPageResults, context.comparator);
		LinkedList<DefaultResponse> otherPageMergedResults = (LinkedList)DuplicateUtil.resort(otherPageResults);
		
		// 洗脸
		List<DefaultResponse> lastResults = lastResort(context, topHotMergedResults, newMergedResult,
				classicalMergedResult, hotMergedResults, otherPageMergedResults, newOtherPageMergedResults);
		
		statCountLog(context, searchResults, topHotMergedResults.size(), newMergedResult.size(), 
				classicalMergedResult.size(), hotMergedResults.size(), 
				lastResults.size());
		
		return lastResults;
	}
	
	private void statCountLog(SearchContext context, MergedResult<DefaultResponse> mergedResult, 
			int topHotResortCount, int newResortCount, int classicalResortCount,
			int hotResortCount, int totalResortCount){
		
		if (logger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("--- 洗脸LOG --- ").append('\n');
			
			sb.append("queryStr=").append(context.p.queryOriginal).append(", p.type=" + context.p.type);
			sb.append('\n');
			
			int responseCount = mergedResult.list.size();
			sb.append("Response结果总数=").append(responseCount);
			sb.append(", statCount=").append(Arrays.toString(mergedResult.getStatCount()));
			sb.append('\n');
			
			sb.append("最终洗脸结果，洗脸结果总数=").append(totalResortCount);
			sb.append(", 最热（大排序）个数=").append(topHotResortCount);
			sb.append(", 最新（精确匹配+去重）个数=").append(newResortCount);
			sb.append(", 经典（精确匹配+去重）个数=").append(classicalResortCount);
			sb.append(", 分类最热（精确匹配+去重）个数=").append(hotResortCount);
			String logStr = sb.toString();
			
			if (responseCount != totalResortCount) {
				logger.error("洗脸结果错误：得到的ResponseCount与洗脸后的结果数不相等！！详细信息如下：\n" + logStr);
			} else {
				logger.debug(logStr);
			}
		}
	}

	/**
	 * 
	 * @param context
	 * @param topHotMergedResults 第一页前四个最热的结果
	 * @param newMergedResult 第一页最新结果集
	 * @param classicalMergedResult 第一页经典结果集
	 * @param hotMergedResults 第一页后面最热结果集
	 * @param otherPageMergedResults 第二页之后的结果集
	 * @param newOtherPageMergedResults 剩下的最新的结果集，在此方法中将被poll空
	 * @return
	 */
	private List<DefaultResponse> lastResort(
			SearchContext<Video> context, 
			List<DefaultResponse> topHotMergedResults,
			List<DefaultResponse> newMergedResult,
			List<DefaultResponse> classicalMergedResult,
			List<DefaultResponse> hotMergedResults,
			LinkedList<DefaultResponse> otherPageMergedResults, 
			LinkedList<DefaultResponse> newOtherPageMergedResults) {
		
		int totalCount = topHotMergedResults.size() + newMergedResult.size() + hotMergedResults.size() + 
						classicalMergedResult.size() + otherPageMergedResults.size() + newOtherPageMergedResults.size();
		List<DefaultResponse> results = new ArrayList<DefaultResponse>(totalCount);
		
		// 在第1~4位置放最热的，如果不够就放一个或者不放
		int topHotMergedResultCount = topHotMergedResults.size();
		if (topHotMergedResultCount > 0) {
			if (topHotMergedResultCount <= TOP_HOT_COUNT_PER_PAGE) {
				results.addAll(topHotMergedResults);
			} else {
				throw new IllegalStateException("洗脸异常，topHotMergedResultCount="+topHotMergedResultCount+", 大于限定值" + TOP_HOT_COUNT_PER_PAGE);
			}
		}
		
		// 在第5、6位置放最新的，如果不够就放一个或者不放
		int newMergedResultCount = newMergedResult.size();
		if (newMergedResultCount > 0) {
			if (newMergedResultCount <= NEW_COUNT_PER_PAGE) {
				results.addAll(newMergedResult);
			} else {
				throw new IllegalStateException("洗脸异常，newMergedResultCount="+newMergedResultCount+", 大于限定值" + NEW_COUNT_PER_PAGE);
			}
		}
		
		// 在第7、8位置放经典的，如果不够就放一个或者不放
		int classicalMergedResultCount = classicalMergedResult.size();
		if (classicalMergedResultCount > 0) {
			if (classicalMergedResultCount <= CLASSICAL_COUNT) {
				results.addAll(classicalMergedResult);
			} else {
				throw new IllegalStateException("洗脸异常，classicalMergedResultCount="+classicalMergedResultCount+", 大于限定值" + CLASSICAL_COUNT);
			}
		}
		
		// 在后面放剩余的最热
		int hotMergedResultCount = hotMergedResults.size();
		if (hotMergedResultCount > 0) {
			results.addAll(hotMergedResults);
		}
		
		// 从第二页开始放其它按最热排的，并且每页在5、6位置插两个最新的
		if (otherPageMergedResults.size() > 0) {
			int addTotalCount = results.size();
			int frontPageSize = context.frontPage.page_size;
			int addCount;	// 当前页插入到第几个
			for (DefaultResponse mergedResult : otherPageMergedResults) {
				// 向每页指定位置插入NEW_COUNT_PER_PAGE个最新的结果
				addCount = addTotalCount % frontPageSize;
				if (addCount == TOP_HOT_COUNT_PER_PAGE) {
					int addNewCount = 0;
					if (newOtherPageMergedResults.size() < NEW_COUNT_PER_PAGE) {
						addNewCount = newOtherPageMergedResults.size();
					} else {
						addNewCount = NEW_COUNT_PER_PAGE;
					}
					for (int i = 0; i < addNewCount; i++) {
						results.add(newOtherPageMergedResults.pollFirst());
						addTotalCount++;
					}
				}
				
				results.add(mergedResult);
				addTotalCount++;
			}
		}
		
		if (newOtherPageMergedResults.size() > 0) {
			results.addAll(newOtherPageMergedResults);
		}
		
		return results;
	}
	
	

}
