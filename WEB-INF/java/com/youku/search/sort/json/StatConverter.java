package com.youku.search.sort.json;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.search.index.entity.Stat;
import com.youku.search.util.StringUtil;

public class StatConverter extends AbstractConverter {

	static Log logger = LogFactory.getLog(StatConverter.class);

	public static JSONObject convert(Stat stat) {

		if (stat == null) {
			return null;
		}

		try {
			JSONObject object = new JSONObject();

			object.put("query", StringUtil.filterNull(stat.keyword));

			return object;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

}
