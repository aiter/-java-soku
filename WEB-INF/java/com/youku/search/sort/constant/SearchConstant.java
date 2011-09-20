package com.youku.search.sort.constant;

/**
 * web前端的查询类型
 */

public class SearchConstant {

	public static final int VIDEO = 1;
	public static final int VIDEOTAG = 8;
	public static final int VIDEO_MD5 = 18;
	// public static final int VIDEO_FULL_MATCH = 19;
	public static final int VIDEO_ONLY = 21;
	public static final int VIDEO_TITLE_TAG = 22;
	public static final int KNOWLEDGE = 24;
	
	public static final int FOLDER = 2;
	public static final int FOLDERTAG = 10;
	public static final int FOLDER_TITLE_TAG = 23;

	public static final int MEMBER = 3;

	public static final int BARPOST_SUBJECT = 4;
	public static final int BARPOST_AUTHOR = 7;
	public static final int BAR = 9;

	public static final int PK = 5;

	public static final int RING = 6;
	public static final int RING_2 = 17;

	public static final int STAT_PINYIN = 15;
	public static final int STAT_KEYWORD = 16;

	public static final int DRAMA = 20;

	public static final int DEBUG_CACHE = 109;
	
	public static boolean isNeedSortType(int type) {
		if (type == DRAMA) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
	}
}
