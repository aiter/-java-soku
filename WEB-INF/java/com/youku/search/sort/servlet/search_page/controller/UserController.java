package com.youku.search.sort.servlet.search_page.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.youku.search.sort.Parameter;
import com.youku.search.sort.ParameterNames;
import com.youku.search.sort.json.util.JSONUtil;
import com.youku.search.sort.servlet.search_page.QueryStringParser;
import com.youku.search.sort.servlet.search_page.SearchHelper;
import com.youku.search.sort.servlet.search_page.WebParam;
import com.youku.search.sort.servlet.search_page.WebParamHelper;
import com.youku.search.sort.servlet.util.WebUtils;
import com.youku.search.util.StringUtil;

@Controller
public class UserController extends BaseController {

	@RequestMapping(value = { "/search_user", "/search_user/**/*" })
	public String user(HttpServletRequest request, ModelMap model)
			throws Exception {

		Map<String, String> web = QueryStringParser.parse(request,
				"/search_user/");
		return handleUser(request,model,web);
	}
	
	@RequestMapping(value = { "/search/user", "/search/user/**/*" })
	public String userOld(HttpServletRequest request, ModelMap model)
			throws Exception {
		Map<String, String> web = QueryStringParser.parse(request,
				"/search/user/");
		return handleUser(request,model,web);
	}
	
	private String handleUser(HttpServletRequest request, ModelMap model,Map<String, String> web)
		throws Exception {
		if (!web.containsKey("pagesize")
				|| StringUtil.parseInt(web.get("pagesize"), 0, 0) == 0) {
			web.put("pagesize", "12");
		}

		WebParam param = WebParamHelper.parse(web);

		String resultView = "/youku/userResult.jsp";

		String query_url = Parameter.buildQueryUrl(request);

		String view = handle_user(query_url, param, model, resultView,
				resultView);
		return view;
	}

	private String handle_user(String query_url, WebParam param,
			ModelMap model, String resultView, String nullResultView)
			throws Exception {

		// 
		Map<ParameterNames, String> map = WebParamHelper.convertUser(param);
		map.put(ParameterNames.query_url, query_url);

		Parameter p = new Parameter(map);

		if (logger.isDebugEnabled()) {
			StringBuilder builder = new StringBuilder();
			builder.append("\n" + "后端参数map: " + map);
			logger.debug(builder.toString());
		}

		//
		model.put("searchType", "user");
		model.put("searchCaption", "空间");
		model.put("searchExtend", "bar");

		model.put("webParam", param);

		// 
		if (logger.isDebugEnabled()) {
			logger.debug("开始查询数据...");
		}

		JSONObject result = SearchHelper.search(p);
		model.put("result", result);

		// 屏蔽
		if (result.optInt("total") == -1) {
			String showMsg = "搜索结果可能涉及不符合相关法律法规和政策的内容，未予显示。";
			model.put("showMsg", showMsg);

			nullResultPage(param, result);

			if (logger.isDebugEnabled()) {
				StringBuilder builder = new StringBuilder();
				builder.append("显示空结果页面: " + nullResultView);
				builder.append(", showMsg: " + showMsg);
				builder.append(", result:\n" + result.toString(4));

				logger.debug(builder.toString());
			}

			return nullResultView;
		}

		if (JSONUtil.isEmpty(result.optJSONObject("items"))) {
			String showMsg = "";

			showMsg = "抱歉，没有找到与 <span class=\"key\">"
					+ WebUtils.htmlEscape(param.getQ()) + "</span> 相关的空间。";

			model.put("showMsg", showMsg);
			nullResultPage(param, result);

			if (logger.isDebugEnabled()) {
				StringBuilder builder = new StringBuilder();
				builder.append("显示空结果页面: " + nullResultView);
				builder.append(", result:\n" + result.toString(4));

				logger.debug(builder.toString());
			}
			return nullResultView;

		}

		//
		if (logger.isDebugEnabled()) {
			logger.debug("查询bar信息...");
		}
		model.put("bar", get_bar(p.query));

		if (logger.isDebugEnabled()) {
			StringBuilder builder = new StringBuilder();
			builder.append("查询完毕，显示页面: " + resultView);
			builder.append(", result:\n" + result.toString(4));

			logger.debug(builder.toString());
		}
		return resultView;
	}

	public static void main(String[] args) {
		String s = "𐤇";
		char[] chars = s.toCharArray();

		System.out.println(new String(chars));
		System.out.println("chars.length: " + chars.length);
		System.out.println(Character.codePointCount(chars, 0, chars.length));
		System.out.println("is high: " + Character.isHighSurrogate(chars[0]));
		System.out.println("is low: " + Character.isLowSurrogate(chars[1]));

		System.out.println(Character.toCodePoint(chars[0], chars[1]));

		char[] chars_new = new char[2];
		chars_new[0] = chars[1];
		chars_new[1] = chars[0];
		System.out.println(new String(chars_new));
		System.out.println(Character.codePointCount(chars_new, 0,
				chars_new.length));
	}
}
