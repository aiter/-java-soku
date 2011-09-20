package com.youku.search.sort.search.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import com.youku.search.index.entity.User;
import com.youku.search.sort.Parameter;
import com.youku.search.sort.VideoRecommend;
import com.youku.search.sort.core.IndexSearchResult;
import com.youku.search.sort.core.IndexSearcher;
import com.youku.search.sort.core.SearchContext;
import com.youku.search.sort.json.UserConverter;
import com.youku.search.sort.search.AbstractSearchTemplate;
import com.youku.search.util.Constant;

public class UserSearch extends AbstractSearchTemplate<User> {

	public static final UserSearch I = new UserSearch();

	public UserSearch() {
		super("user");
	}

	@Override
	protected SearchResult<User> getSearchResult(SearchContext<User> context) {

		IndexSearchResult<User> indexSearchResult = IndexSearcher
				.search(context);

		int add = addRecommendUser(indexSearchResult.list, context.p);
		indexSearchResult.totalCount += add;

		SearchResult<User> result = new SearchResult<User>(indexSearchResult);
		return result;
	}

	@Override
	protected JSONObject buildJSONObject(SearchResult<User> result) {

		JSONObject jsonObject = UserConverter
				.convert(result.page, result.total);

		return jsonObject;
	}

	private int addRecommendUser(List<User> resultList, Parameter p) {

		if (p.curPage == 1 && p.orderField == Constant.Sort.SORT_SCORE) {

			List<User> recommends = VideoRecommend.getCachedVideoRecommend(
					p.query, p.type);

			if (recommends != null && !recommends.isEmpty()) {
				int addedCount = recommends.size();
				Set<Integer> ids = new HashSet<Integer>();
				for (User user : recommends) {
					ids.add(user.pk_user);
				}

				for (Iterator<User> i = resultList.iterator(); i.hasNext();) {
					User user = i.next();
					if (ids.contains(user.pk_user)) {
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
}
