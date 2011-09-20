package com.youku.search.sort.search.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import com.youku.search.index.entity.Folder;
import com.youku.search.sort.VideoRecommend;
import com.youku.search.sort.constant.SearchConstant;
import com.youku.search.sort.core.IndexSearchResult;
import com.youku.search.sort.core.IndexSearcher;
import com.youku.search.sort.core.SearchContext;
import com.youku.search.sort.entity.CategoryCountBean;
import com.youku.search.sort.json.FolderConverter;
import com.youku.search.sort.search.AbstractSearchTemplate;
import com.youku.search.sort.search.impl.helper.HelperForCache;
import com.youku.search.sort.util.FolderCategoryCount;

public class FolderSearch extends AbstractSearchTemplate<Folder> {

	public static class FolderSearchResult extends SearchResult<Folder> {
		public List<CategoryCountBean> categoryCount;
		public String suggestionWord;
		public JSONObject relevantWord;

		public FolderSearchResult(IndexSearchResult<Folder> indexSearchResult) {
			super(indexSearchResult);
		}
	}

	public static final FolderSearch I = new FolderSearch();

	public FolderSearch() {
		super("folder", ShieldChannelTarget.FOLDER);
	}

	@Override
	protected SearchResult<Folder> getSearchResult(SearchContext<Folder> context) {

		IndexSearchResult<Folder> searchResult = IndexSearcher
				.searchWithDuplicate(context);

		if (context.p.curPage == 1 && context.p.type == SearchConstant.FOLDER) {

			List<Folder> recommends = VideoRecommend.getCachedVideoRecommend(
					context.p.query, context.p.type);

			if (recommends != null && !recommends.isEmpty()) {

				int addedCount = recommends.size();

				Set<Integer> ids = new HashSet<Integer>();
				for (Folder folder : recommends) {
					ids.add(folder.pk_folder);
				}

				for (Iterator<Folder> i = searchResult.list.iterator(); i
						.hasNext();) {
					Folder folder = i.next();
					if (ids.contains(folder.pk_folder)) {
						i.remove();
						--addedCount;
					}
				}

				searchResult.list.addAll(0, recommends);
				searchResult.totalCount += addedCount;
			}
		}

		boolean count = false;

		List<CategoryCountBean> countBeanList = count ? FolderCategoryCount
				.getInstance()
				.count(searchResult.list, searchResult.totalCount) : null;

		FolderSearchResult result = new FolderSearchResult(searchResult);

		result.categoryCount = countBeanList;
		result.suggestionWord = Suggestion.getSuggestionWord(context.p);
		result.relevantWord = RelevantWords.getWordsJSONObject(context.p);

		return result;
	}

	@Override
	protected JSONObject buildJSONObject(SearchResult<Folder> result) {

		FolderSearchResult r = (FolderSearchResult) result;
		JSONObject jsonObject = FolderConverter.convert(r);
		return jsonObject;
	}

	@Override
	protected boolean processCached(SearchContext<Folder> context,
			String cachedString, JSONObject cachedObject) {

		return HelperForCache
				.processCached(context, cachedString, cachedObject);
	}

}
