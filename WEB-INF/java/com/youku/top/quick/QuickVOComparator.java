package com.youku.top.quick;

import java.util.Comparator;

public class QuickVOComparator implements Comparator<QuickVO>{

	@Override
	public int compare(QuickVO o1, QuickVO o2) {
		if(o1.getQuery_count_sub() > o2.getQuery_count_sub())
			return -1;
		else if(o1.getQuery_count_sub() == o2.getQuery_count_sub())
			return 0;
		else return 1;
	}

}
