package com.youku.search.sort.search.impl;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import com.youku.search.config.Config;
import com.youku.search.drama.Drama;
import com.youku.search.drama.Drama.Type;
import com.youku.search.drama.cache.DramaSearcher;
import com.youku.search.index.entity.Folder;
import com.youku.search.index.entity.Query;
import com.youku.search.index.entity.Video;
import com.youku.search.pool.net.LockQuery;
import com.youku.search.sort.Parameter;
import com.youku.search.sort.VideoMusicRecommend;
import com.youku.search.sort.VideoRecommend;
import com.youku.search.sort.constant.SearchConstant;
import com.youku.search.sort.core.IndexSearchResult;
import com.youku.search.sort.core.IndexSearcher;
import com.youku.search.sort.core.SearchContext;
import com.youku.search.sort.core.search.MultiIndexSearcher;
import com.youku.search.sort.core.search.MultiIndexSearcher.DuplicateMergedResult;
import com.youku.search.sort.entity.CategoryCountBean;
import com.youku.search.sort.json.VideoConverter;
import com.youku.search.sort.major_term.MajorTermSearcher;
import com.youku.search.sort.search.impl.helper.HelperForCache;
import com.youku.search.sort.search.impl.helper.HelperForDramaCache;
import com.youku.search.sort.util.FolderMapVideo;
import com.youku.search.sort.util.VideoCategoryCount;
import com.youku.search.sort.util.bridge.SearchUtil;
import com.youku.search.sort.util.bridge.comparator.impl.FolderSortComparator;
import com.youku.search.util.Constant;

public class VideoSearch extends AbstractVideoSearch {

	public static class VideoSearchOptions {

		public final BitSet bitSet = new BitSet();

		public final int recommendVideo;
		public final int categoryCount;
		public final int relatedFolder;
		public final int relatedDrama;
		public final int dupCount;
		public final int suggestionWord;
		public final int relevantWord;
		public final int copyrightMusic;
		public final int majorTerm;

		public final int size;// 参数总数
		public final int default_start;// 包含
		public final int default_end;// 包含

		public VideoSearchOptions() {

			// 默认参数
			recommendVideo = 0;
			categoryCount = 1;
			relatedFolder = 2;
			relatedDrama = 3;
			dupCount = 4;
			suggestionWord = 5;
			relevantWord = 6;
			copyrightMusic = 7;
			majorTerm = 8;

			default_start = recommendVideo;
			default_end = relevantWord;

			// 扩展参数

			// OK
			size = 9;

			setToDefault();
		}

		public void setToDefault() {
			for (int i = 0; i < size; i++) {
				bitSet.set(i);
			}

			bitSet.clear(relatedFolder);
		}

		public void clearAll() {
			bitSet.clear();
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < size; i++) {
				if (bitSet.get(i)) {
					builder.append('1');
				} else {
					builder.append('0');
				}
			}
			return builder.toString();
		}

		public static void main(String[] args) {
			VideoSearchOptions options = new VideoSearchOptions();
			System.out.println(options);
		}
	}

	public static class VideoSearchResult extends SearchResult<Video> {
		public List<Folder> folderList;
		public Drama drama;
		public Drama zongyi;
		public int[] dupCount;
		public List<CategoryCountBean> categoryCount;
		public String suggestionWord;
		public JSONObject relevantWord;
		public JSONObject majorTerm;

		public VideoSearchOptions options = new VideoSearchOptions();

		public VideoSearchResult(SearchResult<Video> searchResult) {
			super(searchResult);
		}

		public VideoSearchResult(IndexSearchResult<Video> indexSearchResult) {
			super(indexSearchResult);
		}
	}

	public static final AbstractVideoSearch I = new VideoSearch();
	
	public VideoSearch() {
		super("video", ShieldChannelTarget.VIDEO);
	}
	
	@Override
	protected SearchResult<Video> getSearchResult(SearchContext<Video> context) {
		
		final VideoSearchOptions options = context.p.options;
		
		boolean count = false;
		
		if (Config.getDuplicateStatus() > 0) {
			
			IndexSearchResult<Video> indexSearchResult = getIndexSearchResult(context);
			
			
			List<Long> optionsCost = new ArrayList<Long>();
			
			long getRecommendVideoStart = System.currentTimeMillis();
			if (options.bitSet.get(options.recommendVideo)) {
				int add = addRecommendVideo(indexSearchResult.list, context.p);
				indexSearchResult.totalCount += add;
			}
			optionsCost.add(System.currentTimeMillis() - getRecommendVideoStart);
			
			// 2010.12.28 改为将版权音乐放到第5条
			// 2011.07.12 先关闭此处逻辑
//			if (options.bitSet.get(options.copyrightMusic)) {
//				int add = addCopyrightMusic(indexSearchResult.list, context.p);
//				indexSearchResult.totalCount += add;
//			}

			List<Folder> folderList = null;
			if (options.bitSet.get(options.relatedFolder)) {
				folderList = getRelatedFolder(context);
			}
			
			long getDramaAndZongyiStart = System.currentTimeMillis();
			Drama drama = null;
			Drama zongyi = null;
			if (options.bitSet.get(options.relatedDrama)) {
				drama = getRelatedDrama(context);
				zongyi = getRelatedZongyi(context);
			}
			optionsCost.add(System.currentTimeMillis() - getDramaAndZongyiStart);

			int[] dupCount = null;
			if (options.bitSet.get(options.dupCount)) {
				dupCount = indexSearchResult.dupCountPage();
			}

			List<CategoryCountBean> categoryCount = null;
			if (options.bitSet.get(options.categoryCount) && count) {
				categoryCount = VideoCategoryCount.getInstance().count(
						indexSearchResult.list, indexSearchResult.totalCount);
			}

			long getSuggestionStart = System.currentTimeMillis();
			String suggestionWord = null;
			if (options.bitSet.get(options.suggestionWord)) {
				suggestionWord = Suggestion.getSuggestionWord(context.p);
			}
			optionsCost.add(System.currentTimeMillis() - getSuggestionStart);

			long getRelevantWordStart = System.currentTimeMillis();
			JSONObject relevantWord = null;
			if (options.bitSet.get(options.relevantWord)) {
				relevantWord = RelevantWords.getWordsJSONObject(context.p);
			}
			optionsCost.add(System.currentTimeMillis() - getRelevantWordStart);

			// 检查大词
			JSONObject majorTerm = null;
			if (options.bitSet.get(options.majorTerm)) {
				try {
					JSONObject termObject = MajorTermSearcher
							.search(context.p.query);
					if (termObject != null) {
						majorTerm = termObject;
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
			
			indexSearchResult.cost.add(optionsCost);
			
			// ok
			VideoSearchResult result = new VideoSearchResult(indexSearchResult);
			result.folderList = folderList;
			result.drama = drama;
			result.zongyi = zongyi;
			result.dupCount = dupCount;
			result.categoryCount = categoryCount;
			result.suggestionWord = suggestionWord;
			result.relevantWord = relevantWord;
			result.majorTerm = majorTerm;
			
			result.options = options;
			
			return result;

		} else {

			IndexSearchResult<Video> indexSearchResult = IndexSearcher
					.search(context);

			int add = addRecommendVideo(indexSearchResult.list, context.p);
			indexSearchResult.totalCount += add;

			VideoSearchResult result = new VideoSearchResult(indexSearchResult);

			List<CategoryCountBean> categoryCount = count ? VideoCategoryCount
					.getInstance().count(indexSearchResult.list,
							indexSearchResult.totalCount) : null;

			result.folderList = getRelatedFolder(context);
			result.drama = getRelatedDrama(context);
			result.zongyi = getRelatedZongyi(context);
			result.dupCount = null;
			result.categoryCount = categoryCount;
			result.suggestionWord = Suggestion.getSuggestionWord(context.p);
			result.relevantWord = RelevantWords.getWordsJSONObject(context.p);
			result.majorTerm = MajorTermSearcher.search(context.p.query);

			return result;
		}

	}

	@Override
	protected JSONObject buildJSONObject(SearchResult<Video> result) {
		VideoSearchResult r = (VideoSearchResult) result;
		JSONObject jsonObject = VideoConverter.convert(r);
		return jsonObject;
	}
	
	private int addRecommendVideo(List<Video> resultList, Parameter p) {
		// 2011.4.26 关闭推荐功能
		if (true)
			return 0;

		if (p.curPage == 1 && p.type == SearchConstant.VIDEO
				&& p.orderField == Constant.Sort.SORT_SCORE) {

			List<Video> recommends = VideoRecommend.getCachedVideoRecommend(
					p.query, p.type);

			if (recommends != null && !recommends.isEmpty()) {
				int addedCount = recommends.size();
				Set<Integer> ids = new HashSet<Integer>();
				for (Video video : recommends) {
					ids.add(video.vid);
				}

				for (Iterator<Video> i = resultList.iterator(); i.hasNext();) {
					Video video = i.next();
					if (ids.contains(video.vid)) {
						i.remove();
						--addedCount;
					}
				}
				resultList.addAll(0, recommends);
				return addedCount;
			}
		}

		return 0;
	}

	/**
	 * 如果符合条件，添加一条有版权的音乐到搜索结果的第一条 2010.12.28 改为将版权音乐放到第5条
	 * 
	 * @param resultList
	 * @param p
	 * @return
	 */
	private int addCopyrightMusic(List<Video> resultList, Parameter p) {

		if (p.curPage == 1 && p.type == SearchConstant.VIDEO
				&& p.orderField == Constant.Sort.SORT_SCORE) {

			List<Video> recommends = VideoMusicRecommend.getCachedVideoMusic(
					p.query, p.type);

			if (recommends != null && !recommends.isEmpty()) {
				int addedCount = 1;// 只取第一条

				int id = recommends.get(0).vid;

				for (Iterator<Video> i = resultList.iterator(); i.hasNext();) {
					Video video = i.next();
					if (id == video.vid) {
						i.remove();
						--addedCount;
					}
				}
				// 2010.12.28 改为将版权音乐放到第5条
				if (resultList.size() >= 4) {
					resultList.add(4, recommends.get(0));
				} else {
					resultList.add(resultList.size(), recommends.get(0));
				}
				return addedCount;
			}
		}

		return 0;
	}

	@Override
	protected boolean processCached(SearchContext<Video> context,
			String cachedString, JSONObject cachedObject) {

		boolean changed = HelperForCache.processCached(context, cachedString,
				cachedObject);

		changed = HelperForDramaCache.processCached(context, cachedString,
				cachedObject) || changed;

		return changed;
	}

	// TODO 将来把这个类移到合适的类中
	private List<Folder> getRelatedFolder(SearchContext<Video> vContext) {

		final int folderNum = Config.getFolderNumEmbededVideoSearch();
		if (vContext.p.curPage != 1 || folderNum < 1) {
			return null;
		}

		int folderCateId = FolderMapVideo
				.getFolderCateIdByVideo(vContext.p.cateId);

		int folderSearchType = Constant.QueryField.FOLDER;
		if (vContext.searchType == Constant.QueryField.VIDEOTAG) {
			folderSearchType = Constant.QueryField.FOLDERTAG;
		}

		Query folderObject = new Query();
		folderObject.start = 0;
		folderObject.end = folderNum;
		folderObject.sort = Constant.Sort.SORT_SCORE;
		folderObject.reverse = true;
		folderObject.keywords = vContext.p.query;
		folderObject.category = folderCateId;
		folderObject.partner = vContext.p.partnerId;
		folderObject.operator = vContext.p.logic;
		folderObject.field = folderSearchType;

		InetSocketAddress[] servers = SearchUtil
				.getLuceneServers(folderSearchType);

		LockQuery lockQuery = new LockQuery(servers, folderObject);
		int minResult = folderNum;
		Comparator<Folder> comparator = FolderSortComparator.SCORE_ORDER_REVERSE;

		DuplicateMergedResult<Folder> duplicateMergedResult = MultiIndexSearcher.I
				.searchWithDuplicate(lockQuery, minResult, comparator);

		final int end = Math.min(folderNum, duplicateMergedResult.list.size());

		List<Folder> list = new ArrayList<Folder>();
		for (int i = 0; i < end; i++) {
			list.add(duplicateMergedResult.list.get(i));
		}

		return list;
	}

	private Drama getRelatedDrama(SearchContext<Video> context) {
		return DramaSearcher.searchByName(context.p.query, Type.DRAMA);
	}

	private Drama getRelatedZongyi(SearchContext<Video> context) {
		return DramaSearcher.searchByName(context.p.query, Type.ZONGYI);
	}

}
