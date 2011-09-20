package com.youku.search.sort.search.impl.helper;

import static com.youku.search.sort.json.AbstractConverter.RELEVANT_WORDS;
import static com.youku.search.sort.json.AbstractConverter.SUGGESTION;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.search.sort.core.SearchContext;
import com.youku.search.sort.json.util.JSONUtil;
import com.youku.search.sort.search.impl.RelevantWords;
import com.youku.search.sort.search.impl.Suggestion;
import com.youku.search.sort.search.impl.VideoSearch.VideoSearchOptions;
import com.youku.search.util.StringUtil;

public class HelperForCache {

	static final Log logger = LogFactory.getLog(HelperForCache.class);

	/**
	 * 
	 * 建议词和相关词单独缓存，所以，需要添加到查询结果中
	 * 
	 * @return cachedObject是否被修改
	 */
	public static <T> boolean processCached(SearchContext<T> context,
			String cachedString, JSONObject cachedObject) {

		boolean changed = false;

		final VideoSearchOptions options = context.p.options;

		String suggestion = null;
		JSONObject relevant = null;

		if (options.bitSet.get(options.suggestionWord)) {
			suggestion = Suggestion.getSuggestionWord(context.p);
			suggestion = StringUtil.filterNull(suggestion);

			try {
				cachedObject.put(SUGGESTION, suggestion);

				if (logger.isDebugEnabled()) {
					logger.debug("在查询结果中添加纠错：" + suggestion);
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}

			changed = true;
		}

		if (options.bitSet.get(options.relevantWord)) {
			relevant = RelevantWords.getWordsJSONObject(context.p);
			Object value = JSONUtil.filterEmptyObjectAndProps(relevant);

			try {
				cachedObject.put(RELEVANT_WORDS, value);

				if (logger.isDebugEnabled()) {
					logger.debug("在查询结果中添加相关词：" + relevant);
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}

			changed = true;
		}

		return changed;
	}

	/**
	 * 建议词和相关词单独缓存，所以，需要从查询结果中去掉他们
	 * 
	 * @return jsonObject是否被修改
	 */
	public static <T> boolean processForCache(SearchContext<T> context,
			JSONObject jsonObject, String jsonString) {

		VideoSearchOptions options = context.p.options;

		boolean changed = false;
		if (options.bitSet.get(options.suggestionWord)) {
			if (jsonObject.has(SUGGESTION)) {
				changed = true;

				String suggestion = null;
				suggestion = (String) jsonObject.remove(SUGGESTION);

				if (logger.isDebugEnabled()) {
					logger.debug("在查询结果中删除纠错：" + suggestion);
				}
			}
		}

		if (options.bitSet.get(options.relevantWord)) {
			if (jsonObject.has(RELEVANT_WORDS)) {
				changed = true;

				Object relevant = jsonObject.remove(RELEVANT_WORDS);

				if (logger.isDebugEnabled()) {
					logger.debug("在查询结果中删除相关词：" + relevant);
				}
			}
		}

		return changed;
	}
}
