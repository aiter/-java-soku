package com.youku.soku.sort;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.sort.log.RemoteLogger;
import com.youku.search.sort.search.LogInfo;
import com.youku.search.sort.search.LogInfo.Item;
import com.youku.search.util.Constant;

/**
 * 记录soku搜索请求的log信息
 * 
 * 
 * 备注： 站内搜索页面最下面会嵌入的站外搜索结果；这部分内容前端会缓存，为了统计被缓存的请求，增加这个log记录请求接口
 * @deprecated  合并到SearcherServlet 参数加入_only_log=ture,替换本接口
 */
public class SearcherLogServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	Log logger = LogFactory.getLog(getClass());

	static final String RESPONSE_OK = "ok";
	static final String RESPONSE_ERROR = "error";

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		handleRequest(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		handleRequest(request, response);
	}

	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		Parameter p = null;

		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=utf-8");

		try {
			p = new Parameter(request);
			if (p.keyword.length() > 0) {
				LogInfo logInfo = buildLogInfo(p);
				RemoteLogger.log(RemoteLogger.sokuQuery, logInfo);

				response.getWriter().print(RESPONSE_OK);
				response.getWriter().flush();
				return;
			}
		} catch (Exception e) {
			logger.error("记录请求日志发生异常！query： " + p, e);
		}

		response.getWriter().flush();
		response.getWriter().print(RESPONSE_ERROR);
	}

	LogInfo buildLogInfo(Parameter p) {

		LogInfo info = new LogInfo();

		info.set(Item.query, p.keyword);
		info.set(Item.source, p._source);
		info.set(Item.type, "video");

		if (p.logic == Constant.Operator.AND) {
			info.set(Item.logic, "and");
		} else {
			info.set(Item.logic, "or");
		}

		info.set(Item.order_field, null);
		info.set(Item.order_reverse, p.reverse);
		info.set(Item.page, p.page);
		info.set(Item.cache, true);
		info.set(Item.total_result, 1);
		info.set(Item.page_result, 1);
		info.set(Item.cost, 0);
		info.set(Item.miss, false);
		info.set(Item.cacheKey, "unknow_since_is_log_request");
		info.set(Item.url, p.queryUrl);
		info.set(Item.because, p._because);
		info.set(Item.others, "this_is_a_log_request");

		return info;
	}

}
