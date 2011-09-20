package com.youku.search.sort.core;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.index.entity.Result;
import com.youku.search.pool.net.Lock.MergedResult;
import com.youku.search.pool.net.Lock.NotMergedResult;
import com.youku.search.pool.net.LockQuery;
import com.youku.search.sort.core.entity.Span;
import com.youku.search.sort.core.search.MultiIndexSearcher;
import com.youku.search.sort.core.search.MultiIndexSearcher.DuplicateMergedResult;
import com.youku.search.sort.core.search.QueryObjectUtil;
import com.youku.search.sort.util.DuplicateUtil;
import com.youku.search.sort.util.DuplicateUtil.DuplicateResult;

class IndexSearcherOneByOne {

	static class FetchInfo {
		// 查询结果总数
		public int total;

		// 是否丢失结果
		public boolean miss;

		// 查询成本
		public List<List<Long>> cost = new ArrayList<List<Long>>();

		// 已经考虑反序排序，所有有结果的server的列表
		public List<InetSocketAddress> dataAddr = new ArrayList<InetSocketAddress>();

		// 已经考虑反序排序，查询页所在的server的列表
		public List<InetSocketAddress> pageAddr = new ArrayList<InetSocketAddress>();

		// 该请求页是否有数据
		public boolean empty_page() {
			return pageAddr.isEmpty();
		}

		// 该请求页在第一个查询index server中的偏移量
		public Span span = new Span();
	}

	static Log logger = LogFactory.getLog(IndexSearcherOneByOne.class);

	static final MultiIndexSearcher searcher = MultiIndexSearcher.I;

	public static <T, R> IndexSearchResult<R> search(SearchContext<T> context,
			int minResult) {

		InetSocketAddress[] addresses = context.lockQuery.addresses;

		// 预先抓取信息
		FetchInfo info = analyze(context, addresses);
		if (info.empty_page()) {// 结果页为空
			return emptyIndexSearchResult(context, info);
		}

		// 汇总、排序
		MergedResult<R> merged = doSearch(info, context, minResult);

		// 构造IndexSearchResult对象，返回
		final Span span = new Span(0, context.frontPage.page_size);
		IndexSearchResult<R> result = new IndexSearchResult<R>(merged, span);
		return result;
	}

	public static <T> DuplicateSearchResult<T> searchWithDuplicate(
			SearchContext<T> context, int minResult) {

		InetSocketAddress[] addresses = context.lockQuery.addresses;

		// 预先抓取信息
		FetchInfo info = analyze(context, addresses);
		if (info.empty_page()) {// 结果页为空
			return emptyDuplicateSearchResult(context, info);
		}

		// 汇总、排序
		DuplicateMergedResult<T> merged = doSearchWithDuplicate(info, context,
				minResult);

		// 构造IndexSearchResult对象，返回
		final Span span = new Span(0, context.frontPage.page_size);
		DuplicateSearchResult<T> result = new DuplicateSearchResult<T>(merged,
				span);

		return result;
	}

	// 分析信息
	private static <T> FetchInfo analyze(SearchContext<T> context,
			InetSocketAddress[] addresses) {

		final NotMergedResult<T> notMerged = searcher
				.searchNotMerged(context.lockQuery);

		final Map<SocketAddress, Result<T>> map = notMerged.map;

		FetchInfo info = new FetchInfo();

		// total allAddr
		for (InetSocketAddress address : addresses) {
			final Result result = map.get(address);
			final int thisTotal = result.totalCount;
			if (thisTotal < 1) {
				continue;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("totalCount of this session: " + thisTotal + "; "
						+ address);
			}

			info.total += thisTotal;

			if (context.p.reverse) {
				info.dataAddr.add(0, address);
			} else {
				info.dataAddr.add(address);
			}
		}

		info.miss = notMerged.miss;
		info.cost.addAll(notMerged.cost);

		if (info.total < 1 || info.total <= context.frontPage.start()) {
			// 结果页为空
		} else {

			int part_total = 0;
			int index_server_start = -1;
			for (int i = 0; i < info.dataAddr.size(); i++) {
				final int this_total = map.get(info.dataAddr.get(i)).totalCount;
				part_total += this_total;
				if (part_total > context.frontPage.start()) {
					index_server_start = i;// 找到的第一个index server
					info.span.start = context.frontPage.start()
							- (part_total - this_total);
					info.span.end = info.span.start
							+ context.frontPage.page_size;

					break;
				}
			}

			if (index_server_start < 0) {
				// 没找到

			} else {
				info.pageAddr = info.dataAddr.subList(index_server_start,
						info.dataAddr.size());
			}
		}

		if (logger.isDebugEnabled()) {
			StringBuilder builder = new StringBuilder();
			builder.append("cacheKey：" + context.cacheKey + "; ");
			builder.append("查询结果数：" + info.total + "; ");
			builder.append("最初的服务器：" + Arrays.toString(addresses) + "; ");
			builder.append("有数据的服务器：" + info.dataAddr + "; ");
			builder.append("开始查询的服务器：" + info.pageAddr + "; ");
			builder.append("miss：" + info.miss + "; ");

			logger.debug(builder.toString());
		}

		return info;
	}

	private static <T, R> IndexSearchResult<R> emptyIndexSearchResult(
			SearchContext<T> context, FetchInfo info) {

		IndexSearchResult<R> result = new IndexSearchResult<R>();

		result.totalCount = info.total;
		// result.list ; //noop
		result.miss = info.miss;
		result.searchCount = 1;
		result.cost.addAll(info.cost);

		// 分页信息
		// noop

		return result;
	}

	private static <T> DuplicateSearchResult<T> emptyDuplicateSearchResult(
			SearchContext<T> context, FetchInfo info) {

		DuplicateSearchResult<T> result = new DuplicateSearchResult<T>();

		result.totalCount = info.total;
		// result.list ; //noop
		result.miss = info.miss;
		result.searchCount = 1;
		result.cost.addAll(info.cost);

		// 分页信息
		// noop

		return result;
	}

	private static <T, R> MergedResult<R> doSearch(FetchInfo info,
			SearchContext<T> context, final int minResult) {

		return doSearch(info, context, minResult, false);
	}

	private static <T, R> MergedResult<R> doSearch(FetchInfo info,
			SearchContext<T> context, final int minResult, boolean excludeLast) {

		MergedResult<R> merged = new MergedResult<R>();
		merged.totalCount = info.total;
		merged.miss = merged.miss || info.miss;
		merged.searchCount++;
		merged.cost.addAll(info.cost);

		int thisMinResult = minResult;

		final int addrSize = info.pageAddr.size();
		final int end_index = excludeLast ? addrSize - 1 : addrSize;

		for (int i = 0; i < end_index; i++) {
			updateQueryObject(context, info, i);

			InetSocketAddress addr = info.pageAddr.get(i);
			LockQuery lockQuery = new LockQuery(addr, context.query);
			MergedResult<R> result = searcher.search(lockQuery, thisMinResult);
			merged.merge(result);

			if (result.list.isEmpty() || merged.list.size() >= minResult) {
				break;
			}

			thisMinResult = minResult - merged.list.size();
		}

		// 注意，这里有一个强制转换，是否会不安全？
		// modified by gaosong 2011-06-01
		Collections.sort((List<R>)merged.list, (Comparator<R>)context.comparator);

		return merged;
	}

	private static <T> DuplicateMergedResult<T> doSearchWithDuplicate(
			FetchInfo info, SearchContext<T> context, final int minResult) {
		MergedResult<T> raw = new MergedResult<T>();
		raw.totalCount = info.total;
		raw.miss = info.miss;
		raw.searchCount++;
		raw.cost.addAll(info.cost);

		DuplicateResult<T> duplicateResult = null;

		int raw_size = 0;
		int new_size = 0;
		int total_diff = 0;

		final int addrSize = info.pageAddr.size();
		for (int i = 0; i < addrSize; i++) {

			updateQueryObject(context, info, i);

			LockQuery lockQuery = new LockQuery(info.pageAddr.get(i),
					context.query);

			while (true) {
				MergedResult<T> thisResult = searcher.search(lockQuery);
				raw.merge(thisResult);
				Collections.sort(raw.list, context.comparator);

				duplicateResult = DuplicateUtil.remove(raw.list);

				raw_size = raw.list.size();
				new_size = duplicateResult.result.size();
				final int this_diff = raw_size - new_size;

				// duplicateResult 始终是最后去重后的结果集，所以最终的去重数也就是最后一次的数量
				total_diff = this_diff;

				if (thisResult.list.isEmpty() || !thisResult.hasNext
						|| new_size >= minResult) {
					// 退出这个索引服务器的查询循环
					break;
				}

				QueryObjectUtil.nextPage(lockQuery.queryObject());
			}

			if (new_size >= minResult) {// 退出本次去重循环调用
				break;
			}
		}

		// 对视频查询、相关度排序情况下的特殊处理
		// DuplicateUtil.adjustScoreAndResort(duplicateResult,
		// context.comparator);

		// 返回结果
		DuplicateMergedResult<T> merged = new DuplicateMergedResult<T>();
		merged.merge(raw);// 只是用来设置部分信息
		merged.list = duplicateResult.result; // 调整去重后的信息
		merged.totalCount = raw.totalCount - total_diff; // 调整去重后的信息

		merged.md5Map.putAll(duplicateResult.md5Map);
		merged.objectMap.putAll(duplicateResult.objectMap);

		if (logger.isDebugEnabled()) {
			logger.debug("old size: " + raw_size + ", new_size: " + new_size
					+ ", diff: " + total_diff);
		}

		return merged;
	}

	private static <T> Object updateQueryObject(SearchContext<T> context,
			FetchInfo info, int index) {

		Span querySpan = new Span();

		if (index == 0) {
			querySpan.start = info.span.start;
			querySpan.end = querySpan.start + context.indexPageSize;
		} else {
			querySpan.start = 0;
			querySpan.end = querySpan.start + context.indexPageSize;
		}

		QueryObjectUtil.updateSpan(context.query, querySpan);

		return context.query;
	}

}
