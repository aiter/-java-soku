package com.youku.search.sort.search.impl.helper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.search.drama.Drama;
import com.youku.search.drama.Drama.Type;
import com.youku.search.drama.cache.DramaSearcher;
import com.youku.search.sort.core.SearchContext;
import com.youku.search.sort.json.drama.DramaConverter;
import com.youku.search.sort.json.util.JSONUtil;
import com.youku.search.sort.search.impl.VideoSearch.VideoSearchOptions;

public class HelperForDramaCache {

	static final Log logger = LogFactory.getLog(HelperForDramaCache.class);

	/**
	 * 剧集搜索结果单独缓存，所以，需要添加到查询结果中
	 * 
	 * @return cachedObject是否被修改
	 */
	public static <T> boolean processCached(SearchContext<T> context,
			String cachedString, JSONObject cachedObject) {

		final VideoSearchOptions options = context.p.options;

		boolean changed = false;

		if (options.bitSet.get(options.relatedDrama)) {
			changed = true;

			Drama drama = DramaSearcher.searchByName(context.p.query,
					Type.DRAMA);
			JSONObject dramaObject = DramaConverter.convert(drama);
			dramaObject = JSONUtil.newIfNull(dramaObject);

			Drama zongyi = DramaSearcher.searchByName(context.p.query,
					Type.ZONGYI);
			JSONObject zongyiObject = DramaConverter.convert(zongyi);
			zongyiObject = JSONUtil.newIfNull(zongyiObject);

			if (logger.isDebugEnabled()) {
				logger.debug("在缓存的对象上添加剧集搜索：" + dramaObject);
				logger.debug("在缓存的对象上添加综艺搜索：" + zongyiObject);
			}

			try {
				cachedObject.put("drama", JSONUtil
						.filterEmptyObjectAndProps(dramaObject));

				cachedObject.put("zongyi", JSONUtil
						.filterEmptyObjectAndProps(zongyiObject));

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		return changed;
	}

	/**
	 * 剧集搜索结果单独缓存，所以，需要从查询结果中删除
	 * 
	 * @return jsonObject是否被修改
	 */
	public static <T> boolean processForCache(SearchContext<T> context,
			JSONObject jsonObject, String jsonString) {

		final VideoSearchOptions options = context.p.options;

		boolean changed = false;

		if (options.bitSet.get(options.relatedDrama)) {
			changed = true;

			Object dramaObject = jsonObject.remove("drama");

			if (logger.isDebugEnabled()) {
				logger.debug("在缓存的对象上删除剧集搜索：" + dramaObject);
			}

			Object zongyiObject = jsonObject.remove("zongyi");
			if (logger.isDebugEnabled()) {
				logger.debug("在缓存的对象上删除剧集搜索：" + zongyiObject);
			}
		}

		return changed;
	}

}
