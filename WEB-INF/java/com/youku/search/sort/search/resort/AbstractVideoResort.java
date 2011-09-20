package com.youku.search.sort.search.resort;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.pool.net.Lock.MergedResult;
import com.youku.search.sort.core.SearchContext;

public abstract class AbstractVideoResort<T, R> {
	
	protected static final Log logger = LogFactory.getLog(AbstractVideoResort.class);
	
	public abstract List<R> resort(SearchContext<T> context, MergedResult<R> searchResults);
	
}
