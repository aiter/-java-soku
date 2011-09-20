package com.youku.search.sort.servlet.search_page.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.torque.Torque;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.youku.search.config.Config;
import com.youku.search.sort.MemCache;
import com.youku.search.sort.Parameter;
import com.youku.search.sort.ParameterNames;
import com.youku.search.sort.MemCache.StoreResult;
import com.youku.search.sort.entity.BarCatalogMap;
import com.youku.search.sort.entity.BarCatalogMap.Catalog;
import com.youku.search.sort.json.util.JSONUtil;
import com.youku.search.sort.json.util.JSONUtil.ParseResult;
import com.youku.search.sort.servlet.search_page.QueryStringParser;
import com.youku.search.sort.servlet.search_page.SearchHelper;
import com.youku.search.sort.servlet.search_page.WebParam;
import com.youku.search.sort.servlet.search_page.WebParamHelper;
import com.youku.search.sort.servlet.util.WebUtils;
import com.youku.search.util.JdbcUtil;
import com.youku.search.util.MyUtil;

@Controller
public class BarController extends BaseController {

	@RequestMapping(value = { "/search_bar", "/search_bar/**/*" })
	public String bar(HttpServletRequest request, HttpServletResponse response,
			ModelMap model) throws Exception {

		Map<String, String> web = QueryStringParser.parse(request,
				"/search_bar/");
		return handleBar(request, model, web);
	}
	
	@RequestMapping(value = { "/search/bar", "/search/bar/**/*" })
	public String barOld(HttpServletRequest request, HttpServletResponse response,
			ModelMap model) throws Exception {

		Map<String, String> web = QueryStringParser.parse(request,
				"/search/bar/");
		return handleBar(request, model, web);
	}
	
	private String handleBar(HttpServletRequest request, ModelMap model,Map<String, String> web)
	throws Exception {
		web.put("sbt", "post");
		
		WebParam param = WebParamHelper.parse(web);

		String query_url = Parameter.buildQueryUrl(request);
		query_url=query_url+"_sbt_post";
		

		Map<ParameterNames, String> map = WebParamHelper.convertBar(param);
		map.put(ParameterNames.query_url, query_url);

		Parameter p = new Parameter(map);

		model.put("searchType", "bar");
		model.put("searchCaption", "看吧");
		model.put("searchExtend", param.getSbt());

		model.put("webParam", param);
        
		param.setSbt(WebParam.TYPE_BAR);
		String sbt = param.getSbt();
		if (WebParam.TYPE_POST.equals(sbt) || WebParam.TYPE_USER.equals(sbt)) {
			return handle_post_user(param, p, model);
		} else {
			return handle_bar(param, p, model);
		}
	}
	

	private String handle_bar(WebParam param, Parameter p, ModelMap model) {
//		String keyword = param.getQ();
		String keyword = p.query;

		JSONObject bar = null;
		JSONObject relatedBars = null;

		try {
			bar = get_bar(keyword);

			if (bar != null) {
				relatedBars = SearchHelper.search(p);
			}

			model.addAttribute("bar", bar);
			model.addAttribute("relatedBars", relatedBars);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		try {
			JSONArray bar_catalogs = getBarCatalogs(bar);
			model.addAttribute("bar_catalogs", bar_catalogs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		try {
			JSONArray lastest_posts = getLastest_posts(bar);
			model.addAttribute("lastest_posts", lastest_posts);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		try {
			JSONObject postsearch = getBarPost(param, p);
			model.addAttribute("postsearch", postsearch);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return "/youku/barResult.jsp";
	}

	private JSONArray getBarCatalogs(JSONObject bar) throws Exception {
		JSONArray array = new JSONArray();

		if (bar == null) {
			return array;
		}

		int bar_id = bar.optInt("barid");
		BarCatalogMap map = BarCatalogMap.getInstance();
		List<Catalog> list = map.getCatalogs(bar_id);

		if (list == null) {
			return array;
		}

		for (Catalog catalog : list) {
			JSONObject object = new JSONObject();
			object.put("id", catalog.id);
			object.put("name", catalog.name);
			object.put("parent_id", catalog.parent_id);

			array.put(object);
		}

		return array;
	}

	private JSONArray getLastest_posts(JSONObject bar) throws Exception {

		JSONArray array = new JSONArray();
		if (bar == null) {
			return array;
		}

		final int bar_id = bar.optInt("barid");
		int cacheSeconds = Config.getCacheTimeOut();
		if (cacheSeconds <= 0) {
			cacheSeconds = 300;// 5分钟
		}

		final String key = "youku_bar_last_posts_" + bar_id;

		// cache
		if (logger.isDebugEnabled()) {
			logger.debug("检查最新帖子缓存, key: " + key);
		}

		String json = (String) MemCache.cacheGet(key, cacheSeconds);
		ParseResult parseResult = JSONUtil.tryParse(json);
		if (parseResult.valid) {
			array = parseResult.object.optJSONArray("data");
			if (logger.isDebugEnabled()) {
				logger.debug("缓存有效, json:\n" + parseResult.object.toString(4));
			}

			return array;
		}

		// database
		String sql = "select pk_subject, subject from t_bar_subject where fk_bar = ? and deleted = 0 order by last_post_time desc limit 3";

		if (logger.isDebugEnabled()) {
			logger.debug("缓存无效, 查询数据库, sql: " + sql);
		}

		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement statement = null;
		try {
			conn = Torque.getConnection("youkubar");

			statement = conn.prepareStatement(sql);
			statement.setInt(1, bar_id);

			rs = statement.executeQuery();

			while (rs.next()) {
				int pk_subject = rs.getInt("pk_subject");
				String subject = MyUtil.getString(rs.getString("subject"));

				JSONObject object = new JSONObject();
				object.put("pk_subject", pk_subject);
				object.put("subject", subject);

				array.put(object);
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, statement, conn);
		}

		// cache
		if (logger.isDebugEnabled()) {
			logger.debug("查询数据库结束, 最后查询到的数据:\n" + array.toString(4));
		}
		try {
			JSONObject data = new JSONObject();
			data.put("data", array);

			StoreResult storeResult = MemCache.cacheSet(key, data.toString(),
					cacheSeconds);

			if (logger.isDebugEnabled()) {
				logger.debug("存储到缓存结果: " + storeResult);
			}

			if (!StoreResult.success.equals(storeResult)) {
				if (logger.isWarnEnabled()) {
					logger.warn("存储到缓存结果: " + storeResult + "; key: " + key);
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return array;
	}

	private String handle_post_user(WebParam param, Parameter p, ModelMap model)
			throws Exception {

		String resultView = "/youku/postResult.jsp";
		String nullResultView = "/youku/nullResult_jsdx.jsp";

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

		// 空结果
		if (JSONUtil.isEmpty(result.optJSONObject("items"))) {

			if (logger.isDebugEnabled()) {
				logger.debug("本次查询返回空结果！");
			}

			String showMsg = "抱歉，没有找到与 <span class=\"key\">"
					+ WebUtils.htmlEscape(param.getQ()) + "</span> 相关的帖子。";
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

		// ok
		if (logger.isDebugEnabled()) {
			logger.debug("查询bar信息...");
		}
		model.addAttribute("bar", get_bar(p.query));

		if (logger.isDebugEnabled()) {
			StringBuilder builder = new StringBuilder();
			builder.append("查询完毕，显示页面: " + resultView);
			builder.append(", result:\n" + result.toString(4));

			logger.debug(builder.toString());
		}

		return resultView;
	}
	
	public JSONObject getBarPost(WebParam param, Parameter p)
	throws Exception {	
	logger.info("开始获取看吧帖子...");
	JSONObject result = SearchHelper.search(p);
	//logger.info("查询看吧帖子的result为："+result.toString(4));
	return result;
	}

}
