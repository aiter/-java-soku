/**
 * 
 */
package com.youku.soku.web.controller;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import com.youku.search.util.DataFormat;
import com.youku.soku.config.Config;
import com.youku.soku.web.ParameterS;

/**
 * @author 1verge
 *
 */
public class Sender {
	
	private static Random random = new Random(); 
	
	public static final String SEARCHURL = "/search";	//请求sorturl地址
	public static final String EXTSEARCHURL = "/extsearch";	//请求sorturl地址
	public static final String AUTOCOMPLETEURL = "/search_keys?type=video&k=";	//输入框自动提示功能请求地址
	
	public static String getSearchUrl(ParameterS para)
	{
		//随机取一台服务器
		String[] servers = Config.getControlServers();
		int index = random.nextInt(servers.length);
		
		StringBuilder sb = new StringBuilder("http://");
		sb.append(servers[index]);
		if (para.getVersion_id() >0 || para.getSite_version_id()>0 || para.getName_id()>0)
			sb.append( Sender.EXTSEARCHURL).append(para.getExtSearchString());
		else
			sb.append( Sender.SEARCHURL).append(para.getSearchString());
		
		return sb.toString();
	}
	
	
	public static String getSearchPlayUrl(HttpServletRequest request){
		
		int cate = DataFormat.parseInt(request.getParameter("ca"));
		int episode_id = DataFormat.parseInt(request.getParameter("ep"));
		boolean change_site = request.getParameter("cg")!=null;
		int episode_start = DataFormat.parseInt(request.getParameter("st"));
		int episode_limit = DataFormat.parseInt(request.getParameter("lm"));
		
		
		String[] servers = Config.getControlServers();
		int index = random.nextInt(servers.length);
		
		StringBuilder builder = new StringBuilder( "http://");
		builder.append(servers[index]);
		builder.append(Sender.EXTSEARCHURL);
		builder.append("?cate_id=");
		builder.append(cate);
		builder.append("&episode_id=");
		builder.append(episode_id);
		if(change_site)
			builder.append("&change_site");
		
		builder.append("&episode_start=");
		builder.append(episode_start);
		builder.append("&episode_limit=");
		builder.append(episode_limit);
		
		return builder.toString();
	}
	
	public static String getAutoCompleteUrl(String keyword)
	{
		//随机取一台服务器
		String[] servers = Config.getAutoCompleteServers();
		int index = random.nextInt(servers.length);
		
		StringBuilder sb = new StringBuilder("http://");
			return sb.append(servers[index])
				.append( Sender.AUTOCOMPLETEURL)
				.append(keyword)
				.toString();
	}
}
