/**
 * 
 */
package com.youku.soku.index.manager.db;

import java.util.Date;
import java.util.List;

import com.youku.soku.index.om.Site;

/**
 * @author 1verge
 *
 */
public interface IVideoManager {
	public List<AssembleDoc> getVideos(int start,int limit,Site site);
	
	public List<AssembleDoc> getVideos(Date startTime,Date endTime,int limit,Site site);
	
	public List<AssembleDoc> getVideos(int start,int limit,Date endTime,Site site);
	
	public List<AssembleDoc> getVideosDesc(int end,int limit,Site site);
	
	public int getTotalVideoCount();
	
	
	public AssembleDoc getVideoAsDocument(int id,Site site);
	
}
