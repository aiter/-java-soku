package com.youku.search.pool.net;

import java.io.Serializable;

public class QueryHolder implements Serializable {

	private static final long serialVersionUID = 1L;

	public long c_start;
	public Object queryObject;

	public QueryHolder(Object queryObject) {
		this.queryObject = queryObject;
	}
}
