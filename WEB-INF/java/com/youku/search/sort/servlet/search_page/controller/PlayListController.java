package com.youku.search.sort.servlet.search_page.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.youku.search.sort.KeywordFilter;
import com.youku.search.sort.Parameter;
import com.youku.search.sort.ParameterNames;
import com.youku.search.sort.entity.CategoryMap;
import com.youku.search.sort.entity.CategoryMap.Category;
import com.youku.search.sort.json.util.JSONUtil;
import com.youku.search.sort.servlet.search_page.QueryStringParser;
import com.youku.search.sort.servlet.search_page.SearchHelper;
import com.youku.search.sort.servlet.search_page.WebParam;
import com.youku.search.sort.servlet.search_page.WebParamHelper;
import com.youku.search.sort.servlet.search_page.util.NovaMiddleResourceUtil;
import com.youku.search.sort.servlet.util.WebUtils;
import com.youku.search.util.StringUtil;

@Controller
public class PlayListController extends BaseController {

	@RequestMapping(value = { "/search_playlist", "/search_playlist/**/*" })
	public String playlist(HttpServletRequest request,HttpServletResponse response, ModelMap model)
			throws Exception {

		Map<String, String> web = QueryStringParser.parse(request,
				"/search_playlist/");
		return handlePlaylist(request,response, model, web);
	}
	
	@RequestMapping(value = { "/search/playlist", "/search/playlist/**/*" })
	public String playlistOld(HttpServletRequest request,HttpServletResponse response, ModelMap model)
			throws Exception {

		Map<String, String> web = QueryStringParser.parse(request,
				"/search/playlist/");
		return handlePlaylist(request,response, model, web);
	}
	
	private String handlePlaylist(HttpServletRequest request,HttpServletResponse response, ModelMap model,Map<String, String> web)
	throws Exception {
		if (!web.containsKey("pagesize")
				|| StringUtil.parseInt(web.get("pagesize"), 0, 0) == 0) {
			web.put("pagesize", "12");
		}

		WebParam param = WebParamHelper.parse(web);
		
		String resultView = "/youku/playlistResult.jsp";

		String query_url = Parameter.buildQueryUrl(request);

		String view = handle(query_url, param, model, resultView,
				resultView,web);
		if (web.get("h") !=null){
			response.setContentType("text/html; charset=utf-8");
				try {
					JSONObject result  = (JSONObject)model.get("result");
					if(result!=null){
						response.getWriter().print(result.toString(4));
					}else {
						response.getWriter().print("{}");
					}
//					JSONArray cms              = (JSONArray)model.get("cms");
					JSONArray recommendShows   = (JSONArray)model.get("recommendShows");
//					if(cms!=null){
//						response.getWriter().print(cms.toString(4));
//					}
					if(recommendShows!=null){
						response.getWriter().print(recommendShows.toString(4));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			return null;
		}
		
		return view;
	}

	@RequestMapping(value = { "/search_advancedplaylist",
			"/search_advancedplaylist/**/*" })
	public String advancedplaylist(HttpServletRequest request, ModelMap model) {

		Map<String, String> web = QueryStringParser.parse(request,
				"/search_advancedplaylist/");
		return handleAdvancePlaylist(request, model, web);
	}
	
	@RequestMapping(value = { "/search/advancedplaylist",
		"/search/advancedplaylist/**/*" })
	public String advancedplaylistOld(HttpServletRequest request, ModelMap model) {
	
		Map<String, String> web = QueryStringParser.parse(request,
				"/search/advancedplaylist/");
		return handleAdvancePlaylist(request, model, web);
	}
	
	private String handleAdvancePlaylist(HttpServletRequest request, ModelMap model,Map<String, String> web){
		String keyword = web.get("q");
//		keyword = KeywordFilter.filter(keyword);

		String key1 = keyword;
		if (key1 != null) {
			try {
				key1 = key1.replaceAll("[{}'\"+-]", " ");
				key1 = key1.replaceAll("\\s{2,}", " ");
				key1 = key1.trim();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		WebParam param = new WebParam();
		param.setQ(keyword);

		model.put("searchType", "playlist");
		model.put("searchCaption", "专辑");
		model.put("searchExtend", "bar");

		model.put("webParam", param);

		model.put("keyword", keyword);
		model.put("key1", key1);

		return "/youku/advancedPlaylist.jsp";
	}

	private String handle(String query_url, WebParam param, ModelMap model,
			String resultView, String nullResultView,Map<String, String> webParameter) throws Exception {

		// 
		Map<ParameterNames, String> map = WebParamHelper.convertFolder(param);
		map.put(ParameterNames.query_url, query_url);

		Parameter p = new Parameter(map);

		CategoryMap categoryMap = CategoryMap.getInstance();
		List<Category> folderCategories = CategoryMap.getInstance().folderList;
		Category category = categoryMap.map.get(param.getCateid());

		if (logger.isDebugEnabled()) {
			StringBuilder builder = new StringBuilder();
			builder.append("\n" + "后端参数map: " + map);
			logger.debug(builder.toString());
		}

		//
		model.put("searchType", "playlist");
		model.put("searchCaption", "专辑");
		model.put("searchExtend", "bar");

		model.put("webParam", param);
		model.put("categories", folderCategories);
		model.put("category", category);
		
		model.put("webParameter", webParameter);

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

		// 节目
		if (param.getPage() == 1 && param.getCateid() == 0 && param.getOrderby()==1) {
			if (logger.isDebugEnabled()) {
				logger.debug("需要查询版权库数据...");
			}

			try {
//				commendOdshow(param.getQ(), category, result);
//				commendOdshow(p.query, category, result);
				commendOdshowNew(p.query, category, result);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		// 空结果
		if (JSONUtil.isEmpty(result.optJSONObject("items"))
				&& JSONUtil.isEmpty(result.optJSONObject("drama"))
				&& JSONUtil.isEmpty(result.optJSONObject("zongyi"))
				&& JSONUtil.isEmpty(result.optJSONObject("series_odshows"))
				&& JSONUtil.isEmpty(result.optJSONObject("person_odshows"))
				&& JSONUtil.isEmpty(result.optJSONObject("odshows"))) {

			if (logger.isDebugEnabled()) {
				logger.debug("本次查询返回空结果，没有找到索引数据、版权库数据、直达区数据！");
			}

			// 排行榜
			if (param.getCateid() > 0 && "top".equals(param.getSource())) {
				model.clear();

				String redirect_url = "redirect:/search_playlist/q_"
						+ WebUtils.urlEncode(param.getQ());

				if (logger.isDebugEnabled()) {
					logger.debug("请求来自排行榜，需要重定向请求到: " + redirect_url);
				}

				return redirect_url;
			}

			String cate = category == null ? null : category.name;

			String showMsg = "";
			if (cate == null || cate.isEmpty()) {
				showMsg = "抱歉，没有找到与 <span class=\"key\">"
						+ WebUtils.htmlEscape(param.getQ()) + "</span> 相关的专辑。";

			} else {
				showMsg = "抱歉，在 " + WebUtils.htmlEscape(cate)
						+ " 中没有找到与 <span class=\"key\">"
						+ WebUtils.htmlEscape(param.getQ()) + "</span> 相关的专辑。";
			}
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
			logger.debug("查询cms数据...");
		}
		model.put("cms", NovaMiddleResourceUtil.cms(p.query));

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
}