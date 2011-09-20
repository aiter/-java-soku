package com.youku.top.paihangbang;

import java.util.Comparator;

import com.youku.top.paihangbang.entity.TopWordsEntity;

public class TopWordComparator implements Comparator<TopWordsEntity>{

	@Override
	public int compare(TopWordsEntity o1, TopWordsEntity o2) {
		if(o1.getTopwords().getQueryCount() < o2.getTopwords().getQueryCount())
			return 1;
		else if(o1.getTopwords().getQueryCount() == o2.getTopwords().getQueryCount())
			return 0;
		else return -1;
	}



}
