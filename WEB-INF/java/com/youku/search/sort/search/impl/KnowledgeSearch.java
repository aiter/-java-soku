package com.youku.search.sort.search.impl;

import org.json.JSONObject;

import com.youku.search.config.Config;
import com.youku.search.index.entity.Query;
import com.youku.search.index.entity.Video;
import com.youku.search.sort.core.IndexSearchResult;
import com.youku.search.sort.core.IndexSearcher;
import com.youku.search.sort.core.SearchContext;
import com.youku.search.sort.json.VideoConverter;
import com.youku.search.sort.search.AbstractSearchTemplate;
import com.youku.search.sort.search.impl.VideoSearch.VideoSearchResult;
import com.youku.search.sort.util.DuplicateUtil;

/**
 * 相关视频搜索
 */
public class KnowledgeSearch extends AbstractSearchTemplate<Video> {

	public static final KnowledgeSearch I = new KnowledgeSearch();

	public KnowledgeSearch() {
		super("video_title_tag", ShieldChannelTarget.VIDEO);
	}

	protected SearchResult<Video> getSearchResult(SearchContext<Video> context) {

		//重构索引发送的query对象
		Query query = (Query)context.lockQuery.queryObject();
		query.categories = "87,90,92,103,105";	//限制分类
		
		// 不隐藏重复的视频
		IndexSearchResult<Video> indexSearchResult = IndexSearcher.search(context);
		if (Config.getDuplicateStatus() > 0) {
			indexSearchResult.list = DuplicateUtil.resort(indexSearchResult.list);
		}
		//对知识分类特殊处理
		indexSearchResult.list = new KnowledgeResort<Video>(indexSearchResult.list,context.frontPage).getResult(); 
		//结束

		VideoSearchResult result = new VideoSearchResult(indexSearchResult);

		return result;

		

	}
	@Override
	protected JSONObject buildJSONObject(SearchResult<Video> result) {

		JSONObject jsonObject = VideoConverter.convert(result);
		return jsonObject;
	}

}
