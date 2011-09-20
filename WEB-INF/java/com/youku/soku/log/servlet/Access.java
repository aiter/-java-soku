/**
 * 
 */
package com.youku.soku.log.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.youku.soku.log.AccessLog;
import com.youku.soku.log.RemoteLogger;
import com.youku.soku.util.CookieManager;

/**
 * @author 1verge
 *
 */
public class Access extends HttpServlet {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6514533306945808282L;
	public void doGet(HttpServletRequest request,HttpServletResponse response)
	{
		
		String pageurl = request.getParameter("pageurl");
		String referer = request.getParameter("referer");
		
		String ip = request.getRemoteAddr();
		
		//创建cookie
		CookieManager cookieManager = new CookieManager(request,response);
		String cookie = cookieManager.getUniqCookie();
		
		AccessLog log = new AccessLog();
		log.setTime(System.currentTimeMillis());
		log.setIp(ip);
		log.setCookie(cookie);
		log.setReferer(referer);
		log.setPageurl(pageurl);
		
		System.out.println(log.getContent());
		RemoteLogger.sokuAccessLog.log(log.getContent());
		return;
		
	}
}
