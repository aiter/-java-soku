package com.youku.soku.manage.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.youku.soku.manage.common.Constants;

public class SearchParameter {

	public static final int STATUS_ALL = -1;

	public static final int WITH_SERIES = 1;

	public static final int SOURCE_YOUKU = 2;

	public static final int SOURCE_OTHER_SITE = 3;

	public static final int SOURCE_AUTO_SEARCH = 4;

	public static final int STATUS_NOT_COMPLETE = 5;

	public static final int STATUS_COMPLETE = 6;

	public static final int STATUS_NO_LOGO = 7;

	public static final int STATUS_BLOCKED = 8;

	private String searchWord;

	private boolean accuratelyMatched;

	private int status;

	private boolean latestUpdate;

	private int categoryId;

	private int siteId;

	private int concernLevel;

	public static final int FOWARD_WORD_ALL = -1;

	public static final int FOWARD_WORD_NO = 0;

	public static final int FOWARD_WORD_YES = 1;

	private int fowardWordStatus;

	public int getFowardWordStatus() {
		return fowardWordStatus;
	}

	public void setFowardWordStatus(int fowardWordStatus) {
		this.fowardWordStatus = fowardWordStatus;
	}

	public int getConcernLevel() {
		return concernLevel;
	}

	public void setConcernLevel(int concernLevel) {
		this.concernLevel = concernLevel;
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

	public boolean isAccuratelyMatched() {
		return accuratelyMatched;
	}

	public void setAccuratelyMatched(boolean accuratelyMatched) {
		this.accuratelyMatched = accuratelyMatched;
	}

	public static Map<Integer, String> getStatusMap() {

		Map<Integer, String> statusMap = new LinkedHashMap<Integer, String>();
		statusMap.put(STATUS_ALL, "所有");
		statusMap.put(WITH_SERIES, "拥有系列");
		statusMap.put(SOURCE_YOUKU, "优酷版权");
		statusMap.put(SOURCE_OTHER_SITE, "站外版权");
		statusMap.put(SOURCE_AUTO_SEARCH, "自动发现");
		statusMap.put(STATUS_NOT_COMPLETE, "未收录完");
		statusMap.put(STATUS_COMPLETE, "已收录完");
		// statusMap.put(STATUS_NO_LOGO, "缺少剧照");
		statusMap.put(STATUS_BLOCKED, "被屏蔽");

		Collections.unmodifiableMap(statusMap);
		return statusMap;
	}

	public static Map<Integer, String> getDirectCategory() {

		Map<Integer, String> categoryMap = new LinkedHashMap<Integer, String>();
		categoryMap.put(-1, "所有");
		categoryMap.put(Constants.TELEPLAY_CATE_ID, Constants.TELEPLAY_STR);
		categoryMap.put(Constants.MOVIE_CATE_ID, Constants.MOVIE_STR);
		categoryMap.put(Constants.VARIETY_CATE_ID, Constants.VARIETY_STR);
		categoryMap.put(Constants.ANIME_CATE_ID, Constants.ANIME_STR);
		Collections.unmodifiableMap(categoryMap);

		return categoryMap;
	}

	public static Map<Integer, String> getForwardWordCategory() {

		Map<Integer, String> categoryMap = new LinkedHashMap<Integer, String>();
		categoryMap.put(-1, "所有");
		categoryMap.put(Constants.TELEPLAY_CATE_ID, Constants.TELEPLAY_STR);
		categoryMap.put(Constants.MOVIE_CATE_ID, Constants.MOVIE_STR);
		categoryMap.put(Constants.VARIETY_CATE_ID, Constants.VARIETY_STR);
		categoryMap.put(Constants.ANIME_CATE_ID, Constants.ANIME_STR);
		categoryMap.put(Constants.PERSON_CATE_ID, Constants.PERSON_STR);
		Collections.unmodifiableMap(categoryMap);

		return categoryMap;
	}

	public static Map<Integer, String> getForwardWordStatusMap() {

		Map<Integer, String> forwardWordStatusMap = new LinkedHashMap<Integer, String>();
		forwardWordStatusMap.put(FOWARD_WORD_ALL, "全部");
		forwardWordStatusMap.put(FOWARD_WORD_YES, "有跳转词");
		forwardWordStatusMap.put(FOWARD_WORD_NO, "无跳转词");
		Collections.unmodifiableMap(forwardWordStatusMap);

		return forwardWordStatusMap;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isLatestUpdate() {
		return latestUpdate;
	}

	public void setLatestUpdate(boolean latestUpdate) {
		this.latestUpdate = latestUpdate;
	}

}
