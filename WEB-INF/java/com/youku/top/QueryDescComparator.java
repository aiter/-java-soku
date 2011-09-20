package com.youku.top;

import java.util.Comparator;

public class QueryDescComparator implements Comparator<AnimeEntry>{

	@Override
	public int compare(AnimeEntry o1, AnimeEntry o2) {
		if(o1.getCount() < o2.getCount())
			return 1;
		else if(o1.getCount() == o2.getCount())
			return 0;
		else return -1;
	}

	

}
