package com.youku.search.sort.util.bridge.comparator.impl;

import java.util.Comparator;

import com.youku.search.index.entity.PrimaryKey;

public class ScoreSortComparator implements Comparator<PrimaryKey> {

	public static final ScoreSortComparator I = new ScoreSortComparator();

	@Override
	public int compare(PrimaryKey o1, PrimaryKey o2) {
		Float f1 = o1 == null ? 0 : o1.score;
		Float f2 = o2 == null ? 0 : o2.score;
		return -1 * Float.compare(f1, f2);
	}

}
