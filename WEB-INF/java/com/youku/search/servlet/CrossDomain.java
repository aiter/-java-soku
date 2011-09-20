package com.youku.search.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public class CrossDomain extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5298157587569450097L;
	public CrossDomain() {
		// TODO Auto-generated constructor stub
	}

	  public void init() throws ServletException
	  {
		  
	  }
	  protected void service(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException, IOException
	  {
		  /**
		   * 
 

ETag: "1999477140"







HTTP/1.1 200 OK
Server: adserver
Pragma: No-cache
Cache-Control: no-cache
Expires: 0
Content-Type: text/html; charset=utf-8
Connection: close
Transfer-Encoding: chunked
Date: Thu, 20 Mar 2008 03:44:25 GMT
		   */
			res.setHeader("Expires","Sat, 05 Mar 2011 03:44:25 GMT");
			res.setHeader("Cache-Control", "max-age=93312000");
			res.setHeader("Last-Modified","Wed, 19 Mar 2008 02:08:13 GMT");
			res.setHeader("ETag","\"d2692-79-4423b9324aa80\"");
			res.setContentType("text/xml;charset=UTF-8"); 
			res.getWriter().write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><cross-domain-policy><allow-access-from domain=\"*\"/></cross-domain-policy>" );
			res.getWriter().flush();
			return;
	  }
}
