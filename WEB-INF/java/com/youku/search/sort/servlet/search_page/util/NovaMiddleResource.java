package com.youku.search.sort.servlet.search_page.util;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.search.sort.servlet.util.WebUtils;
import com.youku.search.util.Wget;

/**
 * 测试地址：http://10.101.19.33/test.html
 * 
 * 文档地址：http://wiki.1verge.net/ds:overview
 * 
 */
public class NovaMiddleResource {

//	public static final String server = "10.101.88.226";
	public static final String server = "10.103.88.54";//2011.3.8 改为新ip

	static Log logger = LogFactory.getLog(NovaMiddleResource.class);

	public static JSONObject search(String resourceType,
			String resourceSubType, String query, Map<String, String> params) {
		return search(server, resourceType, resourceSubType, query, params);
	}

	public static JSONObject search(String server, String resourceType,
			String resourceSubType, String query, Map<String, String> params) {

		StringBuilder builder = new StringBuilder();

		try {
			builder.append("http://");
			builder.append(server);
			builder.append("/");

			builder.append(resourceType);
			if (resourceSubType != null) {
				builder.append(".");
				builder.append(resourceSubType);
			}

			builder.append("?q=");
			builder.append(WebUtils.urlEncode(query));

			if (params != null) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();

					builder.append("&");
					builder.append(WebUtils.urlEncode(key));
					builder.append("=");
					builder.append(WebUtils.urlEncode(value));
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug("url: " + builder);
			}

			byte[] bytes = Wget.get(builder.toString(), 3000);
			JSONObject jsonObject = new JSONObject(new String(bytes, "utf-8"));

			if (logger.isDebugEnabled()) {
				logger.debug("response:\n" + jsonObject.toString(4));
			}

			return jsonObject;

		} catch (Exception e) {
//			logger.error("抓取数据发生异常, url: " + builder, e);
			logger.error("抓取数据发生异常, url: " + builder);
			return null;
		}
	}
}
