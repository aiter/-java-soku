package com.youku.search.sort.search.impl;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.search.config.Config;
import com.youku.search.index.entity.Query;
import com.youku.search.index.entity.Stat;
import com.youku.search.pool.net.LockQuery;
import com.youku.search.pool.net.Lock.MergedResult;
import com.youku.search.sort.MemCache;
import com.youku.search.sort.Parameter;
import com.youku.search.sort.core.search.MultiIndexSearcher;
import com.youku.search.sort.json.StatConverter;
import com.youku.search.sort.json.util.JSONUtil;
import com.youku.search.sort.json.util.JSONUtil.ParseResult;
import com.youku.search.sort.util.bridge.SearchUtil;
import com.youku.search.util.Constant;

public class RelevantWords {

	static Log logger = LogFactory.getLog(RelevantWords.class);

	private static int searchType = Constant.QueryField.STAT_KEYWORD;

	public static JSONObject getWordsJSONObject(Parameter p) {

		if (Config.getRelevantWordStatus() < 1) {
			logger.debug("当前配置禁用了相关搜索");
			return null;
		}

		if (p.query == null || p.query.length() == 0) {
			logger.debug("查询关键词为空，返回空结果");
			return null;
		}

		if (p.relNum < 1) {
			logger.debug("用户需要的相关词数目小于1，返回空结果。相关词数: " + p.relNum);
			return null;
		}

		String cacheKey = SearchUtil.CacheKey.relavantWords(p);

		String jsonString = "";
		if (!p.delMemchache) {
			logger.debug("需要检查缓存，cacheKey: " + cacheKey);
			jsonString = (String) MemCache.cacheGet(cacheKey);

		} else {
			logger.debug("跳过检查缓存，cacheKey: " + cacheKey);
		}

		ParseResult parseResult = JSONUtil.tryParse(jsonString);
		if (parseResult.valid) {
			logger.debug("缓存有效，cacheKey: " + cacheKey + ", jsonString: "
					+ jsonString);

			return parseResult.object;

		} else {
			logger.debug("缓存无效，cacheKey: " + cacheKey);
		}

		InetSocketAddress[] servers = SearchUtil.getLuceneServers(searchType);

		// create a search object
		Query object = new Query();
		object.start = 0;
		object.end = p.relNum + 1;
		object.sort = Constant.Sort.SORT_SCORE;
		object.reverse = true;
		object.keywords = p.query;
		object.category = p.type;// 注意：此字段在这里表示前端的查询类型
		object.partner = 0;
		object.operator = Constant.Operator.AND;
		object.field = searchType;

		// hit lucene...
		LockQuery lockQuery = new LockQuery(servers, object);
		MergedResult<Stat> result = MultiIndexSearcher.I.search(lockQuery);

		if (result.list.isEmpty()) {
			logger.debug("索引服务器返回空结果，cacheKey: " + cacheKey);
			return null;
		}

		List<Stat> filteredList = filter(result.list, p.relNum, p.query);

		if (logger.isDebugEnabled()) {
			logger.debug("索引服务器返回 " + result.list.size() + " 结果，用户需要 "
					+ p.relNum + " 个结果，过滤后的实际结果数为 " + filteredList.size()
					+ ", cacheKey: " + cacheKey);
		}

		if (filteredList.isEmpty()) {
			logger.debug("过滤后的结果数为0，返回空结果, cacheKey: " + cacheKey);
			return null;
		}

		JSONObject jsonObject = buildJSONObject(filteredList);

		if (jsonObject == null) {
			logger.debug("把查询结果转换为JSONObject时发生错误，返回空结果， cacheKey：" + cacheKey);
			return null;
		}

		if (result.miss) {
			logger.debug("部分lucene server没有返回结果，不缓存查询结果， cacheKey：" + cacheKey);

		} else {
			jsonString = jsonObject.toString();

			logger.debug("缓存查询结果， cacheKey：" + cacheKey + ", jsonString: "
					+ jsonString);

			MemCache.cacheSet(cacheKey, jsonString, 43200);
		}

		return jsonObject;
	}

	private static JSONObject buildJSONObject(List<Stat> filteredList) {

		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("resultnum", filteredList.size());

			JSONObject itemsObject = new JSONObject();
			for (int i = 0; i < filteredList.size(); i++) {
				Stat stat = filteredList.get(i);
				itemsObject.put(i + "", StatConverter.convert(stat));
			}
			jsonObject.put("items", itemsObject);

			return jsonObject;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	private static List<Stat> filter(List<Stat> resultList, int targetSize,
			String query) {

		List<Stat> list = new ArrayList<Stat>();

		for (Stat stat : resultList) {
			if (list.size() >= targetSize) {
				break;
			}

			if (!query.equalsIgnoreCase(stat.keyword)) {
				list.add(stat);
			}
		}

		return list;
	}

}
