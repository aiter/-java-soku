/**
 * 
 */
package com.youku.soku.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.youku.search.util.DataFormat;
import com.youku.soku.manage.service.SokuFeedbackService;

/**
 * @author 1verge
 * 
 */
public class Feedback extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4633368295062824002L;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String message = request.getParameter("message");
		String url = request.getParameter("url");
		String keyword = request.getParameter("keyword");
		int state = DataFormat.parseInt(request.getParameter("state"));
		int source = DataFormat.parseInt(request.getParameter("source"));

		if (StringUtils.isBlank(keyword))
			keyword = "";
		if (StringUtils.isBlank(message))
			message = "";
		String host = getRemoteAddr(request);

		SokuFeedbackService.saveFeedback(keyword, state, url, message, source,
				host);
		response.getWriter().write("{\"state\":\"success\",\"msg\":\"保存成功\"}");
		return;
	}

	private String getRemoteAddr(HttpServletRequest request) {
		String host = request.getHeader("x-forwarded-for");
		if (!StringUtils.isBlank(host)) {
			String[] ips = host.split(",");
			if (null != ips) {
				for (String ip : ips) {
					if (!StringUtils.isBlank(ip)
							&& "unknown".equalsIgnoreCase(ip)) {
						host = ip;
						break;
					}
				}
			}
		}
		if (StringUtils.isBlank(host) || "unknown".equalsIgnoreCase(host)) {
			host = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtils.isBlank(host) || "unknown".equalsIgnoreCase(host)) {
			host = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtils.isBlank(host) || "unknown".equalsIgnoreCase(host)) {
			host = request.getRemoteAddr();
		}
		return host;
	}

}
