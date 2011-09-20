/**
 * 
 */
package com.youku.soku.newext.util.comparator;

import java.util.Comparator;

import com.youku.soku.library.load.ProgrammeEpisode;

/**
 * @author liuyunjian
 * 2011-4-26
 */
public class EpisodeOrderComparator implements Comparator<ProgrammeEpisode> {

	@Override
	public int compare(ProgrammeEpisode o1, ProgrammeEpisode o2) {
		if(o1==null && o2==null){
			return 0;
		}
		if(o1==null){
			return -1;
		}
		if(o2==null){
			return 1;
		}
		return o1.getOrderId()-o2.getOrderId();
	}

}
