package com.youku.search.sort.core.search;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.index.server.ChangeServer;
import com.youku.search.pool.net.ClientManager;
import com.youku.search.pool.net.Lock;
import com.youku.search.pool.net.Lock.MergedResult;
import com.youku.search.pool.net.Lock.NotMergedResult;
import com.youku.search.pool.net.LockQuery;
import com.youku.search.pool.net.ResultHolder;
import com.youku.search.sort.benchmark.MockVideoSearchClient.QueryStat;
import com.youku.search.sort.util.DuplicateUtil;
import com.youku.search.sort.util.DuplicateUtil.DuplicateResult;
import com.youku.search.sort.util.bridge.SearchUtil;
import com.youku.search.util.Constant.QueryField;

/**
 * 从多个远程的lucene server查询、合并结果
 */
public class MultiIndexSearcher {

	public static final MultiIndexSearcher I = new MultiIndexSearcher();

	Log logger = LogFactory.getLog(getClass());

	public <T> MergedResult<T> search(LockQuery lockQuery) {

		ClientManager manager = ClientManager.getInstance();
		Lock lock = manager.query(lockQuery);
		
		MergedResult<T> mergedResult = lock.getResultMerged();
		
		// 如果是动态服务器，将会由hasNext等规则动态的改变服务器地址，以减少下次请求量
		// modified by gaosong
		if (SearchUtil.getIsDynamicServer(lockQuery.type)) {
			lockQuery.addresses = lock.getHasNextSockets();
			
			if (logger.isDebugEnabled()) {
				StringBuilder sb = new StringBuilder();
				sb.append('[');
				for (int j = 0; j < lockQuery.addresses.length; j++) {
					sb.append(lockQuery.addresses[j].getAddress().getHostAddress() + ",");
				}
				sb.append(']');
				logger.debug("--- 动态改变的目标C-Server的IP：" + sb.toString());
				
//				QueryStat.I.addSuccessRequestCount(lock.getSuccessSockets().length);
			}
		}
		
		return mergedResult;
	}
	
	public <T> NotMergedResult<T> searchNotMerged(LockQuery lockQuery) {

		ClientManager manager = ClientManager.getInstance();
		Lock lock = manager.query(lockQuery);

		NotMergedResult<T> notMergedResult = lock.getResultNotMerged();

		return notMergedResult;
	}

//	public <T> MergedResult<T> search(LockQuery lockQuery,
//			Comparator<T> comparator) {
//
//		MergedResult<T> merged = search(lockQuery);
//		Collections.sort(merged.list, comparator);
//		return merged;
//	}

	public <T> MergedResult<T> search(LockQuery lockQuery, int minResult) {

		int i = 0;
		MergedResult<T> merged = new MergedResult<T>();

		InetSocketAddress[] originAddresses = lockQuery.addresses;
		while (true) {
			i++;
			MergedResult<T> result = search(lockQuery);
			
			merged.merge(result);
			
			if (logger.isDebugEnabled()) {
				logger.debug("------------- 一次集群请求的结果数，result.list="
						+ result.list.size() + ", 总共的结果数，result.list="
						+ merged.list.size() + ", 最少需要个数minResult=" + minResult + ", hasNext=" + result.hasNext);	
			}
			
			if (result.list.isEmpty() || merged.list.size() >= minResult
					|| !result.hasNext) {
				break;
			}
			
			QueryObjectUtil.nextPage(lockQuery.queryObject());
		}
		// 还原lockQuery.addresses，这样如果外部需要重用lockQuery的话才不会出问题
		lockQuery.addresses = originAddresses;
		
		if (i != merged.searchCount) {
			logger.error("loop count: " + i + ", " + merged.searchCount
					+ "，这两个值应该相等！！");
		}

		return merged;
	}

	/**
	 * 对查询结果排序后返回
	 */
	public <T> MergedResult<T> search(LockQuery lockQuery, int minResult,
			Comparator<T> comparator) {
		
		MergedResult<T> merged = search(lockQuery, minResult);
		Collections.sort(merged.list, comparator);
		return merged;
	}

	/**
	 * 支持视频、专辑的去重查询
	 */
	public <T> DuplicateMergedResult<T> searchWithDuplicate(
			LockQuery lockQuery, int minResult, Comparator<T> comparator) {

		MergedResult<T> raw = search(lockQuery, minResult);
		DuplicateResult<T> duplicateResult = null;

		int raw_size = 0;
		int new_size = 0;
		int diff = 0;

		while (true) {
			Collections.sort(raw.list, comparator);
			duplicateResult = DuplicateUtil.remove(raw.list);

			raw_size = raw.list.size();
			new_size = duplicateResult.result.size();
			diff = raw_size - new_size;

			if (diff == 0 || new_size >= minResult || !raw.hasNext) {// 退出循环
				break;
			}

			QueryObjectUtil.nextPage(lockQuery.queryObject());
			MergedResult<T> result = search(lockQuery);
			raw.merge(result);

			if (result.list.isEmpty()) {// 没有更多结果
				break;
			}
		}

		// 对视频查询、相关度排序情况下的特殊处理
		// DuplicateUtil.adjustScoreAndResort(duplicateResult, comparator);

		// 返回结果
		DuplicateMergedResult<T> merged = new DuplicateMergedResult<T>();

		merged.merge(raw);// 只是用来设置部分信息
		merged.list = duplicateResult.result; // 调整去重后的信息
		merged.totalCount = raw.totalCount - diff; // 调整去重后的信息

		merged.md5Map.putAll(duplicateResult.md5Map);
		merged.objectMap.putAll(duplicateResult.objectMap);

		if (logger.isDebugEnabled()) {
			logger.debug("old size: " + raw_size + ", new_size: " + new_size
					+ ", diff: " + diff);
		}

		return merged;
	}

	/**
	 * 支持视频、专辑的去重查询
	 */
	public <T> DuplicateMergedResult<T> searchWithDuplicateForExternalSite(
			LockQuery lockQuery, int minResult, Comparator<T> comparator) {

		MergedResult<T> raw = search(lockQuery, minResult);
		DuplicateResult<T> duplicateResult = null;

		int raw_size = 0;
		int new_size = 0;
		int diff = 0;

		while (true) {
			Collections.sort(raw.list, comparator);
			duplicateResult = DuplicateUtil.remove(raw.list);

			raw_size = raw.list.size();
			new_size = duplicateResult.result.size();
			diff = raw_size - new_size;

			if (diff == 0 || new_size >= minResult || !raw.hasNext) {// 退出循环
				break;
			}

			QueryObjectUtil.nextPage(lockQuery.queryObject());
			MergedResult<T> result = search(lockQuery);
			raw.merge(result);

			if (result.list.isEmpty()) {// 没有更多结果
				break;
			}
		}

		// 返回结果
		DuplicateMergedResult<T> merged = new DuplicateMergedResult<T>();

		merged.merge(raw);// 只是用来设置部分信息
		merged.list = duplicateResult.result; // 调整去重后的信息
		merged.totalCount = raw.totalCount - diff; // 调整去重后的信息

		merged.md5Map.putAll(duplicateResult.md5Map);
		merged.objectMap.putAll(duplicateResult.objectMap);

		if (logger.isDebugEnabled()) {
			logger.debug("old size: " + raw_size + ", new_size: " + new_size
					+ ", diff: " + diff);
		}

		return merged;
	}

	/**
	 * 查询结果去重后的结果
	 */
	public static class DuplicateMergedResult<T> extends MergedResult<T> {
		public Map<String, Integer> md5Map = new HashMap<String, Integer>();
		public Map<T, Integer> objectMap = new HashMap<T, Integer>();
	}
}
