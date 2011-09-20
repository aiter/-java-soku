package com.youku.search.sort.servlet.search_page.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.youku.search.sort.json.util.JSONUtil;
import com.youku.search.sort.json.util.JSONUtil.ParseResult;
import com.youku.search.util.StringUtil;

@Controller
public class AjaxController extends BaseController {

	@RequestMapping(value = "/QSearch/~ajax/getSeriesOdshow")
	public void getSeriesOdshow(HttpServletRequest request,
			HttpServletResponse response) {

		try {
			response.setContentType("text; charset=UTF-8");

			JSONObject result = getSeriesOdshow_(request, response);

			if (result == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("result: null Object");
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("result:\n" + result.toString(4));
				}
			}

			String resultString = process(result);

			if (logger.isDebugEnabled()) {
				logger.debug("result:\n" + resultString);
			}

			byte bytes[] = resultString.getBytes("UTF-8");
			response.setBufferSize(bytes.length);

			response.getOutputStream().write(bytes);
			response.flushBuffer();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			response.resetBuffer();
		}
	}

	JSONObject getSeriesOdshow_(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String value = request.getParameter("__ap");
		ParseResult parseResult = JSONUtil.tryParse(value);
		if (!parseResult.valid) {
			return null;
		}

		JSONObject params = parseResult.object;
		String showid = params.optString("showid");
		int lastseq = params.optInt("lastseq");
		lastseq = Math.max(lastseq, 0);

		if (showid.isEmpty()) {
			return null;
		}

		JSONObject result = getSeriesShowVideo(showid, lastseq);
		return result;
	}

	String process(JSONObject jsonObject) {
		boolean deepEncode = true;

		if (jsonObject == null) {
			return "";
		}

		String objectString = jsonObject.toString();
		if (deepEncode) {
			StringBuilder builder = new StringBuilder();

			char[] chars = objectString.toCharArray();
			for (int i = 0; i < chars.length; i++) {
				char c = chars[i];
				if (chars[i] < 256) {
					builder.append(c);
				} else {
					String value = "0000" + Integer.toHexString(c);
					value = "\\u" + value.substring(value.length() - 4);
					builder.append(value);
				}
			}

			objectString = builder.toString();
			objectString = JSONObject.quote(objectString);
		}

		String result = objectString;
		return result;
	}

	@RequestMapping(value = { "/QSearch/~ajax/getPersonOdshow",
			"/search/getPersonOdshow" })
	public String getPersonOdshow(HttpServletRequest request, ModelMap model) {

		String view = "/youku/ajaxPersonShow.jsp";

		try {
			getPersonOdshow_(request, model);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return view;
	}

	void getPersonOdshow_(HttpServletRequest request, ModelMap model)
			throws Exception {

		String personname = request.getParameter("personname");
		personname = StringUtil.filterNull(personname).trim();

		String categorytab_param = request.getParameter("categorytab");
		int categorytab = StringUtil.parseInt(categorytab_param, 0);
		String category = "";

		if (personname.isEmpty()) {
			return;
		}

		switch (categorytab) {
		case 1:
			category = "电影";
			break;
		case 2:
			category = "电视剧";
			break;
		case 3:
			category = "综艺";
			break;
		case 4:
			category = "音乐";
			break;
		case 5:
			category = "体育";
			break;
		case 9:
			category = "预告片";
			break;
		default:
			category = "";
		}

		if (category.isEmpty()) {
			return;
		}

		String query = null;
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", "44");

		String fd_value = "showname show_thumburl completed episode_last streamtypes releaseyear releasemonth hasvideotype paid";
		params.put("fd", fd_value);

		params.put("ob", "releasedate:desc");
		params.put("cl", "search_result");

		if (categorytab == 9) {
			query = "person:"
					+ personname
					+ " showcategory:电影,电视剧,综艺,体育 state:normal hasvideotype:预告片 -hasvideotype:正片";
		} else {
			query = "person:" + personname + " showcategory:" + category
					+ " allowfilter:1 state:normal";
		}

		JSONObject result = listOdShow(query, params);
		JSONArray results = JSONUtil.getProperty(result, JSONArray.class,
				"results");

		model.addAttribute("pageResult", results);
		model.addAttribute("categorytab", categorytab);
	}
}
