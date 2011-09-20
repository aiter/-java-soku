package com.youku.search.pool.net;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.index.entity.Result;
import com.youku.search.pool.net.ResultHolderConstant.DummyResult;

public class Lock<T> {

	public static final String KEY = Lock.class.getName() + "_" + "Lock";

	Log logger = LogFactory.getLog(getClass());

	public final long start;// 查询开始时间
	public long send;// 请求发送开始时间
	public final long life;// 查询有效时长
	public final long deadline;// 查询有效时间点 start + life
	public long sent;// 请求发送结束时间
	public long end;// 查询结束时间

	public long[] conn_starts;// 开始取得连接
	public long[] conn_ends;// 取得连接完成

	public final Object queryObject;// 向远程服务器发送的请求对像
	public final SocketAddress[] addresses;// 远程服务器的地址

	public final Object sync;// 同步锁

	public final Map<SocketAddress, ResultHolder<T>> map = new ConcurrentHashMap<SocketAddress, ResultHolder<T>>();// 查询结果，数目可能小于addresses.length
	private List<ResultHolder<T>> resultHoldertList;// 缓存查询结果，数目等于addresses.length

	private boolean setMiss = false;// 是否更新了miss值
	private boolean miss = false;// 是否miss

	public Lock(Object queryObject, InetSocketAddress[] addresses, long life) {
		this.start = System.currentTimeMillis();
		this.life = life;
		this.deadline = this.start + this.life;
		this.queryObject = queryObject;
		this.addresses = addresses;
		this.sync = new Object();

		conn_starts = new long[addresses.length];
		conn_ends = new long[addresses.length];
	}

	/**
	 * 是否添加成功
	 */
	public boolean addResult(SocketAddress address, DummyResult<T> result) {
		ResultHolder<T> holder = new ResultHolder<T>();
		holder.c_start = this.start;
		holder.c_received = System.currentTimeMillis();
		holder.result = result;

		return addResult(address, holder);
	}

	/**
	 * 是否添加成功
	 */
	public boolean addResult(SocketAddress address, ResultHolder<T> holder) {

		if (holder.cost() < life) {
			map.put(address, holder);

			if (map.size() >= addresses.length) {
				synchronized (sync) {
					sync.notifyAll();
				}
			}

			return true;
		}

		return false;
	}

	/**
	 * 返回查询结果列表，每个远程服务器返回一个结果；查询结果的按addresses的顺序排序。
	 * 
	 * 连接远程服务器失败、远程服务器返回查询结果为null、以及其他异常等可以导致一个MissResult对象的出现。
	 * 
	 * 返回的list的个数等于远程服务器的个数，并且按远程服务器的顺序排序。
	 * 
	 * @return 返回查询结果列表，永远不为null。
	 */
	public List<ResultHolder<T>> getResultHolderList() {

		if (resultHoldertList != null) {
			return resultHoldertList;
		}

		resultHoldertList = new ArrayList<ResultHolder<T>>(addresses.length);
		for (SocketAddress address : addresses) {
			ResultHolder<T> holder = map.get(address);
			if (holder == null) {
				holder = new ResultHolder<T>();
				holder.cost = end - send;
				holder.result = ResultHolderConstant.Miss.I;
			}

			resultHoldertList.add(holder);

			boolean isDebug = logger.isDebugEnabled();
			if (isDebug) {
				Result<?> result = holder.result;

				StringBuilder builder = new StringBuilder();
				builder.append("queryObject: " + queryObject + "; ");
				builder.append("server: " + address + "; ");
				builder.append("totalCount: " + result.totalCount + "; ");

				if (result.results == null) {
					builder.append("results: null object" + "; ");
				} else {
					builder.append("results: " + result.results.size() + "; ");
				}

				builder.append("hasNext: " + result.hasNext + "; ");
				builder.append("timecost: " + result.timecost + "; ");
				builder.append("extra: " + result.extra + "; ");

				logger.debug(builder.toString());
			}
		}

		return resultHoldertList;
	}

	public NotMergedResult<T> getResultNotMerged() {

		NotMergedResult<T> notMergedResult = new NotMergedResult<T>();

		List<Long> indexCost = new LinkedList<Long>();// 索引端查询时间
		List<Long> minaCost = new LinkedList<Long>();// 客户端具体查询时间
		List<Long> connCost = new LinkedList<Long>();// 客户端建立链接的时间
		List<Long> clientCost = new LinkedList<Long>();// 客户端总体查询的时间

		List<ResultHolder<T>> resultList = getResultHolderList();

		for (int i = 0; i < resultList.size(); i++) {
			ResultHolder<T> resultHolder = resultList.get(i);
			Result<T> result = resultHolder.result;

			notMergedResult.map.put(addresses[i], result);

			indexCost.add((long) result.timecost);
			minaCost.add(resultHolder.cost());

			notMergedResult.extra.add(result.extra);
		}

		//
		for (int i = 0; i < conn_starts.length; i++) {
			connCost.add(conn_ends[i] - conn_starts[i]);
		}

		//      
		clientCost.add(send - start);
		clientCost.add(sent - send);
		clientCost.add(end - sent);
		clientCost.add(end - start);

		//
		notMergedResult.cost.add(indexCost);
		notMergedResult.cost.add(minaCost);
		notMergedResult.cost.add(clientCost);
		notMergedResult.cost.add(connCost);

		notMergedResult.miss = miss();

		return notMergedResult;
	}

	/**
	 * 是否丢失结果？比如某个remote server挂了。这个方法应该在查询完成后调用。
	 */
	public boolean miss() {

		if (setMiss) {
			return miss;
		}

		List<ResultHolder<T>> resultHoldertList = getResultHolderList();
		for (ResultHolder<T> holder : resultHoldertList) {
			if (ResultHolderConstant.isPrivateResult(holder.result)) {
				miss = true;
				setMiss = true;
				return miss;
			}
		}

		miss = false;
		setMiss = true;
		return miss;
	}

	/**
	 * 对查询结果进行合并，
	 */
	public MergedResult<T> getResultMerged() {

		MergedResult<T> mergedResult = new MergedResult<T>();

		List<Long> indexCost = new LinkedList<Long>();// 索引端查询时间
		List<Long> minaCost = new LinkedList<Long>();// 客户端具体查询时间
		List<Long> connCost = new LinkedList<Long>();// 客户端建立链接的时间
		List<Long> clientCost = new LinkedList<Long>();// 客户端总体查询的时间

		//
		List<ResultHolder<T>> resultList = getResultHolderList();
		List<Serializable> extraList = new LinkedList<Serializable>();
		for (int i = 0; i < resultList.size(); i++) {
			ResultHolder<T> resultHolder = resultList.get(i);
			Result<T> result = resultHolder.result;
			
			// 只要有一台机器有next则总的hasNext就会等于true
			mergedResult.hasNext |= result.hasNext;
			
			indexCost.add((long) result.timecost);
			
			minaCost.add(resultHolder.cost());
			
			if (ResultHolderConstant.isPrivateResult(result)) {
				logger.error("搜索结果Result miss, resultClass=" + result.getClass().getSimpleName() + ", Miss Server=" + addresses[i].toString());
			}
			
			if (result.results != null) {
				mergedResult.list.addAll(result.results);
			}

			mergedResult.totalCount += result.totalCount;

			extraList.add(result.extra);
			
			mergedResult.mergedStatCount(result);
		}
		
		//
		for (int i = 0; i < conn_starts.length; i++) {
			connCost.add(conn_ends[i] - conn_starts[i]);
		}

		//      
		clientCost.add(send - start);
		clientCost.add(sent - send);
		clientCost.add(end - sent);
		clientCost.add(end - start);

		//
		mergedResult.cost.add(indexCost);
		mergedResult.cost.add(minaCost);
		mergedResult.cost.add(clientCost);
		mergedResult.cost.add(connCost);
		mergedResult.searchCount = 1;
		mergedResult.miss = miss();

		mergedResult.extra.add(extraList);

		return mergedResult;
	}
	
	/**
	 * 根据Lock.map，返回所有成功请求的SocketAddress[]
	 * 
	 * @return
	 */
	public InetSocketAddress[] getSuccessSockets() {
		List<InetSocketAddress> resultList = new ArrayList<InetSocketAddress>();
		
		SocketAddress address = null;
		ResultHolder<T> resultHolder = null;
		Result<T> r = null;
		for (Map.Entry<SocketAddress, ResultHolder<T>> entry : this.map.entrySet()) {
			address = entry.getKey();
			resultHolder = entry.getValue();
			
			if (null == address || null == resultHolder) {
				continue;
			}
			
			r = resultHolder.result;
			if (null == r || ResultHolderConstant.isPrivateResult(r)) {
				continue;
			}
			
			resultList.add((InetSocketAddress)address);
		}
		
		return resultList.toArray(new InetSocketAddress[resultList.size()]);
	}
	
	/**
	 * 过滤Lock.map，返回所有resultHolder.result.hasNext==true的SocketAddress[]
	 * 
	 * @return
	 */
	public InetSocketAddress[] getHasNextSockets() {
		List<InetSocketAddress> resultList = new ArrayList<InetSocketAddress>();
		
		SocketAddress address = null;
		ResultHolder<T> resultHolder = null;
		Result<T> r = null;
		for (Map.Entry<SocketAddress, ResultHolder<T>> entry : this.map.entrySet()) {
			address = entry.getKey();
			resultHolder = entry.getValue();
			
			if (null == address || null == resultHolder) {
				continue;
			}
			
			r = resultHolder.result;
			if (null == r || ResultHolderConstant.isPrivateResult(r)) {
				continue;
			}
			
			if (!r.hasNext) {
				continue;
			}
			
			resultList.add((InetSocketAddress)address);
		}
		
		return resultList.toArray(new InetSocketAddress[resultList.size()]);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Lock:");
		buffer.append(" addresses = " + Arrays.toString(addresses));
		return buffer.toString();
	}

	public static class MergedResult<T> {
		public int totalCount;
		public List<T> list = new LinkedList<T>();
		public boolean miss;
		public int searchCount;
		public List<List<Long>> cost = new LinkedList<List<Long>>();
		public boolean hasNext;
		
		public List<List<Serializable>> extra = new LinkedList<List<Serializable>>();
		
		private int[] statCount;
		
		/**
		 * 由于现在搜索已经不去重了，所以这里可以计算totalCount <br>
		 * modified by gaosong 2011-06-03 <br>
		 * 
		 */
		public void merge(MergedResult<T> addon) {
			totalCount = totalCount + addon.totalCount;
			list.addAll(addon.list);
			miss = miss || addon.miss;
			searchCount += addon.searchCount;
			cost.addAll(addon.cost);
			hasNext = addon.hasNext;
			extra.addAll(addon.extra);
			mergedStatCount(addon);
		}
		
		public void mergedStatCount(Result addOnResult){
			if (addOnResult.statCount != null) {
				if (null == this.statCount) {
					 this.statCount = addOnResult.statCount;
				} else {
					for (int i = 0; i < statCount.length; i++) {
						this.statCount[i]+=addOnResult.statCount[i];
					}
				}
			}
		}
		
		private void mergedStatCount(MergedResult<T> addon) {
			if (addon.statCount != null) {
				if (null == this.statCount) {
					 this.statCount = addon.statCount;
				} else {
					for (int i = 0; i < statCount.length; i++) {
						this.statCount[i]+=addon.statCount[i];
					}
				}
			}
		}

		public String costString() {
			return Arrays.toString(cost.toArray());
		}

		public int[] getStatCount() {
			return statCount;
		}
	}

	public static class NotMergedResult<T> {
		public Map<SocketAddress, Result<T>> map = new HashMap<SocketAddress, Result<T>>();
		public boolean miss;
		public List<List<Long>> cost = new LinkedList<List<Long>>();
		public List<Serializable> extra = new LinkedList<Serializable>();

		public String costString() {
			return Arrays.toString(cost.toArray());
		}
	}
}
