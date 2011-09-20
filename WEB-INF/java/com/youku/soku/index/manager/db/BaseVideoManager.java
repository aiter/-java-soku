/**
 * 
 */
package com.youku.soku.index.manager.db;

/**
 * @author 1verge
 *
 */
public abstract class BaseVideoManager implements IVideoManager{
	/**
	 * 去掉域名
	 * @param url
	 * @return
	 */
	protected String getUrlNoDomain(String url)
	{
		return url.substring(url.indexOf("/",8));
	}
	
}
