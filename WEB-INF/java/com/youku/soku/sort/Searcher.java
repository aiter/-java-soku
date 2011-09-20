package com.youku.soku.sort;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

/**
 * 只搜索视频的接口。
 */
public class Searcher {

	static Log logger = LogFactory.getLog(Searcher.class);

	public static JSONObject search(Parameter parameter) throws Exception {

		JSONObject object = Searcher_mix_site.INSTANCE.search(parameter);

		if (object == null) {
			return null;
		}

		return object;
	}

}