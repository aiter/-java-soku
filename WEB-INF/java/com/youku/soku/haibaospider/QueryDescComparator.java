package com.youku.soku.haibaospider;

import java.util.Comparator;

public class QueryDescComparator implements Comparator<ResultVO>{

	@Override
	public int compare(ResultVO o1, ResultVO o2) {
		if(o1.getQueryCount() < o2.getQueryCount())
			return 1;
		else if(o1.getQueryCount() == o2.getQueryCount())
			return 0;
		else return -1;
	}

	

}
