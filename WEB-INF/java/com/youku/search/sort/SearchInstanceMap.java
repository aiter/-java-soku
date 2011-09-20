package com.youku.search.sort;

import java.util.HashMap;
import java.util.Map;

import com.youku.search.sort.constant.SearchConstant;
import com.youku.search.sort.search.impl.AdvanceFolderSearch;
import com.youku.search.sort.search.impl.AdvanceVideoSearch;
import com.youku.search.sort.search.impl.BarPostSearch;
import com.youku.search.sort.search.impl.BarSearch;
import com.youku.search.sort.search.impl.DebugCache;
import com.youku.search.sort.search.impl.DramaSearch;
import com.youku.search.sort.search.impl.FolderSearch;
import com.youku.search.sort.search.impl.FolderTitleTagSearch;
import com.youku.search.sort.search.impl.KnowledgeSearch;
import com.youku.search.sort.search.impl.PkSearch;
import com.youku.search.sort.search.impl.RingSearch;
import com.youku.search.sort.search.impl.UserSearch;
import com.youku.search.sort.search.impl.VideoMD5Search;
import com.youku.search.sort.search.impl.VideoOnlySearch;
import com.youku.search.sort.search.impl.VideoSearch;
import com.youku.search.sort.search.impl.VideoTitleTagSearch;

/**
 * 搜索处理入口点
 */
public class SearchInstanceMap {

	public static final Map<Integer, Search> adv = new HashMap<Integer, Search>();
	public static final Map<Integer, Search> plain = new HashMap<Integer, Search>();
	
	static {
		adv.put(SearchConstant.VIDEO, AdvanceVideoSearch.I);
		adv.put(SearchConstant.FOLDER, AdvanceFolderSearch.I);

		//
		plain.put(SearchConstant.VIDEO, VideoSearch.I);
		plain.put(SearchConstant.VIDEOTAG, VideoSearch.I);
		plain.put(SearchConstant.VIDEO_MD5, VideoMD5Search.I);
		plain.put(SearchConstant.VIDEO_ONLY, VideoOnlySearch.I);
		
		// VIDEO_TITLE_TAG的逻辑修改为和VIDEO_ONLY的逻辑一样，以后再来完善
		// modified by gaosong 2011-07-14
//		plain.put(SearchConstant.VIDEO_TITLE_TAG, VideoTitleTagSearch.I);
		plain.put(SearchConstant.VIDEO_TITLE_TAG, VideoOnlySearch.I);


		plain.put(SearchConstant.FOLDER, FolderSearch.I);
		plain.put(SearchConstant.FOLDERTAG, FolderSearch.I);
		plain.put(SearchConstant.FOLDER_TITLE_TAG, FolderTitleTagSearch.I);

		plain.put(SearchConstant.MEMBER, UserSearch.I);

		plain.put(SearchConstant.BARPOST_SUBJECT, BarPostSearch.I);
		plain.put(SearchConstant.BARPOST_AUTHOR, BarPostSearch.I);
		plain.put(SearchConstant.BAR, BarSearch.I);

		plain.put(SearchConstant.PK, PkSearch.I);

		plain.put(SearchConstant.RING, RingSearch.I);
		plain.put(SearchConstant.RING_2, RingSearch.I);

		plain.put(SearchConstant.DRAMA, DramaSearch.I);
		plain.put(SearchConstant.KNOWLEDGE, KnowledgeSearch.I);

		plain.put(SearchConstant.DEBUG_CACHE, DebugCache.I);
	}

	public static Search getSearch(int type, boolean advance) {

		// 高级搜索
		if (advance) {
			Search search = adv.get(type);
			if (search == null) {
				throw new RuntimeException("未知的高级查询类型：type = " + type);
			}
			return search;
		}

		// 普通搜索
		Search search = plain.get(type);
		if (search == null) {
			throw new RuntimeException("未知的查询类型：type = " + type);
		}
		return search;
	}
}