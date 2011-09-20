package com.youku.search.log_servlet;

import static com.youku.search.util.StringUtil.filterNull;
import static java.lang.String.valueOf;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.log_servlet.ClickLogInfo.Item;
import com.youku.search.sort.log.RemoteLogger;

/**
 * 站内、站外点击log
 */

// log_type=youku_click&url=jiabaozhen&source=youku&type=video&keyword=jia&because=1
// log_type=soku_click&url=jiabaozhen&source=youku&type=video&keyword=jia&because=1

public class ClickLogServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	Log logger = LogFactory.getLog(getClass());

	static final String RESPONSE_OK = "ok";
	static final String RESPONSE_ERROR = "error";

	public static enum ClickLogType {
		youku_click, // 这个日志信息是点击youku视频的
		soku_click, // 这个日志信息是点击soku视频的
	}

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

		ClickLogType clickLogType = ClickLogType.valueOf(request
				.getParameter("log_type"));

		String url = filterNull(request.getParameter("url")).trim();
		String source = valueOf(request.getParameter("source"));
		String type = filterNull(request.getParameter("type"));
		String keyword = filterNull(request.getParameter("keyword")).trim();
		String because = filterNull(request.getParameter("because")).trim();

		if (url.length() == 0) {
			return RESPONSE_ERROR;
		}

		ClickLogInfo logInfo = new ClickLogInfo();
		logInfo.set(Item.url, url);
		logInfo.set(Item.source, source);
		logInfo.set(Item.type, type);
		logInfo.set(Item.keyword, keyword);
		logInfo.set(Item.because, because);

		// log
		switch (clickLogType) {
		case youku_click:
			RemoteLogger.log(RemoteLogger.youkuClick, logInfo.getLogString());
			break;

		case soku_click:
			RemoteLogger.log(RemoteLogger.sokuClick, logInfo.getLogString());
			break;

		default:
			throw new RuntimeException("未知的类型：" + clickLogType);
		}

		return RESPONSE_OK;
	}
}