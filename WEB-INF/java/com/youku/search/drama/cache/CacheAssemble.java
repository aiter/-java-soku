package com.youku.search.drama.cache;

import java.io.Serializable;

import com.youku.search.drama.Drama;
import com.youku.search.sort.MemCache;

/**
 * 针对单个version 缓存的对象 记录了所在电视剧的名称,和装载用的version_id
 */
public class CacheAssemble implements Serializable {

	private static final long serialVersionUID = 1L;

	public String drama_cache_key;
	public String version_id;

	public Drama getDramaFromMemCache() {
		Drama d = (Drama) MemCache.cacheGet(drama_cache_key);
		if (d != null) {
			d.setShowVersion(version_id);
		}
		return d;
	}

}
