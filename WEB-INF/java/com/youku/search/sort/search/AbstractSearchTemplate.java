package com.youku.search.sort.search;

import java.util.List;

import org.json.JSONObject;

import com.youku.search.config.Config;
import com.youku.search.pool.net.util.Cost;
import com.youku.search.sort.MemCache;
import com.youku.search.sort.MemCache.StoreResult;
import com.youku.search.sort.Parameter;
import com.youku.search.sort.core.IndexSearchResult;
import com.youku.search.sort.core.SearchContext;
import com.youku.search.sort.json.util.JSONUtil;
import com.youku.search.sort.json.util.JSONUtil.ParseResult;
import com.youku.search.sort.log.RemoteLogger;
import com.youku.search.sort.search.LogInfo.Item;
import com.youku.search.sort.util.bridge.SearchUtil;
import com.youku.search.util.Constant;

public abstract class AbstractSearchTemplate<T> extends AbstractSearch {

	public static class SearchResult<E> {
		public boolean miss;// 远程服务器是否丢失结果？
		public boolean zero_result;// 是否是空结果？
		public int total;// 查询结果总数
		public int loop;// 像一组lucene服务器的查询次数
		public String costString;// 具体每一台lucene服务器上的查询时间
		public List<E> page;// 查询结果（一页数据）

		public SearchResult() {
		}

		public SearchResult(SearchResult<E> searchResult) {
			miss = searchResult.miss;
			zero_result = searchResult.zero_result;
			total = searchResult.total;
			loop = searchResult.loop;
			costString = searchResult.costString;
			page = searchResult.page;
		}

		public SearchResult(IndexSearchResult<E> indexSearchResult) {

			final List<E> pageList = indexSearchResult.page();

			miss = indexSearchResult.miss;
			zero_result = pageList.isEmpty();
			total = indexSearchResult.totalCount;
			loop = indexSearchResult.searchCount;
			costString = indexSearchResult.costString();
			page = pageList;
		}
	}

	public static class SearchResultWrapper<E> {
		public SearchResult<E> searchResult;
		public JSONObject jsonObject;
	}

	public static class CacheResult extends ParseResult {
		public boolean changed;// 从input得到的object是否又被修改过

		public void update(ParseResult parseResult) {
			this.input = parseResult.input;
			this.valid = parseResult.valid;
			this.object = parseResult.object;
		}
	}

	public AbstractSearchTemplate(String search_name) {
		super(search_name);
	}

	public AbstractSearchTemplate(String search_name, ShieldChannelTarget target) {
		super(search_name, target);
	}

	@Override
	public final JSONObject search(Parameter p) throws Exception {

		final Cost total_cost = new Cost();

		// 空关键词 无效的关键词
		//2011.3.4 将这个判断移到各个具体的外层接口中。/search?这个接口允许空key
		/*if (p.query == null || p.query.isEmpty()
				|| !WordProcessor.checkKeywordIsValid(p.query)) {
			return AbstractSearch.JSON_STRING.OBJECT_ZERO_RESULT;
		}*/

		// 被过滤的关键词
		if (needCheckBadWords) {
			List<Integer> shieldChannels = getShieldChannels(p.query);
			if (shieldChannels == Shield_All) {
				logger.debug("查询关键词被过滤, 返回空结果, query: " + p.query);
				return AbstractSearch.JSON_STRING.OBJECT_BAD_WORDS;
			} else if (shieldChannels == Shield_None) {
				logger.debug("不需要屏蔽该关键词: query: " + p.query);
			} else {
				p.appendExclude_cates(shieldChannels);
				logger.debug("关键词被过滤部分频道, query: " + p.query
						+ ", 需要屏蔽的channels: " + shieldChannels
						+ ", 最终的exclude_cates: " + p.exclude_cates);
			}
		}
		
		
		// C-Server固定一台一次最多返回48条结果
		// modified by gaosong 2011-06-09
		if (SearchUtil.getIsCServer(p.type)){
			indexPageSize = Constant.Socket.INDEX_PAGE_SIZE;
		}
		SearchContext<T> sc = new SearchContext<T>(p, indexPageSize);
		
		final Cost search_cost = new Cost();// 表示从lucene或者缓存取得查询结果的时间

		// 首先检查缓存
		CacheResult cacheResult = getJSONObjectFromCache(sc);
		search_cost.updateEnd();

		final JSONObject resultObject;
		SearchResultWrapper<T> wrapper = new SearchResultWrapper<T>();

		if (cacheResult.valid) { // 缓存有效
			resultObject = cacheResult.object;

		} else {// 缓存无效，需要从lucene中查询结果

			search_cost.updateStart();
			wrapper = getJSONObject(sc);	
			search_cost.updateEnd();

			resultObject = wrapper.jsonObject;

			// 查询结果的后处理1
			try {
				resultObject.put("cost", search_cost.getCost());
				resultObject.put("miss", wrapper.searchResult.miss);
				resultObject.put("page", p.pageSize);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}

			// 查询结果的后处理2
			postProcessSearchResult(resultObject);
		}

		// 是否需要缓存查询结果
		cacheSearchResultIfPossible(cacheResult.valid, sc,
				wrapper.searchResult, resultObject);

		// 查询完毕
		total_cost.updateEnd();

		// 发送日志
		LogInfo info = buildLogInfo(sc, total_cost.getCost(), search_cost
				.getCost(), cacheResult.valid, resultObject,
				wrapper.searchResult);
		RemoteLogger.log(RemoteLogger.youkuQuery, info);
		
		StringBuilder sb = new StringBuilder();
		sb.append("--- LOGINFO --- ").append("|");
		sb.append("请求Query=" + info.get(LogInfo.Item.query)).append("|");
		sb.append("是否miss=" + info.get(LogInfo.Item.miss)).append("|");
		sb.append("总搜索结果=" + info.get(LogInfo.Item.total_result)).append("|");
		sb.append("总用时=" + info.get(LogInfo.Item.cost)).append("|");
		sb.append("others=" + info.get(LogInfo.Item.others)).append("|");
		statInfoLogger.info(sb.toString());
		
//		if (QueryStat.I.isStat.get()) {
//			QueryStat.I.addLogInfo(info);
//			
//			// 按时间排序的话，记录时间差值
//			if (p.orderFieldStr.equalsIgnoreCase("createtime")) {
//				QueryStat.I.addJSONObject(resultObject);
//			}
//		}
		
		// OK
		return resultObject;
	}

	private LogInfo buildLogInfo(SearchContext<T> sc, long total_cost,
			long search_cost, boolean cacheValid, JSONObject responseObject,
			SearchResult<T> searchResult) {

		Parameter p = sc.p;

		LogInfo info = new LogInfo();

		info.set(Item.query, p.query);
		info.set(Item.source, p._source);
		info.set(Item.type, search_name);

		final String logic = (p.logic == Constant.Operator.AND) ? "and" : "or";
		info.set(Item.logic, logic);

		info.set(Item.order_field, p.orderFieldStr);
		info.set(Item.order_reverse, p.reverse);
		info.set(Item.page, p.curPage);

		info.set(Item.cache, cacheValid);

		if (cacheValid) {
			JSONObject items = responseObject.optJSONObject("items");
			if (items == null) {
				items = new JSONObject();
			}

			info.set(Item.total_result, responseObject.optInt("total"));
			info.set(Item.page_result, items.length());
			info.set(Item.cost, total_cost);
			info.set(Item.miss, false);
			info.set(Item.cacheKey, sc.cacheKey);
			info.set(Item.url, p.queryUrl);
			info.set(Item.because, null);
		} else {

			info.set(Item.total_result, searchResult.total);
			info.set(Item.page_result, searchResult.page.size());
			info.set(Item.cost, total_cost);
			info.set(Item.miss, searchResult.miss);
			info.set(Item.cacheKey, sc.cacheKey);
			info.set(Item.url, p.queryUrl);
			info.set(Item.because, null);

			final StringBuilder others = new StringBuilder();
			others.append("searchtime:" + search_cost);
			others.append("; ");
			others.append("loop:" + searchResult.loop);
			others.append("; ");
			others.append("index:" + searchResult.costString);
			info.set(Item.others, others.toString());
		}

		return info;
	}

	private void cacheSearchResultIfPossible(boolean cacheValid,
			SearchContext<T> sc, SearchResult<T> searchResult,
			JSONObject jsonObject) {

		boolean debug = logger.isDebugEnabled();

		if (cacheValid) {
			if (debug) {
				logger.debug("查询结果来自缓存，不需要缓存查询结果，cacheKey：" + sc.cacheKey);
			}
			return;
		}

		if (searchResult.zero_result) {
			if (debug) {
				logger.debug("查询结果为空，不缓存查询结果，cacheKey：" + sc.cacheKey);
			}
			return;
		}

		if (searchResult.miss) {
			if (debug) {
				logger.debug("部分lucene server没有返回结果，不缓存查询结果，cacheKey："
						+ sc.cacheKey);
			}
			return;
		}

		String jsonString = jsonObject.toString();
		StoreResult storeResult = MemCache.cacheSet(sc.cacheKey, jsonString,
				Config.getCacheTimeOut());

		if (debug) {
			logger.debug("可以缓存查询结果，存储到memcache，cacheKey：" + sc.cacheKey
					+ "; storeResult: " + storeResult);
		}
	}

	private CacheResult getJSONObjectFromCache(SearchContext<T> sc) {

		CacheResult cacheResult = new CacheResult();

		if (sc.p.delMemchache) {
			logger.debug("跳过检查缓存，cacheKey：" + sc.cacheKey);
			return cacheResult;
		}

		logger.debug("需要检查缓存，cacheKey: " + sc.cacheKey);
		final String cachedString = (String) MemCache.cacheGet(sc.cacheKey,
				Config.getCacheTimeOut());
		cacheResult.update(JSONUtil.tryParse(cachedString));

		if (cacheResult.valid) {
			logger.debug("缓存有效，开始处理缓存内容，cacheKey：" + sc.cacheKey);

			cacheResult.changed = processCached(sc, cachedString,
					cacheResult.object);

			if (logger.isDebugEnabled()) {
				if (cacheResult.changed) {
					logger.debug("处理缓存内容结束，返回内容有变化，cacheKey：" + sc.cacheKey);
				} else {
					logger.debug("处理缓存内容结束，返回内容无变化，cacheKey：" + sc.cacheKey);
				}
			}

			// 查询结果后处理
			cacheResult.changed = cacheResult.changed
					|| postProcessSearchResult(cacheResult.object);

		} else {
			logger.debug("缓存无效，cacheKey：" + sc.cacheKey);
		}

		return cacheResult;
	}

	private SearchResultWrapper<T> getJSONObject(SearchContext<T> sc)
			throws Exception {

		SearchResult<T> searchResult = getSearchResult(sc);

		JSONObject jsonObject = buildJSONObject(searchResult);

		// OK
		SearchResultWrapper<T> result = new SearchResultWrapper<T>();
		result.searchResult = searchResult;
		result.jsonObject = jsonObject;
		return result;
	}

	/**
	 * 根据查询参数，返回一个查询结果
	 */
	protected abstract SearchResult<T> getSearchResult(SearchContext<T> context);

	/**
	 * 根据查询结果，返回json形式的表示结果
	 */
	protected abstract JSONObject buildJSONObject(SearchResult<T> result);

	/**
	 * 对返回查询结果做一些后处理。默认处理是用 "" 替换空的JSONObject对象。
	 * 
	 * @param searchResult
	 *            既可能是来自缓存，也可能是直接来自lucene的查询结果
	 * 
	 */
	protected boolean postProcessSearchResult(JSONObject searchResult) {
		return JSONUtil.filter(searchResult);
	}

	/**
	 * 缓存的内容与返回给客户的内容可能不相同，此方法可以修改缓存的json对象，以影响返回的json串。
	 * 
	 * 
	 * @param context
	 *            查询环境
	 * @param cachedString
	 *            找到的缓存字符串
	 * @param cachedObject
	 *            找到的缓存字符串对应的json对象
	 * @return 是否修改了cachedObject对象
	 */
	protected boolean processCached(SearchContext<T> context,
			String cachedString, JSONObject cachedObject) {
		return false;
	}

}
