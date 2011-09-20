package com.youku.soku.newext.searcher;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.soku.sort.Parameter;


/**
 * 搜索处理入口点
 */
public class SearcherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	Log logger = LogFactory.getLog(getClass());

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		handleRequest(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		handleRequest(request, response);
	}

	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) {

		try {
			request.setCharacterEncoding("UTF-8");
			response.setContentType("text/html; charset=utf-8");

			String responseString = "";
			Parameter parameter = new Parameter(request);
			parameter.keyword=StringUtils.trimToEmpty(parameter.keyword);

			
			
			//在直达区中不处理屏蔽词
			boolean success  = true;
			
			if(success){
				try {
					String result = Searcher.search(parameter);
					responseString = result;
					
					}catch (Exception e) {
					logger.error("查询发生异常！query： " + parameter, e);
				}
			}else {
				//TODO 如果需要，可以返回提示屏蔽词信息
			}

			response.getWriter().print(responseString);
			response.getWriter().flush();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	
}
