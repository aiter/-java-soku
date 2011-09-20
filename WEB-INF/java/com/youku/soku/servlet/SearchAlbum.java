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

import org.json.JSONObject;

import com.youku.search.sort.KeywordFilter;
import com.youku.soku.sort.Parameter;
import com.youku.soku.sort.album.AlbumSearch;

/**
 * @author 1verge
 *
 */
public class SearchAlbum extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1717252361703325420L;

	public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
	{
		JSONObject content = null;
		//检查关键字
		String keyword = request.getParameter("keyword");
		keyword = KeywordFilter.filter(keyword, 100);
		Parameter param = new Parameter(request);
		try {
			content = AlbumSearch.search(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		request.setAttribute("content",content);
		request.setAttribute("param",param);
	
		/* 获取上下文环境 */
		ServletContext sc = getServletContext();

		/* 设置返回地址 */
		RequestDispatcher rd=null;
		rd = sc.getRequestDispatcher("/result/album.jsp");

		/* forward到结果页面 */
		rd.forward(request, response);
		return;
		
	}
	public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
	{
		doGet( request, response);
	}
}
