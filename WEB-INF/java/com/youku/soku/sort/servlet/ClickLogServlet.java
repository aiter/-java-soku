package com.youku.soku.sort.servlet;

import static com.youku.search.util.StringUtil.filterNull;
import static java.lang.String.valueOf;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.log_servlet.ClickLogInfo;
import com.youku.search.log_servlet.ClickLogInfo.Item;
import com.youku.search.sort.log.RemoteLogger;

/**
 * 视频点击log
 */
public class ClickLogServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	Log logger = LogFactory.getLog(getClass());

	static final String RESPONSE_OK = "ok";
	static final String RESPONSE_ERROR = "error";

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

			String responseString = RESPONSE_ERROR;

			try {
				responseString = handleRequest(request);
			} catch (Exception e) {
				logger.error(
						"处理请求发生异常，queryString：" + request.getQueryString(), e);
			}

			response.getWriter().print(responseString);
			response.getWriter().flush();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	String handleRequest(HttpServletRequest request) {

		String url = filterNull(request.getParameter("url")).trim();
		String source = valueOf(request.getParameter("source"));
		String keyword = filterNull(request.getParameter("keyword")).trim();
		String because = filterNull(request.getParameter("because")).trim();
		String category = filterNull(request.getParameter("cate")).trim();
		String wait = filterNull(request.getParameter("wait")).trim();
		String programmeId = filterNull(request.getParameter("programmeid"));
		String siteId = filterNull(request.getParameter("siteid")).trim();
		
		if (url.length() == 0) {
			return RESPONSE_ERROR;
		}

		ClickLogInfo logInfo = new ClickLogInfo();
		logInfo.set(Item.url, url);
		logInfo.set(Item.source, source);
		logInfo.set(Item.keyword, keyword);
		logInfo.set(Item.because, because);
		logInfo.set(Item.category, category);
		logInfo.set(Item.wait, wait);
		logInfo.set(Item.programmeid, programmeId);
		logInfo.set(Item.siteid, siteId);
		
		// log
		if (source.equals("soku"))
			RemoteLogger.log(RemoteLogger.sokuClick, logInfo.getLogString());
		else
			RemoteLogger.log(RemoteLogger.youkuClick, logInfo.getLogString());
		return RESPONSE_OK;
	}
}