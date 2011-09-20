package com.youku.search.sort.core;

import java.util.ArrayList;
import java.util.List;

import com.youku.search.pool.net.Lock.MergedResult;
import com.youku.search.sort.core.entity.Span;

/**
 * 在MergedResult的基础上添加分页信息
 */
public class IndexSearchResult<R> extends MergedResult<R> {

	public Span span = new Span();
	
	public IndexSearchResult() {
	}

	public IndexSearchResult(MergedResult<R> merged, Span span) {
		this.totalCount = merged.totalCount;
		this.list = merged.list;
		this.miss = merged.miss;
		this.searchCount = merged.searchCount;
		this.cost = merged.cost;
		this.hasNext = merged.hasNext;
		this.extra = merged.extra;

		// 分页信息
		this.span.start = span.start;
		this.span.end = span.end;
	}
	
	public IndexSearchResult(List<R> list, IndexSearchResult templateSearchResult){
		this.list = list;
		this.totalCount = templateSearchResult.totalCount;
		this.miss = templateSearchResult.miss;
		this.searchCount = templateSearchResult.searchCount;
		this.cost = templateSearchResult.cost;
		this.hasNext = templateSearchResult.hasNext;
		this.extra = templateSearchResult.extra;
		
		// 分页信息
		this.span.start = templateSearchResult.span.start;
		this.span.end = templateSearchResult.span.end;
	}
	
	public IndexSearchResult(List<R> list, MergedResult templateSearchResult, Span span){
		this.list = list;
		this.totalCount = templateSearchResult.totalCount;
		this.miss = templateSearchResult.miss;
		this.searchCount = templateSearchResult.searchCount;
		this.cost = templateSearchResult.cost;
		this.hasNext = templateSearchResult.hasNext;
		this.extra = templateSearchResult.extra;
		
		// 分页信息
		this.span.start = span.start;
		this.span.end = span.end;
	}

	public List<R> page() {
		List<R> page = new ArrayList<R>();
		for (int i = span.start; i < span.end && i < list.size(); i++) {
			page.add(list.get(i));
		}
		return page;
	}

	/**
	 * 视频去重信息 备注：临时的解决方案，去重信息本来不该在这个类里面
	 */
	public int[] dupCountPage() {
		int[] dupCount = new int[span.size()];
		for (int i = 0; i < dupCount.length; i++) {
			dupCount[i] = 1;
		}
		return dupCount;
	}

}
