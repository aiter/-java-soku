package com.youku.search.sort.search.impl;

import org.json.JSONObject;

import com.youku.search.drama.cache.DramaSearcher;
import com.youku.search.pool.net.util.Cost;
import com.youku.search.sort.Parameter;
import com.youku.search.sort.json.util.JSONUtil;
import com.youku.search.sort.log.RemoteLogger;
import com.youku.search.sort.search.AbstractSearch;
import com.youku.search.sort.search.LogInfo;
import com.youku.search.sort.search.LogInfo.Item;
import com.youku.search.util.Constant;

public class DramaSearch extends AbstractSearch {

	public static final DramaSearch I = new DramaSearch();

	public DramaSearch() {
		super("drama");
	}

	public final JSONObject search(Parameter p) {

		JSONObject response = process(p);

		if (p.callback == null) {
			return response;
		}

		String result = postProcess(p.callback, response);
		throw new StringSearchResultException(result);
	}

	private JSONObject process(Parameter p) {

		final Cost cost = new Cost();

		if (p.query == null || p.query.length() == 0) {
			logger.debug("查询关键词为空，返回空结果");
			return AbstractSearch.JSON_STRING.OBJECT_ZERO_RESULT;
		}

		JSONObject json = null;
		if (p.feedback) {
			json = DramaSearcher.getEpisodeVideos(p.query, p.pageSize);
		} else {
			json = DramaSearcher.searchByVideoId(p.query);
		}

		// 查询完毕
		cost.updateEnd();

		// 发送日志
		LogInfo info = buildLogInfo(p, cost.getCost(), json);
		RemoteLogger.log(RemoteLogger.youkuQuery, info);

		return json != null ? json
				: AbstractSearch.JSON_STRING.OBJECT_ZERO_RESULT;
	}

	private LogInfo buildLogInfo(Parameter p, long cost, JSONObject json) {

		LogInfo info = new LogInfo();

		info.set(Item.query, p.query);
		info.set(Item.source, p._source);
		info.set(Item.type, search_name);

		final String logic = (p.logic == Constant.Operator.AND) ? "and" : "or";
		info.set(Item.logic, logic);

		info.set(Item.order_field, p.orderFieldStr);
		info.set(Item.order_reverse, p.reverse);
		info.set(Item.page, p.curPage);
		info.set(Item.cache, true);

		if (json == null || json.length() == 0) {
			info.set(Item.total_result, 0);
			info.set(Item.page_result, 0);
		} else {
			info.set(Item.total_result, 1);
			info.set(Item.page_result, 1);
		}

		info.set(Item.cost, cost);
		info.set(Item.miss, false);
		info.set(Item.cacheKey, null);
		info.set(Item.url, p.queryUrl);

		return info;
	}

	private String postProcess(String callback, JSONObject response) {

		JSONObject object = response == null ? new JSONObject() : response;
		JSONUtil.encodeAscii(object);

		String json = object.toString();

		json = json.replaceAll("\\\\\\\\", "\\\\");

		return callback + "(" + json + ");";
	}

	public static void main(String[] args) {
		String s = "\\\\";
		System.out.println(s);
		System.out.println(s.replaceAll("\\\\\\\\", "\\\\"));
	}

}
