package com.youku.soku.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.youku.search.sort.MemCache;

public class SokuIndexPic extends HttpServlet {
	

	public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
	{
		response.setContentType("text/html;charset=utf-8");
	    request.setCharacterEncoding("UTF-8");
		
		final PrintWriter writer = response.getWriter();
		String r = (String) MemCache.cacheGet("index_pic");
		writer.write(r);		
		writer.flush();
	}
	public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
	{
		doGet( request, response);
	}
}

