/**
 * 
 */
package com.youku.soku.web.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author 1verge
 *
 */

public class PageUtil {
	private static Log logger=LogFactory.getLog(PageUtil.class);
	public static String getContent(String url,int totalcount,int currentpage,int pagesize)
	{
		int totalpage = (int)(totalcount+(pagesize-1))/pagesize;
		if (totalpage == 0)totalpage=1;
		if (totalpage > 35)totalpage=35;
		
		if (currentpage > totalpage)
			currentpage= totalpage;
		
		int fixcount = 4;
		int start = currentpage-fixcount;
		int end = currentpage+fixcount+1;
		
		if (start < 1)
		{
			start = 1;
			end = start+2*fixcount+1;
		}
		
		if (end > totalpage)
			end = totalpage;
		
		if (totalpage >= 2*fixcount && end - start < 2*fixcount)
			start = end-2*fixcount+1;
		
		
		StringBuffer sb = new StringBuffer();
		
		if(currentpage >1)
			sb.append("<a href='"+ url+ (currentpage-1) +"'> 上一页 </a>");
		
		for (int i=start;i<=end;i++)
		{
			if (i == currentpage)
				sb.append("<span class=\"current\">"+ i +"</span>\n");
			else
				sb.append("<a href='"+ url + i +"'>" + i + "</a> \n");
		}
		
		if (totalpage > currentpage)
			sb.append("<a href='"+ url + (currentpage+1) +"'> 下一页</a>");
		
		return sb.toString();
	}
	
	
	public static String getNewContent(String url,int totalcount,int currentpage,int pagesize)
	{
		int totalpage = (int)(totalcount+(pagesize-1))/pagesize;
		if (totalpage == 0)totalpage=1;
		if (totalpage > 35)totalpage=35;
		
		if (currentpage > totalpage)
			currentpage= totalpage;
		
		int fixcount = 4;
		int start = currentpage-fixcount;
		int end = currentpage+fixcount+1;
		
		if (start < 1)
		{
			start = 1;
			end = start+2*fixcount+1;
		}
		
		if (end > totalpage)
			end = totalpage;
		
		if (totalpage >= 2*fixcount && end - start < 2*fixcount)
			start = end-2*fixcount+1;
		
		
		StringBuffer sb = new StringBuffer();
		sb.append("<ul class='pages'>");
		
		if(currentpage >1)
			sb.append("<li class='prev'><a href='"+ url + (currentpage-1)  +"'> 上一页 </a></li>");
		
		for (int i=start;i<=end;i++)
		{
			if (i == currentpage)
				sb.append("<li class='current'><span>"+ i +"</span></li>");
			else
				sb.append("<li><a href='"+ url + i +"'>" + i + "</a></li>");
		}
		
		if (totalpage > currentpage)
			sb.append("<li class='next'><a href='"+ url + (currentpage+1)  +"'> 下一页 </a></li>");
		
		sb.append("</ul>");
		
		return sb.toString();
	}
	
	/**
	 * 新soku 目录分页
	 * @param url
	 * @param totalcount
	 * @param currentpage
	 * @param pagesize
	 * @return
	 */
	public static String getNewContent(String prefixUrl,String subfixUrl,int totalcount,int currentpage,int pagesize)
	{
		int totalpage = (int)(totalcount+(pagesize-1))/pagesize;
		if (totalpage == 0)totalpage=1;
		if (totalpage > 100)totalpage=100;
		
		if (currentpage > totalpage)
			currentpage= totalpage;
		
		int fixcount = 4;
		int start = currentpage-fixcount;
		int end = currentpage+fixcount+1;
		
		if (start < 1)
		{
			start = 1;
			end = start+2*fixcount+1;
		}
		
		if (end > totalpage)
			end = totalpage;
		
		if (totalpage >= 2*fixcount && end - start < 2*fixcount)
			start = end-2*fixcount+1;
		
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("<ul class='pages'>");
		
		if(currentpage >1)
			sb.append("<li class='prev'><a href='"+ prefixUrl + (currentpage-1)+subfixUrl  +"'> 上一页 </a></li>");
		
		for (int i=start;i<=end;i++)
		{
			if (i == currentpage)
				sb.append("<li class='current'><span>"+ i +"</span></li>");
			else
				sb.append("<li><a href='"+ prefixUrl+i+subfixUrl +"'>" + i + "</a></li>");
		}
		
		if (totalpage > currentpage)
			sb.append("<li class='next'><a href='"+ prefixUrl + (currentpage+1)+subfixUrl  +"'> 下一页</a></li>");
		
		sb.append("</ul>");
		return sb.toString();
	}
	
	
	
	
	
	
	
	public static String getContentForTop(String url,int totalcount,int currentpage,int pagesize)
	{
		int totalpage = (int)(totalcount+(pagesize-1))/pagesize;
		if (totalpage == 0)totalpage=1;
		if (totalpage > 35)totalpage=35;
		
		if (currentpage > totalpage)
			currentpage= totalpage;
		
		int fixcount = 4;
		int start = currentpage-fixcount;
		int end = currentpage+fixcount+1;
		
		if (start < 1)
		{
			start = 1;
			end = start+2*fixcount+1;
		}
		
		if (end > totalpage)
			end = totalpage;
		
		if (totalpage >= 2*fixcount && end - start < 2*fixcount)
			start = end-2*fixcount+1;
		
		
		StringBuffer sb = new StringBuffer();
		
		if(currentpage >1)
			
//			sb.append("<a href='"+ url+ (currentpage-1) +"'> &lt;上一页 </a>");
			sb.append("<a href='"+ getPageUrl(url,currentpage-1)+"'> 上一页 </a>");
		
		for (int i=start;i<=end;i++)
		{
			if (i == currentpage)
				sb.append("<span class=\"current\">"+ i +"</span>\n");
			else
//				sb.append("<a href='"+ url + i +"'>" + i + "</a> \n");
				sb.append("<a href='"+ getPageUrl(url,i)+"'>" + i + "</a> \n");
		}
		
		if (totalpage > currentpage)
			sb.append("<a href='"+ getPageUrl(url,currentpage+1)+"'> 下一页 </a>");
		
		return sb.toString();
	}
	public static String getPageUrl(String url,int pageNo){
		try{
			String[] strArr=url.split("_");
            strArr[2]=new Integer(pageNo).toString();
            StringBuffer returnSB=new StringBuffer();
            for(int i=0;i<strArr.length-1;i++){
            	returnSB.append(strArr[i]+"_");
            }
            returnSB.append(strArr[strArr.length-1]);
            return returnSB.toString();
		}catch(Exception e){
			logger.error("转换top page url失败。使用原url:"+url+"  pageNo:"+pageNo);
		}
		return url;
		
	}
	
	public static void main(String[] args){
		String url="/channel/1_2_3_4/movie.html";
//		Pattern patt=null;
//		Matcher matcher=null;
//		patt=Pattern.compile("^/channel/([^_]*)_([^_]*)_([^_]*)_([^_]*)");
//		matcher=patt.matcher(url);
//		if(matcher.find()){
//			System.out.println("the index:"+matcher.start());
//			System.out.println("the string 3:"+matcher.group(3));
//			System.out.println("the string 3:"+matcher.);
//			
//		}
		System.out.println("the input url:"+url);
		System.out.println(getPageUrl(url,2000));
		
		
		System.out.println("over");
		
	}
	
}
