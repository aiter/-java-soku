package com.youku.search.sort.search.resort;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.youku.search.index.entity.DefaultResponse;

/**
 * 注意，此类非线程安全的
 * 
 * @author gaosong
 * 
 */
public abstract class CategoryResort<T> {

	protected Map<Integer, List<T>> hotCategoryMap = new HashMap<Integer, List<T>>();
	
	CategoryResort() {
	}
	
	/**	
	 * 将response放入分类Map中
	 * 
	 * @param response
	 */
	public abstract void addCategoryResponse(T response);
	
	public abstract List<T> resort(int maxResortCount, Comparator<T> hotComparator, List<T> otherPageResults);
	
}
