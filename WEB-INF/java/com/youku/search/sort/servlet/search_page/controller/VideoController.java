package com.youku.search.sort.servlet.search_page.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.youku.search.sort.KeywordFilter;
import com.youku.search.sort.Parameter;
import com.youku.search.sort.ParameterNames;
import com.youku.search.sort.constant.SearchConstant;
import com.youku.search.sort.entity.CategoryMap;
import com.youku.search.sort.entity.CategoryMap.Category;
import com.youku.search.sort.json.util.JSONUtil;
import com.youku.search.sort.servlet.search_page.QueryStringParser;
import com.youku.search.sort.servlet.search_page.SearchHelper;
import com.youku.search.sort.servlet.search_page.WebParam;
import com.youku.search.sort.servlet.search_page.WebParamHelper;
import com.youku.search.sort.servlet.search_page.util.NovaMiddleResourceUtil;
import com.youku.search.sort.servlet.util.WebUtils;
import com.youku.search.sort.util.bridge.SearchUtil;
import com.youku.search.util.Constant;

@Controller
public class VideoController extends BaseController {

	@RequestMapping(value = { "/search_video", "/search_video/**/*" })
	public String video(HttpServletRequest request,HttpServletResponse response, ModelMap model)
			throws Exception {

		Map<String, String> web = QueryStringParser.parse(request,
				"/search_video/");
		return handleVideo(request,response, model, web);
	}
	
	@RequestMapping(value = { "/search/video", "/search/video/**/*" })
	public String videoOld(HttpServletRequest request,HttpServletResponse response, ModelMap model)
			throws Exception {

		Map<String, String> web = QueryStringParser.parse(request,
				"/search/video/");
		return handleVideo(request,response, model, web);
	}
	private String handleVideo(HttpServletRequest request, HttpServletResponse response, ModelMap model,Map<String, String> web)
		throws Exception {

		WebParam param = WebParamHelper.parse(web);

		String resultView = "/youku/video.jsp";

		String query_url = Parameter.buildQueryUrl(request);

		String view = handle_video(query_url, param, model, resultView,
				resultView, web);
		
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

	@RequestMapping(value = { "/search_advancedvideo",
			"/search_advancedvideo/**/*" })
	public String advancedvideo(HttpServletRequest request, ModelMap model)
			throws Exception {
		Map<String, String> web = QueryStringParser.parse(request,
				"/search_advancedvideo/");
		return handleAdvanceVideo(request, model, web);
	}
	
	@RequestMapping(value = { "/search/advancedvideo","/search/advancedvideo/**/*" })
	public String advancedvideoOld(HttpServletRequest request, ModelMap model)
		throws Exception {
		Map<String, String> web = QueryStringParser.parse(request,
				"/search/advancedvideo/");
		return handleAdvanceVideo(request, model, web);
	}
	
	private String handleAdvanceVideo(HttpServletRequest request, ModelMap model,Map<String, String> web)
	throws Exception {
		String keyword = web.get("q");
		//TODO 2011.1.19 注释
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

		model.put("searchType", "video");
		model.put("searchCaption", "视频");
		model.put("searchExtend", "bar");

		model.put("webParam", param);

		model.put("keyword", keyword);
		model.put("key1", key1);

		return "/youku/advancedVideo.jsp";
	}

	/**
	 * 高清Video查询，与江苏电信合作，页面内广告为订制化
	 */
	@RequestMapping(value = { "/search_hdvideo", "/search_hdvideo/**/*" })
	public String hdvideo(HttpServletRequest request, ModelMap model)
			throws Exception {

		Map<String, String> web = QueryStringParser.parse(request,
				"/search_hdvideo/");
		return handleHdvideo(request, model, web);
	}
	
	@RequestMapping(value = { "/search/hdvideo", "/search/hdvideo/**/*" })
	public String hdvideoOld(HttpServletRequest request, ModelMap model)
			throws Exception {

		Map<String, String> web = QueryStringParser.parse(request,
				"/search/hdvideo/");
		return handleHdvideo(request, model, web);
	}
	
	private String handleHdvideo(HttpServletRequest request, ModelMap model,Map<String, String> web)
	throws Exception {
		WebParam param = WebParamHelper.parse(web);

		param.setHd(1);

		String resultView = "/youku/hdVideoResult.jsp";
		String nullResultView = "/youku/nullResult_jsdx.jsp";

		String query_url = Parameter.buildQueryUrl(request);

		String view = handle_video(query_url, param, model, resultView,
				nullResultView,web);
		return view;
	}

	/**
	 * rss输出
	 */
	@RequestMapping(value = "/search/rss/type/video/q/{keyword}")
	public String rssvideo(HttpServletRequest request,
			@PathVariable(value = "keyword") String keyword, ModelMap model)
			throws Exception {

		keyword = WebUtils.urlDecode(keyword);
		keyword = KeywordFilter.filter(keyword);

		WebParam param = new WebParam();
		param.setQ(keyword);
		param.setOrderby(1);
		param.setPage(1);
		param.setPagesize(100);

		String resultView = "/youku/rssVideo.jsp";
		String nullResultView = resultView;

		String query_url = Parameter.buildQueryUrl(request);

		String view = handle_video(query_url, param, model, resultView,
				nullResultView,new HashMap());
		return view;
	}

	String handle_video(String query_url, WebParam param, ModelMap model,
			String resultView, String nullResultView, Map<String, String> webParameter) throws Exception {

		// 
		Map<ParameterNames, String> map = WebParamHelper.convertVideo(param);
		map.put(ParameterNames.query_url, query_url);

		Parameter p = new Parameter(map);

		CategoryMap categoryMap = CategoryMap.getInstance();
		List<Category> videoCategories = CategoryMap.getInstance().videoList;
		Category category = categoryMap.map.get(param.getCateid());

		if (logger.isDebugEnabled()) {
			StringBuilder builder = new StringBuilder();
			builder.append("\n" + "后端参数map: " + map);
			logger.debug(builder.toString());
		}

		//
		model.put("searchType", "video");
		model.put("searchCaption", "视频");
		model.put("searchExtend", "bar");

		model.put("webParam", param);
		model.put("categories", videoCategories);
		model.put("category", category);
		model.put("webParameter", webParameter);

		// 
		if (logger.isDebugEnabled()) {
			logger.debug("开始查询数据...");
		}

		JSONObject result = SearchHelper.search(p);//TODO  搜索传递的都是未处理过的keyword
		model.put("blursearch", 0);
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

		
		// 模糊匹配
		if (JSONUtil.isEmpty(result.optJSONObject("items"))) {
			// 这里排除了C-Server的搜索，因为C-Server返回的已经是精确+模糊的结果了
			// modified by gaosong
			if (!SearchUtil.getIsCServer(p.type)) {
				if (logger.isDebugEnabled()) {
					logger.debug("第一次查询返回空结果，开始第二次模糊查询...");
				}

				p.logic = Constant.Operator.OR;
				p.orderFieldStr = "null";
				p.orderField = Constant.Sort.SORT_SCORE;

				result = SearchHelper.search(p);
				model.put("blursearch", 1);
				model.put("result", result);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("第一次查询返回空结果，但是不需要第二次模糊查询");
				}
			}
		}

		// 节目
		if (param.getPage() == 1
				&& param.getLengthtype() == 0 && (param.getLimitdate()==null || param.getLimitdate().length()==0)  && param.getHd() == 0 && param.getOrderby()==1) {

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
				&& JSONUtil.isEmpty(result.optJSONObject("major_term"))
				&& JSONUtil.isEmpty(result.optJSONObject("series_odshows"))
				&& JSONUtil.isEmpty(result.optJSONArray("person_odshows"))
				&& JSONUtil.isEmpty(result.optJSONArray("odshows"))) {

			if (logger.isDebugEnabled()) {
				logger.debug("本次查询返回空结果，没有找到索引数据、版权库数据、直达区数据！");
			}

			// 排行榜
			if (param.getCateid() > 0 && "top".equals(param.getSource())) {
				model.clear();

				String redirect_url = "redirect:/search_video/q_"
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
						+ WebUtils.htmlEscape(param.getQ()) + "</span> 相关的视频。";
			} else {
				showMsg = "抱歉，在 " + WebUtils.htmlEscape(cate)
						+ " 中没有找到与 <span class=\"key\">"
						+ WebUtils.htmlEscape(param.getQ()) + "</span> 相关的视频。";
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

		// 本站无推荐节目，使用站外搜索
//		if (param.getPage() == 1
//				&& param.getLengthtype() == 0 && param.getHd() == 0 && param.getOrderby()==1) {  
//
//
//			if (logger.isDebugEnabled()) {
//				logger.debug("没有找到版权数据，尝试查询站外搜索数据...");
//			}
//
//			JSONObject outSearchResult = getOutSearchResult(param.getQ(), "2");
//			result.put("outSearchResult", outSearchResult);
//		}

		//CMS内容
		if (logger.isDebugEnabled()) {
			logger.debug("查询cms数据...");
		}
//		model.put("cms", NovaMiddleResourceUtil.cms(param.getQ()));
		model.put("cms", NovaMiddleResourceUtil.cms(p.query));

		//推荐版权
		if (logger.isDebugEnabled()) {
			logger.debug("查询推荐版权内容...");
		}
		String showids = result.optString("search_showids");
		model.put("recommendShows", NovaMiddleResourceUtil
				.recommendShows(showids));

		//bar信息
		if (logger.isDebugEnabled()) {
			logger.debug("查询bar信息...");
		}
//		model.put("bar", get_bar(param.getQ()));
		model.put("bar", get_bar(p.query));

		if (logger.isDebugEnabled()) {
			StringBuilder builder = new StringBuilder();
			builder.append("查询完毕，显示页面: " + resultView);
			builder.append(", result:\n" + result.toString(4));

		//	logger.debug(builder.toString());
		}
		return resultView;
	}

	public static void main(String[] args) throws Exception {
		
//		BasicConfigurator.configure();
//
		BaseController controller = new VideoController();
		
//
//		if (false) {
//			JSONObject result = new JSONObject();
//			WebParam param = new WebParam();
//			param.setQ("优酷牛人");
//			param.setQ("优酷牛人盛典");
//			param.setQ("李小龙");
//			param.setQ("西游记");
//			controller.commendOdshow(param.getQ(), null, result);
//		}
//
//		byte[] bytes = Wget
//				.get("http://test.www.soku.com/search/rss/type/video/q/mm");
//		String s = new String(bytes, "UTF-8");
//
//		System.out.println(s);
//
//		s = s.trim();
//
//		Document document = DocumentBuilderFactory.newInstance()
//				.newDocumentBuilder().parse(
//						new InputSource(new StringReader(s)));
//		
//		
//		
		JSONObject jsonObject = new JSONObject();
//		controller.commendOdshow("非诚勿扰", null, jsonObject);
//		System.out.println(jsonObject.toString(4));
//		String test1 = "英超";
//		String test1 = "烈火红岩";
		String test1 = "王菲";
		controller.commendOdshow(test1, null, jsonObject);
		System.out.println(jsonObject.toString(4));
		

		
//		JSONArray videosJSONArray = JSONUtil.getProperty(JSONUtil.getProperty(JSONUtil.getProperty(JSONUtil.getProperty(jsonObject,JSONObject.class,"odshows"),JSONObject.class,"102192"),JSONObject.class,"episodes"), JSONArray.class, "videos");
//		System.out.println(videosJSONArray);
//		for(int i = 0; i < videosJSONArray.length(); i++) {
//			JSONObject video = videosJSONArray.optJSONObject(i);
//			System.out.println(WebUtils.htmlEscape(video.optString("title")));
//		}
		
	}
}