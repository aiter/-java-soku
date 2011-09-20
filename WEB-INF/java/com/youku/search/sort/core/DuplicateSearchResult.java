package com.youku.search.sort.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.youku.search.sort.core.entity.Span;
import com.youku.search.sort.core.search.MultiIndexSearcher.DuplicateMergedResult;

/**
 * 在IndexSearchResult的基础上视频去重信息
 */
public class DuplicateSearchResult<T> extends IndexSearchResult<T> {

	public Map<String, Integer> md5Map = new HashMap<String, Integer>();
	public Map<T, Integer> objectMap = new HashMap<T, Integer>();

	public DuplicateSearchResult() {
	}

	public DuplicateSearchResult(DuplicateMergedResult<T> merged, Span span) {
		super(merged, span);

		md5Map.putAll(merged.md5Map);
		objectMap.putAll(merged.objectMap);
	}

	@Override
	public int[] dupCountPage() {

		List<T> page = page();

		int[] count = new int[page.size()];
		for (int i = 0; i < page.size(); i++) {
			T object = page.get(i);
			if (objectMap.containsKey(object)) {
				count[i] = objectMap.get(object);
			} else {
				count[i] = 1;
			}
		}

		return count;
	}
}
