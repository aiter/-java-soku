/**
 * 
 */
package com.youku.soku.index.manager.db;

/**
 * @author 1verge
 *
 */
public class VideoManagerFactory {
	protected static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");
	public static IVideoManager getVideoManager(int site)
	{
//		if (site == YoukuVideoManager.site_id)//优酷网
//			return YoukuVideoManager.getInstance();
//		else
			return VideoManager.getInstance();
	}
}
