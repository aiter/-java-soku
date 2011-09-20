package com.youku.search.sort.util.bridge.comparator.impl;

import java.util.Comparator;

import com.youku.search.index.entity.Video;
import com.youku.search.sort.util.bridge.comparator.AbstractSortComparator;
import com.youku.search.util.Constant;

public class VideoFullMatchSortComparator extends AbstractSortComparator<Video> {

	public static final VideoFullMatchSortComparator I = new VideoFullMatchSortComparator();

	@Override
	public int getOrderField(String orderField) {
		return Constant.Sort.SORT_SCORE;
	}

	@Override
	public Comparator<Video> getComparator(int orderField, boolean reverse) {
		return VideoSortComparator.SCORE_ORDER_REVERSE;
	}

}
