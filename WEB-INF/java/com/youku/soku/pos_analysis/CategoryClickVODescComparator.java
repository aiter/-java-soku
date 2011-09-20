package com.youku.soku.pos_analysis;

import java.util.Comparator;

import com.youku.soku.pos_analysis.entity.CategoryClickVO;

public class CategoryClickVODescComparator implements Comparator<CategoryClickVO> {

	@Override
	public int compare(CategoryClickVO o1, CategoryClickVO o2) {
		if(o1.getClick_nums() < o2.getClick_nums())
			return 1;
		else if(o1.getClick_nums() == o2.getClick_nums())
			return 0;
		else return -1;
	}

}
