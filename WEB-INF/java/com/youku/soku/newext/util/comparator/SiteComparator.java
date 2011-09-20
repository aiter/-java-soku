/**
 * 
 */
package com.youku.soku.newext.util.comparator;

import java.util.Comparator;

import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.newext.util.ProgrammeSiteType;

/**
 * @author liuyunjian
 * 2011-4-26
 */
public class SiteComparator implements Comparator<ProgrammeSite> {
	
	/** 
	 *优酷收录完成的，优酷优先
	 *未完成的，集数多的优先
	 */
	@Override
	public int compare(ProgrammeSite o1, ProgrammeSite o2) {
		if(o1==null && o2==null){
			return 0;
		}
		if(o1==null){
			return 1;
		}
		if(o1.getSourceSite()==ProgrammeSiteType.优酷网.getValue() && o1.getCompleted()>0){ 
			return -1;
		}
		if(o2==null){
			return -1;
		}
		if(o2.getSourceSite()==ProgrammeSiteType.优酷网.getValue() && o2.getCompleted()>0){
			return 1;
		}
		if(o2.getSourceSite()==ProgrammeSiteType.优酷网.getValue() && (o2.getEpisodeCollected()==o1.getEpisodeCollected())){
			return 1;
		}
		return o2.getEpisodeCollected()-o1.getEpisodeCollected();
	}

	/** 优酷和综合主要优先的排序*/
	/*@Override 
	public int compare(ProgrammeSite o1, ProgrammeSite o2) {
		if(o1==null && o2==null){
			return 0;
		}
		if(o1==null){
			return -1;
		}
		if(o1.getSourceSite()==ProgrammeSiteType.优酷网.getValue()){ //如果站点是优酷，排第一
			if(o2!=null && o2.getSourceSite()==ProgrammeSiteType.综合.getValue()){
				if(o1.getEpisodeCollected()<o2.getEpisodeCollected()){
					return 1;
				}
			}
			return -1;
		}
		if(o2==null){
			return 1;
		}
		if(o2.getSourceSite()==ProgrammeSiteType.优酷网.getValue()){ //如果站点是优酷，排第一
			if(o1!=null && o1.getSourceSite()==ProgrammeSiteType.综合.getValue()){
				if(o2.getEpisodeCollected()<o1.getEpisodeCollected()){
					return -1;
				}
			}
			return 1;
		}
		return o1.getOrderId()-o2.getOrderId();
	}*/

}
