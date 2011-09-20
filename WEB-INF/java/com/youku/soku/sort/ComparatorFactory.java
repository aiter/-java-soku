package com.youku.soku.sort;

import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.soku.index.entity.Video;
import com.youku.soku.util.Constant.QuerySort;

public class ComparatorFactory {
	static Log logger = LogFactory.getLog(ComparatorFactory.class);

	/**
	 * 根据前台指定的排序方式，返回一个合适的Comparator
	 */
	public static Comparator<?> get(int sort, boolean reverse) {
		if (sort == QuerySort.SORT_SCORE) {
			return reverse ? SCORE_10 : SCORE_01;
		}

		if (sort == QuerySort.SORT_NEW) {
			return reverse ? NEW_10 : NEW_01;
		}

		logger.error("未知的排序类型: " + sort);
		return reverse ? SCORE_10 : SCORE_01;
	}

	public static void main(String[] args) {
		System.out.println(get(QuerySort.SORT_NEW, true).equals(NEW_10));
		System.out.println(get(QuerySort.SORT_NEW, false).equals(NEW_01));
		System.out.println(get(QuerySort.SORT_SCORE, true).equals(SCORE_10));
		System.out.println(get(QuerySort.SORT_SCORE, false).equals(SCORE_01));
	}

	public static final Comparator<Video> SCORE_01 = new Comparator<Video>() {
		public int compare(Video v1, Video v2) {
			return Float.compare(v1.score, v2.score);
		}
	};

	public static final Comparator<Video> SCORE_10 = new Comparator<Video>() {
		public int compare(Video v1, Video v2) {
			return -1 * SCORE_01.compare(v1, v2);
		}
	};
	public static final Comparator<Video> NEW_01 = new Comparator<Video>() {
		public int compare(Video v1, Video v2) {
			return new Long(v1.uploadTime).compareTo(v2.uploadTime);
		}
	};

	public static final Comparator<Video> NEW_10 = new Comparator<Video>() {
		public int compare(Video v1, Video v2) {
			return -1 * NEW_01.compare(v1, v2);
		}
	};

}
