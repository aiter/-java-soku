package com.youku.search.sort.search.impl;

import java.util.ArrayList;
import java.util.List;

import com.youku.search.index.entity.DefaultResponse;
import com.youku.search.index.entity.Video;
import com.youku.search.pool.net.Lock.MergedResult;
import com.youku.search.sort.core.IndexSearchResult;
import com.youku.search.sort.core.SearchContext;
import com.youku.search.sort.core.search.MultiIndexSearcher;
import com.youku.search.sort.search.AbstractSearchTemplate;
import com.youku.search.sort.search.resort.AbstractVideoResort;
import com.youku.search.sort.search.resort.ResortFactory;
import com.youku.search.sort.util.StoreVideoUtil;

public abstract class AbstractVideoSearch extends AbstractSearchTemplate<Video> {

	static final MultiIndexSearcher searcher = MultiIndexSearcher.I;
	
	public AbstractVideoSearch(String search_name) {
		super(search_name);
	}
	
	public AbstractVideoSearch(String search_name, ShieldChannelTarget target) {
		super(search_name, target);
	}

	/**
	 * 进行一次集群检索请求，如有必要，则进行服务器访问控制
	 * 
	 * @param context
	 * @return
	 */
	protected MergedResult<DefaultResponse> search(SearchContext<Video> context) {
		MergedResult<DefaultResponse> mergedResult = null;
		
		if (context.serverWrapper.isDynamic()) {
			context.serverWrapper.getAccessControl().startRequest();
			try {
				mergedResult = searcher.search(context.lockQuery,
						context.minLuceneResultCount);
			} finally {
				context.serverWrapper.getAccessControl().endRequest((null == mergedResult) ? true
						: mergedResult.miss);
			}
		} else {
			mergedResult = searcher.search(context.lockQuery,
					context.minLuceneResultCount);
		}
		
		return mergedResult;
	}
	
	/**
	 * 进行集群检索、洗脸（如有必要）、填充高亮
	 * 
	 * @param context
	 * @return
	 */
	public IndexSearchResult<Video> getIndexSearchResult(SearchContext<Video> context) {
		List<Long> getIndexSearchCost = new ArrayList<Long>(3);
		
		// 集群检索
		long multiSearchStart = System.currentTimeMillis();
		MergedResult<DefaultResponse> mergedResult = search(context);
		getIndexSearchCost.add(System.currentTimeMillis() - multiSearchStart);
		
		// 洗脸
		long resortStart = System.currentTimeMillis();
		AbstractVideoResort<Video, DefaultResponse> videoResort = ResortFactory.getResort(context);
		List<DefaultResponse> resortedList = videoResort.resort(context, mergedResult);
		getIndexSearchCost.add(System.currentTimeMillis() - resortStart);
		
		//取得视频信息
		long getStoreVideoStart = System.currentTimeMillis();
		List<Video> storeVideoList = StoreVideoUtil.getStoreVideoList(context, resortedList);
		getIndexSearchCost.add(System.currentTimeMillis() - getStoreVideoStart);
		
		IndexSearchResult<Video> indexSearchResult = new IndexSearchResult<Video>(
				storeVideoList, mergedResult, context.span);
		
		// 填充高亮
		StoreVideoUtil.fillHighLightField(context, storeVideoList, indexSearchResult);
		
		indexSearchResult.cost.add(getIndexSearchCost);
		return indexSearchResult;
	}
	
}
