package com.youku.top.test;

import java.util.Comparator;

public class InfoDescComparator implements Comparator<Info>{

	@Override
	public int compare(Info o1, Info o2) {
		if(o1.getCount() < o2.getCount())
			return 1;
		else if(o1.getCount() == o2.getCount())
			return 0;
		else return -1;
	}

	

}
