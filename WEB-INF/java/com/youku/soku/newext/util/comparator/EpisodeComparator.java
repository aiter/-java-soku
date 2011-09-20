package com.youku.soku.newext.util.comparator;

import java.io.Serializable;
import java.util.Comparator;

import com.youku.soku.library.load.ProgrammeEpisode;

/**
 * 按order_id倒序排序
 */
public class EpisodeComparator implements Comparator<ProgrammeEpisode>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public int compare(ProgrammeEpisode o1, ProgrammeEpisode o2) {
		if(o1.getOrderId()>o2.getOrderId()){
			return -1;
		}else if(o1.getOrderId()<o2.getOrderId()){
			return 1;
		}else{
			return 0;
		}
		
	}
}
