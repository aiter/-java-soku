package com.youku.search.pool.net;

import java.net.InetSocketAddress;
import java.util.Arrays;

import com.youku.search.sort.util.bridge.BridgeMap;

public class LockQuery {

	public InetSocketAddress[] addresses;
	public QueryHolder queryHolder;

	/**
	 * web前端的查询类型，用来为MINA底层区分使用哪个Encoder <br>
	 * 
	 * @see BridgeMap.SearchType
	 * 
	 *      2011-05-31 gaosong
	 */
	public int type = -1;
	
//	public ServerWrapper serverWrapper;
	
//	public LockQuery(ServerWrapper serverWrapper, Object queryObject) {
//		this(serverWrapper.getAddresses(), queryObject);
//		this.serverWrapper = serverWrapper;
//	}
	
	public LockQuery(InetSocketAddress address, Object queryObject) {
		this(new InetSocketAddress[] { address }, queryObject);
	}
	
	public LockQuery(InetSocketAddress[] addresses, Object queryObject, int type) {
		this(addresses, queryObject);
		this.type = type;
	}

	public LockQuery(InetSocketAddress[] addresses, Object queryObject) {
		this.addresses = addresses;
		this.queryHolder = new QueryHolder(queryObject);
	}

	public Object queryObject() {
		return queryHolder.queryObject;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("addresses: " + Arrays.toString(addresses) + "; ");

		if (queryHolder == null) {
			builder.append("queryObject: " + null);
		} else {
			builder.append("queryObject: " + queryHolder.queryObject);
		}

		return builder.toString();
	}

}
