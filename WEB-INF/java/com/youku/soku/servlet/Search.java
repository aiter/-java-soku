/**
 * 
 */
package com.youku.soku.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;

import com.youku.soku.web.SearchResult;
import com.youku.soku.web.SearchService;

/**
 * @author 1verge
 *
 */
public class Search extends HttpServlet {
	
	private static final long serialVersionUID = -3170250354513211973L;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
	{
		logger.debug("Begin search: " + request.getParameter("keyword"));
		SearchResult searchResult = SearchService.search(request);
		logger.debug("search result: " + searchResult.toString());
		if (request.getParameter("print") !=null){
			response.setContentType("text/html; charset=utf-8");
				try {
					response.getWriter().print(searchResult.getJson().toString(4));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			return;
		}
		
		String tmpUrl = searchResult.getForward().getForward();
		if(tmpUrl.indexOf("redirect")==0){//直接跳转
			response.sendRedirect(tmpUrl.substring("redirect".length()));
			return;
		}
		request.setAttribute("content",searchResult);
		/* 获取上下文环境 */
		ServletContext sc = getServletContext();

		/* 设置返回地址 */
		RequestDispatcher rd=null;
		rd = sc.getRequestDispatcher(searchResult.getForward().getForward());
		
		/* forward到结果页面 */
		rd.forward(request, response);
		
		
		return;
		
	}
	public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
	{
		doGet( request, response);
	}
}
