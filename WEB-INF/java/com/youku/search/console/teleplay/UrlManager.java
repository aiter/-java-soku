/**
 * 
 */
package com.youku.search.console.teleplay;

import java.io.UnsupportedEncodingException;

import com.youku.search.config.Config;

/**
 * @author william
 * 各接口管理类
 */
public class UrlManager {
	
	/**
	 * 如果limit = 0，则按500算
	 * @param words
	 * @param limit
	 * @return
	 */
	public static String getQueryTeleplayUrl(String words,String sort,int limit,int date)
	{
		String addr = Config.getYoukuHost();
//		System.out.println("http://10.101.8.66/search?type=19&keyword="+words+"&curpage=1&partner="+date+"&pagesize="+(limit>0?limit:TeleplayQuery.max_videos));
		try {
			words = java.net.URLEncoder.encode(words,"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			return null;
		}
		String url = "http://"+addr+"/search?keyword="+words+"&curpage=1&partner="+date+"&pagesize="+(limit>0?limit:TeleplayQuery.max_videos+"&advance=1&fields=title");
		
		
		if (sort != null) url += "&orderfield="+sort;
		return url;
	}
	
	public static String getQueryVarietyUrl(String words,String sort,int limit,int date)
	{
//		StringBuffer sb = new StringBuffer();
//		if (exclude != null )
//		{
//			for (String s:exclude)
//			{
//				sb.append(s).append(" ");
//			}
//		}
		String addr = Config.getYoukuHost();
//		System.out.println("{+("+words+")"+ (exclude!=null?" -("+ sb.toString() +")":"")+"}");
		try {
//			words = java.net.URLEncoder.encode("{+("+words+")"+ (exclude!=null?" -("+ sb.toString() +")":"")+"}","UTF-8");
			words = java.net.URLEncoder.encode(words,"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			return null;
		}
		String url = "http://"+addr+"/search?type=1&keyword="+words+"&curpage=1&order=1&fields=title&cateid=0&relnum=15&advance=1&pagesize="+(limit>0?limit:VarietyQuery.max_videos);
		
		if (sort != null) url += "&orderfield="+sort;
		return url;
	}
	
	public static String getQueryVersionUrl(String words)
	{
		String addr = Config.getYoukuHost();
//		System.out.println("http://10.101.8.66/search?type=1&keyword="+words+"&curpage=1&pagesize=0&order=1&orderfield=&fields=title&advance=1");
		try {
			words = java.net.URLEncoder.encode(words,"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			return null;
		}
		String url = "http://"+addr+"/search?type=1&keyword="+words+"&curpage=1&pagesize=0&order=1&orderfield=&fields=title&advance=1";
//		System.out.println(url);
		return url;
	}
	
	public static String getVideoUrl(int vid)
	{
		return "http://"+Config.getYoukuHost()+"/search?keyword=vid:"+ vid +"&na=0";
	}
	
}
