package com.youku.search.drama.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.search.drama.Drama;
import com.youku.search.drama.Drama.Type;
import com.youku.search.sort.MemCache;
import com.youku.search.util.StringUtil;

public class DramaSearcher {
	static Log logger = LogFactory.getLog(DramaSearcher.class);

	/**
	 * 根据名称去匹配
	 * 
	 * @param name
	 * @return
	 */

	public static Drama searchByName(String name, Type t) {

		if (name == null || name.length() == 0) {
			return null;
		}

		final String key = CacheConstant.CACHE_KEY(t)
				+ DramaNames.formatName(name);

		Drama drama = cacheGet(key);

		log_debug(key, drama);
		return drama;
	}

	/**
	 * 根据视频id去匹配，此id是否在剧集内，是则返回对应的电视剧对象
	 */
	public static JSONObject searchByVideoId(String videoId) {
		return DramaCache.getByVideoId(videoId);
	}

	public static JSONObject getEpisodeVideos(String episodeVideoId, int count) {
		int id = StringUtil.parseInt(episodeVideoId, -1, -1);
		if (id == -1) {
			return null;
		}

		return getEpisodeVideos(id, count);
	}

	public static JSONObject getEpisodeVideos(int episodeVideoId, int count) {
		return DramaCache.getEpisodeVideos(episodeVideoId, count);
	}

	private static Drama cacheGet(String key) {
		logger.debug("query drama_cache_key: " + key);

		Object object = MemCache.cacheGet(key);

		if (object == null) {
			return null;
		}

		if (object instanceof Drama) {
			return (Drama) object;
		}

		if (object instanceof CacheAssemble) {
			return ((CacheAssemble) object).getDramaFromMemCache();
		}

		if (object instanceof String) {
			return cacheGet((String) object);
		}

		log_error(object);
		return null;
	}

	private static void log_error(Object o) {
		logger.error("不应该出现这种情况！ object: " + o + "; object's class: "
				+ (o == null ? null : o.getClass()));
	}

	private static void log_debug(String key, Object o) {

		if (logger.isDebugEnabled()) {
			StringBuilder builder = new StringBuilder();

			builder.append("query drama_cache_key: " + key + "; ");
			if (o == null) {
				builder.append("NOT found!");
			} else {
				builder.append("found!");
			}

			logger.debug(builder.toString());
		}
	}

	public static void debug(String name) {
		for (Type type : Type.values()) {
			debug(name, type);
		}
	}

	public static void debug(String name, Type type) {
		final String drama_key = CacheConstant.CACHE_KEY(type)
				+ DramaNames.formatName(name);

		System.out.println("type: " + type.name());
		System.out.println("    drama key: " + drama_key);

		String md5_key = MemCache.processKey(drama_key);
		System.out.println("    md5 key: " + md5_key);

		int md5_hash = md5_key.hashCode();
		System.out.println("    md5Key hash: " + md5_hash);
		System.out.println("    md5Key hash % 4: " + (md5_hash % 4));
	}

	public static void main(String[] args) {
		debug("海贼王");
	}
}
