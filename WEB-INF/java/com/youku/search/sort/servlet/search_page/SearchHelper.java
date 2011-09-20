package com.youku.search.sort.servlet.search_page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.search.analyzer.WordProcessor;
import com.youku.search.sort.Parameter;
import com.youku.search.sort.Search;
import com.youku.search.sort.SearchInstanceMap;
import com.youku.search.sort.search.AbstractSearch;

public class SearchHelper {

	static Log logger = LogFactory.getLog(SearchHelper.class);

	public static JSONObject search(Parameter p) {

		JSONObject result = null;

		try {
			Search search = SearchInstanceMap.getSearch(p.type,
					p.isAdvanceSearch);
			// 空关键词 无效的关键词,2011.3.4 将底层的判断，移到这。其他接口可以空搜索
			if (p.query == null || p.query.isEmpty()
					|| !WordProcessor.checkKeywordIsValid(p.query)) {
				return AbstractSearch.JSON_STRING.OBJECT_ZERO_RESULT;
			}
			result = search.search(p);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		if (result == null) {
			result = new JSONObject();
		}
		
		return result;
	}
}
