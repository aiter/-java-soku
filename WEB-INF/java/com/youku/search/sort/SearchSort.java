package com.youku.search.sort;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.search.sort.Search.StringSearchResultException;

/**
 * 搜索处理入口点
 */
public class SearchSort extends HttpServlet {

	Log logger = LogFactory.getLog(getClass());

	private static final long serialVersionUID = -1128190958095372803L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		handleRequest(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		handleRequest(request, response);
	}

	private void handleRequest(HttpServletRequest request,
			HttpServletResponse response) {

		try {
			request.setCharacterEncoding("UTF-8");
			response.setContentType("text/html; charset=utf-8");

			Parameter p = new Parameter(request);
			String responseString = null;

			try {
				responseString = handleRequest(p);
			} catch (Exception e) {
				logger.error(request.getQueryString() + ": " + e.getMessage(),
						e);
			}

			if (responseString == null) {
				responseString = "";
			}

			response.getWriter().print(responseString);
			response.getWriter().flush();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	private String handleRequest(Parameter p) throws Exception {

		//2011.4.26 关闭推荐功能
//		if (p.recmdStatus == 1) {
//			VideoRecommend recommend = new VideoRecommend();
//			recommend.cacheRecommends();
//
//			String response = recommend.getStatus();
//			return response;
//		}

		Search search = SearchInstanceMap.getSearch(p.type, p.isAdvanceSearch);

		try {
			JSONObject response = search.search(p);
			if (p.human) {
				return response.toString(4);
			}

			return response.toString();

		} catch (StringSearchResultException e) {
			return e.getResult();
		}
	}
}