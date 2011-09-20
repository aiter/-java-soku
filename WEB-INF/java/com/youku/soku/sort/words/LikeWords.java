package com.youku.soku.sort.words;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.search.index.entity.Query;
import com.youku.search.index.entity.Stat;
import com.youku.search.pool.net.LockQuery;
import com.youku.search.pool.net.Lock.MergedResult;
import com.youku.search.sort.MemCache;
import com.youku.search.sort.core.search.MultiIndexSearcher;
import com.youku.search.sort.json.util.JSONUtil;
import com.youku.search.sort.json.util.JSONUtil.ParseResult;
import com.youku.search.util.Constant;
import com.youku.soku.config.Config;
import com.youku.soku.sort.Parameter;

public class LikeWords extends AbstractWords<JSONObject> {

	public LikeWords() {
		searchType = Constant.QueryField.STAT_KEYWORD;
	}

	@Override
	public JSONObject getWord(Parameter p) {
		try {
			return getWord_(p);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	public JSONObject getWord_(Parameter p) throws Exception {

		if (Config.getRelevantWordStatus() < 1) {
			logger.debug("当前配置禁用了相关搜索");
			return null;
		}

		if (p.keyword == null || p.keyword.length() == 0) {
			logger.debug("查询关键词为空，返回空结果");
			return null;
		}

		if (p.relnum < 1) {
			if (logger.isDebugEnabled()) {
				logger.debug("用户需要的相关词数目小于1，返回空结果。相关词数: " + p.relnum);
			}
			return null;
		}

		String cacheKey = getCacheKey(p.keyword) + "_" + p.relnum;

		String jsonString = "";
		if (logger.isDebugEnabled()) {
			logger.debug("检查缓存，cacheKey: " + cacheKey);
		}
		jsonString = (String) MemCache.cacheGet(cacheKey);

		ParseResult parseResult = JSONUtil.tryParse(jsonString);
		if (parseResult.valid) {
			if (logger.isDebugEnabled()) {
				logger.debug("缓存有效，cacheKey: " + cacheKey + ", jsonString: "
						+ jsonString);
			}

			return parseResult.object;

		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("缓存无效，cacheKey: " + cacheKey);
			}
		}

		// create a search object
		Query object = new Query();
		object.start = 0;
		object.end = p.relnum + 1;
		object.reverse = true;
		object.keywords = p.keyword;
		object.operator = Constant.Operator.AND;
		object.field = searchType;

		// hit lucene...
		LockQuery lockQuery = new LockQuery(getServers(), object);

		if (logger.isDebugEnabled()) {
			logger.debug("lockQuery: " + lockQuery);
		}

		MergedResult<Stat> result = MultiIndexSearcher.I.search(lockQuery);

		if (result.list.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("索引服务器返回空结果，cacheKey: " + cacheKey);
			}
			return null;
		}

		List<Stat> wordList = filter(result.list, p.relnum, p.keyword);

		if (logger.isDebugEnabled()) {
			logger.debug("索引服务器返回 " + result.list.size() + " 结果，用户需要 "
					+ p.relnum + " 个结果，过滤后的实际结果数为 " + wordList.size()
					+ ", cacheKey: " + cacheKey);
		}

		if (wordList.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("过滤后的结果数为0，返回空结果, cacheKey: " + cacheKey);
			}
			return null;
		}

		JSONObject jsonObject = buildJSONObject(wordList);

		if (result.miss) {
			if (logger.isDebugEnabled()) {
				logger.debug("部分lucene server没有返回结果，不缓存查询结果， cacheKey："
						+ cacheKey);
			}

		} else {
			jsonString = jsonObject.toString();

			if (logger.isDebugEnabled()) {
				logger.debug("缓存查询结果， cacheKey：" + cacheKey + ", jsonString: "
						+ jsonString);
			}

			MemCache.cacheSet(cacheKey, jsonString, cache_seconds);
		}

		return jsonObject;
	}

	private JSONObject buildJSONObject(List<Stat> wordList) throws Exception {

		JSONObject jsonObject = new JSONObject();

		JSONArray itemsArray = new JSONArray();
		for (int i = 0; i < wordList.size(); i++) {
			Stat word = wordList.get(i);
			itemsArray.put(word.keyword);
		}

		jsonObject.put("count", itemsArray.length());
		jsonObject.put("items", itemsArray);

		return jsonObject;
	}

	private static List<Stat> filter(List<Stat> sourceList, int targetSize,
			String query) {

		List<Stat> list = new ArrayList<Stat>();

		for (Stat word : sourceList) {
			if (list.size() >= targetSize) {
				break;
			}

			if (!query.equalsIgnoreCase(word.keyword)) {
				list.add(word);
			}
		}

		return list;
	}

}
