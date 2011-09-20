package com.youku.search.pool.net;

import java.io.Serializable;

import com.youku.search.index.entity.Result;

public class ResultHolder<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	public Result<T> result;// 来自lucene的查询结果
	public long cost = Long.MIN_VALUE;

	public long c_start;
	public long s_received = -1;
	public long s_send = -2;
	public long c_received = -1;

	public long cost() {
		if (cost == Long.MIN_VALUE) {
			cost = c_received - c_start;
		}
		return cost;
	}
}