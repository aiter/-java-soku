/**
 * 
 */
package com.youku.soku.web;

import java.util.Random;

import com.youku.soku.config.Config;

/**
 * @author 1verge
 * 控制根据返回结果跳转去向
 */
public class Controller {
	
	private static Random random = new Random(); 
	public static final String SEARCHURL = "/search";	//正常搜索结果
	
	public static final String NORMAL = "/result.jsp";	//正常搜索结果
	public static final String PERSON = "/result.jsp";	//人物页
	public static final String TV = "/result.jsp";		//电视剧页
	public static final String VARIETY = "/result.jsp";	//综艺节目
	public static final String CARTOON = "/result.jsp";	//动漫
	
	public static String getForward(SearchResult result)
	{
		if (result == null)
			return null;
		if (result.getContent().getMovie() != null)
		{
			
		}
		
		return null;
	}
	
	public static String getRequestUrl(ParameterS para)
	{
		//随机取一台服务器
		String[] servers = Config.getControlServers();
		int index = random.nextInt(servers.length);
		
		StringBuilder sb = new StringBuilder("http://");
		return sb.append(servers[index]).append( SEARCHURL).append( para.getSearchString()).toString();
	}
	
}
