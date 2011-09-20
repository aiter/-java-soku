package com.youku.search.sort.search.impl;

import java.net.InetSocketAddress;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;

import com.youku.search.config.Config;
import com.youku.search.index.entity.Query;
import com.youku.search.index.entity.Stat;
import com.youku.search.pool.net.LockQuery;
import com.youku.search.pool.net.Lock.MergedResult;
import com.youku.search.sort.MemCache;
import com.youku.search.sort.Parameter;
import com.youku.search.sort.core.search.MultiIndexSearcher;
import com.youku.search.sort.util.bridge.SearchUtil;
import com.youku.search.util.Constant;

public class Suggestion {

	static Log logger = LogFactory.getLog(Suggestion.class);

	private static int searchType = Constant.QueryField.STAT_PINYIN;

	public static String getSuggestionWord(Parameter p) {

		try {
			JSONArray jsonArray = getSimilarJSONArray(p);
			if (jsonArray != null && jsonArray.length() > 0) {
				return (String) jsonArray.get(0);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	public static JSONArray getSimilarJSONArray(Parameter p) {

		if (Config.getSuggestionStatus() < 1) {
			logger.debug("当前配置禁用了拼音纠错");
			return null;
		}

		if (p.query == null || p.query.length() == 0) {
			logger.debug("查询关键词为空，返回空结果");
			return null;
		}

		String cacheKey = SearchUtil.CacheKey.suggestionWords(p);

		if (!p.delMemchache) {
			logger.debug("需要检查缓存，cacheKey: " + cacheKey);

			String jsonString = (String) MemCache.cacheGet(cacheKey);

			if (jsonString != null && jsonString.length() > 0) {
				try {
					JSONArray cachedJSONArray = new JSONArray(jsonString);

					logger.debug("缓存有效，cacheKey: " + cacheKey
							+ ", jsonString: " + jsonString);

					return cachedJSONArray;

				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}

			logger.debug("缓存无效，cacheKey: " + cacheKey);

		} else {
			logger.debug("跳过检查缓存，cacheKey: " + cacheKey);
		}

		//
		InetSocketAddress[] servers = SearchUtil.getLuceneServers(searchType);

		Query object = new Query();
		object.start = 0;
		object.end = 1;
		object.sort = Constant.Sort.SORT_SCORE;
		object.reverse = true;
		object.keywords = p.query;
		object.category = p.type;// 注意：此字段在这里表示前端的查询类型
		object.partner = 0;
		object.operator = Constant.Operator.AND;
		object.field = searchType;

		LockQuery lockQuery = new LockQuery(servers, object);
		MergedResult<Stat> result = MultiIndexSearcher.I.search(lockQuery);

		if (result.list.isEmpty()) {
			logger.debug("索引服务器返回空结果，cacheKey: " + cacheKey);

			return null;
		}

		// lucene后台保证查询结果不会包含查询关键词query
		List<Stat> filteredList = result.list;

		JSONArray jsonArray = buildJSONArray(filteredList);
		if (result.miss) {
			logger.debug("部分lucene server没有返回结果，不缓存查询结果， cacheKey：" + cacheKey);

		} else {
			String jsonString = jsonArray.toString();

			logger.debug("缓存查询结果， cacheKey：" + cacheKey + ", jsonString: "
					+ jsonString);

			MemCache.cacheSet(cacheKey, jsonString, 6 * 3600);
		}

		return jsonArray;
	}

	private static JSONArray buildJSONArray(List<Stat> filteredList) {

		JSONArray jsonArray = new JSONArray();

		for (Stat stat : filteredList) {
			jsonArray.put(stat.keyword);
		}

		return jsonArray;
	}

}
