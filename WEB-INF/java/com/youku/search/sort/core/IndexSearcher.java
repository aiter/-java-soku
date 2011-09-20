package com.youku.search.sort.core;

import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.pool.net.LockQuery;
import com.youku.search.pool.net.Lock.MergedResult;
import com.youku.search.sort.core.entity.Span;
import com.youku.search.sort.core.search.MultiIndexSearcher;
import com.youku.search.sort.core.search.MultiIndexSearcher.DuplicateMergedResult;

public class IndexSearcher {

	static Log logger = LogFactory.getLog(IndexSearcher.class);

	static final MultiIndexSearcher searcher = MultiIndexSearcher.I;
	
	public static <T, R> IndexSearchResult<R> search(SearchContext<T> context) {
		return search(context, context.minLuceneResultCount);
	}
	
	public static <T, R> IndexSearchResult<R> search(SearchContext<T> context,
			int minResult) {
		if (context.one_by_one) {
			return IndexSearcherOneByOne.search(context, minResult);

		} else {
			// 注意，这里有一个强制转换，是否会不安全？
			// modified by gaosong 2011-06-01
			return search(context.lockQuery, minResult, (Comparator<R>)context.comparator, context.span);
		}
	}

	public static <T> DuplicateSearchResult<T> searchWithDuplicate(
			SearchContext<T> context) {
		return searchWithDuplicate(context, context.minLuceneResultCount);
	}

	public static <T> DuplicateSearchResult<T> searchWithDuplicate(
			SearchContext<T> context, int minResult) {

		if (context.one_by_one) {
			return IndexSearcherOneByOne
					.searchWithDuplicate(context, minResult);

		} else {
			return searchWithDuplicate(context.lockQuery, minResult,
					context.comparator, context.span);
		}
	}

	public static <R> IndexSearchResult<R> search(LockQuery lockQuery,
			int minResult, Comparator<R> comparator, Span span) {
		
		MergedResult<R> merged = searcher.search(lockQuery, minResult,
				comparator);
		
		IndexSearchResult<R> result = new IndexSearchResult<R>(merged, span);

		return result;
	}

	public static <T> DuplicateSearchResult<T> searchWithDuplicate(
			LockQuery lockQuery, int minResult, Comparator<T> comparator,
			Span span) {

		DuplicateMergedResult<T> merged = searcher.searchWithDuplicate(
				lockQuery, minResult, comparator);

		DuplicateSearchResult<T> result = new DuplicateSearchResult<T>(merged,
				span);

		return result;
	}
}
