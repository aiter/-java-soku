package com.youku.soku.sort.words;

import java.util.List;

import com.youku.search.index.entity.Query;
import com.youku.search.index.entity.Stat;
import com.youku.search.pool.net.LockQuery;
import com.youku.search.pool.net.Lock.MergedResult;
import com.youku.search.sort.MemCache;
import com.youku.search.sort.core.search.MultiIndexSearcher;
import com.youku.search.util.Constant;
import com.youku.soku.config.Config;
import com.youku.soku.sort.Parameter;

public class CorrectWords extends AbstractWords<String> {

	public CorrectWords() {
		searchType = Constant.QueryField.STAT_PINYIN;
	}

	@Override
	public String getWord(Parameter p) {

		try {
			return getWord_(p);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	String getWord_(Parameter p) {

		if (Config.getSuggestionStatus() < 1) {
			logger.debug("当前配置禁用了拼音纠错");
			return null;
		}

		if (p.keyword == null || p.keyword.length() == 0) {
			logger.debug("查询关键词为空，返回空结果");
			return null;
		}

		String cacheKey = getCacheKey(p.keyword);

		//
		if (logger.isDebugEnabled()) {
			logger.debug("检查缓存，cacheKey: " + cacheKey);
		}
		String theWord = (String) MemCache.cacheGet(cacheKey);

		if (theWord != null && theWord.length() > 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("缓存有效，cacheKey: " + cacheKey + ", word: "
						+ theWord);
			}
			return theWord;
		}

		//
		if (logger.isDebugEnabled()) {
			logger.debug("缓存无效，cacheKey: " + cacheKey);
		}

		Query object = new Query();
		object.start = 0;
		object.end = 1;
		object.reverse = true;
		object.keywords = p.keyword;
		object.operator = Constant.Operator.AND;
		object.field = searchType;

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

		// lucene后台保证查询结果不会包含查询关键词query
		List<Stat> wordList = result.list;
		if (wordList == null || wordList.isEmpty()) {
			return null;
		}

		if (result.miss) {
			if (logger.isDebugEnabled()) {
				logger.debug("部分lucene server没有返回结果，不缓存查询结果， cacheKey："
						+ cacheKey);
			}

		} else {
			theWord = wordList.get(0).keyword;

			if (logger.isDebugEnabled()) {
				logger.debug("缓存查询结果， cacheKey：" + cacheKey + ", jsonString: "
						+ theWord);
			}

			MemCache.cacheSet(cacheKey, theWord, cache_seconds);
		}

		return theWord;
	}

}
