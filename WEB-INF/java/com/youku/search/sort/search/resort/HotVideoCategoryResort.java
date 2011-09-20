package com.youku.search.sort.search.resort;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.youku.search.index.entity.DefaultResponse;
import com.youku.search.sort.util.DuplicateUtil;
import com.youku.search.sort.util.DuplicateUtil.DuplicateResult;
import com.youku.search.util.Constant.VideoCategoryConstant;

public class HotVideoCategoryResort extends CategoryResort<DefaultResponse> {
	
	/**
	 * C-Server返回的所有分类总个数
	 */
	private int size;
	
	/**
	 * 第一页最多返回多少个分类结果
	 */
	private int firstPageMaxResortCount;
	
	private TreeMap<HotCategoryKey, LinkedList<DefaultResponse>> categoryTreeMap = 
		new TreeMap<HotCategoryKey, LinkedList<DefaultResponse>>(hotCategoryComparator);
	
	private static final Comparator<HotCategoryKey> hotCategoryComparator = new Comparator<HotCategoryKey>() {
		@Override
		public int compare(HotCategoryKey o1, HotCategoryKey o2) {
			// 只要有电影，就往前放
			if (o1.cateId == VideoCategoryConstant.MOVIE) {
				return -1;
			} else if (o2.cateId == VideoCategoryConstant.MOVIE) {
				return 1;
			} else {
				// 其它类别，按百分比倒序排序
				if (o1.percent < o2.percent) {
					return 1;
				} else if (o1.percent > o2.percent) {
					return -1;
				} else {
					// 如果比例相同，再根据类别区分（由于categoryTreeMap的每个Entry是一个Category，所以类别不可能重复）
					if (o1.cateId == o2.cateId) {
						return 0;
					} else {
						return 1;
					}
				}
			}
		}
	};
	
	@Override
	public void addCategoryResponse(DefaultResponse response) {
		if (!hotCategoryMap.containsKey(response.category)) {
			hotCategoryMap.put(response.category, new LinkedList<DefaultResponse>());
		}
		List<DefaultResponse> hotResult = hotCategoryMap.get(response.category);
		hotResult.add(response);
		size++;
	}
	
	@Override
	public List<DefaultResponse> resort(int maxResortCount, Comparator<DefaultResponse> hotComparator, List<DefaultResponse> otherPageResults) {
		List<DefaultResponse> hotMergedResults = new LinkedList<DefaultResponse>();
		if (size == 0) {
			return hotMergedResults;
		}
		
		if (size < maxResortCount) {
			this.firstPageMaxResortCount = size;
		} else {
			this.firstPageMaxResortCount = maxResortCount;
		}
		
		// 对每个分类的结果进行排序，然后等比缩小到第一页
		int tempResultCount = 0;
		for (Map.Entry<Integer, List<DefaultResponse>> categoryEntry : hotCategoryMap.entrySet()) {
			int cateId = categoryEntry.getKey();
			List<DefaultResponse> hotCategoryResult = categoryEntry.getValue();
			
			Collections.sort(hotCategoryResult, hotComparator);
			DuplicateResult<DefaultResponse> mergedCategoryResults = DuplicateUtil.remove(hotCategoryResult);
			
			float percent = (float) hotCategoryResult.size() / size;	// 当前分类占总数的百分比
			float firstPageCategoryCount = firstPageMaxResortCount * percent;	// 等比缩小后的个数
			if (firstPageCategoryCount < 1) {
				firstPageCategoryCount = 1;
			}
			
			LinkedList<DefaultResponse> firstPageHotCategoryResults = ResortUtil.getFirstPageMergedResults(
					mergedCategoryResults, (int)firstPageCategoryCount, otherPageResults);
			tempResultCount += firstPageHotCategoryResults.size();
			
			categoryTreeMap.put(new HotCategoryKey(cateId, percent),  firstPageHotCategoryResults);
		}
		
		// 等比缩小的后处理，如果总数大于firstPageMaxResortCount，则每个分类减少一些，直到第一页能放得下
		int decreaseMaxCount = tempResultCount - firstPageMaxResortCount;
		int decreaseCount = 0;
		boolean isDecreased = false;
		for (int i = 0; i < decreaseMaxCount; i++) {
			for (LinkedList<DefaultResponse> categoryResults : categoryTreeMap.values()) {
				// 每个分类取出得分最低的一个放入otherPageResults
				if (categoryResults.size() > 1) {
					otherPageResults.add(categoryResults.pollLast());
					decreaseCount++;
					if (decreaseCount >= decreaseMaxCount) {
						isDecreased = true;
						break;
					}
				}
			}
			
			if (isDecreased) {
				break;
			}
		}
		
		// 汇总最终数据
		for (LinkedList<DefaultResponse> categoryResults : categoryTreeMap.values()) {
			hotMergedResults.addAll(categoryResults);
		}
		
		return hotMergedResults;
	}
	
	private static final class HotCategoryKey {
		private final int cateId;
		private final float percent;
		
		public HotCategoryKey(int cateId, float percent) {
			this.cateId = cateId;
			this.percent = percent;
		}
		
		@Override
		public int hashCode() {
			return HashCodeBuilder.reflectionHashCode(this);
		}
		
		@Override
		public boolean equals(Object obj) {
			return EqualsBuilder.reflectionEquals(this, obj);
		}
	}
	
}
