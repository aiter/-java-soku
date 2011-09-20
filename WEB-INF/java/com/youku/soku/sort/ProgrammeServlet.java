package com.youku.soku.sort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import com.youku.search.pool.net.util.Cost;
import com.youku.search.sort.json.util.JSONUtil;
import com.youku.search.sort.servlet.search_page.util.NovaMiddleResource;
import com.youku.search.sort.servlet.search_page.util.NovaMiddleResourceUtil;
import com.youku.search.util.MyUtil;
import com.youku.search.util.StringUtil;
import com.youku.search.util.Wget;
import com.youku.soku.config.ExtServerConfig;
import com.youku.soku.index.manager.db.SiteManager;
import com.youku.soku.library.load.douban.DoubanWget;
import com.youku.soku.library.load.util.SyncUtil;
import com.youku.soku.manage.util.ImageUtil;

/**
 * 详情页入口点
 */
public class ProgrammeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	Log logger = LogFactory.getLog(getClass());
	
	/*private static Map<String, String> siteNamesMap = new HashMap<String, String>(30);
	static{
		initNamesMap();
	}
	
	private static void initNamesMap() {
		siteNamesMap.put("14", "优酷网");
		siteNamesMap.put("1", "土豆网");
		siteNamesMap.put("2", "56网");
		siteNamesMap.put("12", "六间房");
		siteNamesMap.put("6", "搜狐");
		siteNamesMap.put("3", "新浪网");
		siteNamesMap.put("25", "爱拍游戏");
		siteNamesMap.put("5", "第一视频");
		siteNamesMap.put("8", "凤凰网");
		siteNamesMap.put("9", "激动网");
		siteNamesMap.put("4", "琥珀网");
		siteNamesMap.put("7", "央视网");
		siteNamesMap.put("15", "CNTV");
		siteNamesMap.put("17", "乐视网");
		siteNamesMap.put("20", "江苏卫视");
		siteNamesMap.put("13", "中关村在线");
		siteNamesMap.put("16", "电影网");
		siteNamesMap.put("19", "奇艺网");
		siteNamesMap.put("18", "小银幕");
		siteNamesMap.put("21", "浙江卫视");
		siteNamesMap.put("23", "安徽卫视");
		siteNamesMap.put("24", "芒果网");
		siteNamesMap.put("10", "酷6网");
		siteNamesMap.put("100", "综合");
	}*/

	public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
	{
		doGet( request, response);
	}
	
	

	public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
	{
		String type = request.getParameter("type");
		if("person".equals(type)){//人物处理
			handlPerson(request,response);
			return;
		}else if("rating".equals(type)){//打分
			handlRating(request,response);
			return;
		}
		
		
		JSONObject resultJson = null;
		try {
			String programmeId = request.getParameter("pid");
			resultJson = handleProgramme(programmeId);
		} catch (JSONException e1) {
			
		}
		
		if(resultJson ==null){
			return;
		}
		
		//6、设置是否顶踩信息
		String showid = resultJson.optString("showid");
		if(showid!=null && showid.length()>0){
			char rate = getRating(showid, request);
			try {
				resultJson.put("rating", rate+"");
			} catch (JSONException e) {
			}
		}
		
		if (request.getParameter("h") !=null){
			response.setContentType("text/html; charset=utf-8");
				try {
					response.getWriter().print(resultJson.toString(4));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			return;
		}
		
		request.setAttribute("result",resultJson);
		/* 获取上下文环境 */
		ServletContext sc = getServletContext();

		/* 设置返回地址 */
		RequestDispatcher rd=null;
		rd = sc.getRequestDispatcher("/result/programme.jsp");
		
		/* forward到结果页面 */
		rd.forward(request, response);
		
		return;
		
	}
	/**
	 * @param request
	 * @param response
	 */
	private void handlRating(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String programmeId = request.getParameter("pid");
		if(programmeId==null || programmeId.length()==0){
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().print("failure");
			return;
		}
//		int pid = 0; 
//		pid = MyUtil.decodeVideoId(programmeId); //只接受编码过的ID
		String showid = programmeId; //这里直接传showid
		int rating = StringUtil.parseInt(request.getParameter("rating"), 5);
		
		
		char rate = ' ';
		rate = getRating(showid,request);
		if('d'==rate){
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().print("ok1");
			return;
		}else if ('u'==rate) {
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().print("ok5");
			return;
		}
		
		//如果没有顶/踩过，调用中间层接口
		/**
		 * 顶：http://www.youku.com/show_rating/showid_za1903184250211e097c0_rating_5.html  返回：ok5
		 * 踩：http://www.youku.com/show_rating/showid_za1903184250211e097c0_rating_1.html  返回：ok1
		 * 失败为：failure 
		 */
		
		try {
			String ret = new String(DoubanWget.get("http://www.youku.com/show_rating/showid_z"+showid+"_rating_"+rating+".html"));
			if("ok1".equals(ret)){
				writeCookie(request,response,"_srate",'.'+showid+'d');
			}else if ("ok5".equals(ret)) {
				writeCookie(request,response,"_srate",'.'+showid+'u');
			}else {
				ret = "failure";
			}
			
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().print(ret);
		} catch (Exception e) {
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().print("failure");
		}
	}

	/**
	 * @param showid 
	 * @param request
	 * @return
	 */
	private char getRating(String showid, HttpServletRequest request) {
		Cookie [] cookies = request.getCookies();//_srate=.za1903184250211e097c0d.za1903184250211e097c0u 最后一位代表down/up
		if(cookies!=null && cookies.length>0) {
			String srate = null;
			for(Cookie cookie:cookies) {
				if("_srate".equals(cookie.getName())){
					srate = cookie.getValue();
				}
    		}
			if(srate!=null){
				String [] rates = srate.split("\\.");
				for (int i = 1; i < rates.length; i++) {
					if(showid.equals(rates[i].substring(0,rates[i].length()-1))){
						return rates[i].charAt(rates[i].length()-1);
					}
				}
			}
		}
		
		return ' ';
	}

	/**
	 * @param request
	 * @param response
	 * @param string
	 * @param string2
	 */
	private void writeCookie(HttpServletRequest request,
			HttpServletResponse response, String cookieKey, String cookieValue) {
		Cookie [] cookies = request.getCookies();//_srate=.za1903184250211e097c0d.za1903184250211e097c0u 最后一位代表down/up
		if(cookies!=null && cookies.length>0) {
			String oldValue = null;
			for(Cookie cookie:cookies) {
				if(cookieKey.equals(cookie.getName())){
					oldValue = cookie.getValue();
				}
    		}
			if(oldValue!=null){
				oldValue +=cookieValue;
			}else {
				oldValue = cookieValue;
			}
			
			Cookie cookie = new Cookie(cookieKey, oldValue);
			cookie.setPath("/");
			cookie.setMaxAge(365*24*60*60);
			response.addCookie(cookie);
		}
	}

	/**
	 * 处理人物详情页
	 * @param personName
	 */
	private void handlPerson(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		String programmeId = request.getParameter("pid");
		if(programmeId==null || programmeId.length()==0){
			//错误页面
			return;
		}
		
		int personId = MyUtil.decodeVideoId(programmeId);
		
		String personName = programmeId;
		if(personId>0){
			personName=personId+"";
		}
		
		//http://10.103.8.217/personpro/search?person=%E5%A7%9C%E6%96%87
		//http://10.103.8.217/personpro/search?person=姜文&h&d   d详细信息
		//http://10.103.8.217/personpro/search?person=姜文&h&l   l简要信息
		try {
			StringBuffer serverURI = new StringBuffer("http://");
			serverURI.append(
					org.apache.commons.lang.StringUtils.trimToEmpty(ExtServerConfig.getRandomServer()));
			serverURI.append(org.apache.commons.lang.StringUtils.trimToEmpty(ExtServerConfig.getInstance().getString("PERSON_DETAIL")));
			serverURI.append(personName);
			
			
			Cost cost = new Cost();
			JSONObject personObject = new JSONObject(new String(Wget.get(serverURI.toString())));
			cost.updateEnd();
			logger.info("ext:"+cost.getCost());
			
			
			if(!JSONUtil.isEmpty(personObject)){
				
//				JSONObject result = SyncUtil.buildPerson(personName);
//				if(!JSONUtil.isEmpty(result) && !JSONUtil.isEmpty(result, "results")) {
//					JSONObject firstOne = result.optJSONArray("results").optJSONObject(0);
//					if(!JSONUtil.isEmpty(firstOne)){
//						
//						JSONObject detail = personObject.optJSONObject("detail");
//						if(!JSONUtil.isEmpty(detail)) {
//							for(Iterator<String> itr = firstOne.keys();itr.hasNext();){
//								String key = itr.next();
//								detail.put(key, firstOne.opt(key));
//							}
//								
//							
//						}else {
//							personObject.put("detail", firstOne);
//						}
//						
//					}
//				}
				
				if (request.getParameter("h") !=null){
					response.setContentType("text/html; charset=utf-8");
						try {
							response.getWriter().print(personObject.toString(4));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					return;
				}
				
				request.setAttribute("result",personObject);
				/* 获取上下文环境 */
				ServletContext sc = getServletContext();

				/* 设置返回地址 */
				RequestDispatcher rd=null;
				rd = sc.getRequestDispatcher("/result/person_new.jsp");
				
				/* forward到结果页面 */
				rd.forward(request, response);
				
				return;
			}
		} catch (Exception e) {
			throw new IOException(e.getMessage(), e);
		}
		
	}

	/**
	 * 处理节目详情页的入口
	 */
	private JSONObject handleProgramme(String programmeId) throws JSONException {
		JSONObject result = new JSONObject();
		int pid = 0; 
		if(programmeId==null){
			result.put("err", "参数出错");
		}else {
//			try {
//				//TODO 测试，阶段，可以直接接受未编码的ID
//				pid = Integer.parseInt(programmeId);
//			} catch (Exception e) {
				pid = MyUtil.decodeVideoId(programmeId); //只接受编码过的ID
//			}
			
			if (pid==0) {
				result.put("err", "参数出错");
				//TODO 正式程序需要直接  return
			}
		}
		
		
		//1、通过programmeID，查询信息
		int contentId = 0;
		Cost _extCost = new Cost();
		contentId = buildExt(result,pid);//调用EXT
		_extCost.updateEnd();
		
		//2、通过contentID，查询中间层信息
		Cost _showVideosInfoCost = new Cost();
		buildShow(result,contentId);
		//3、通过contentID，查询中间层视频信息
		buildShowVedio(result,contentId);
		_showVideosInfoCost.updateEnd();
		
		//4、通过doubanID，查询豆瓣的信息
		int doubanId = ContentDoubanMap.getDoubanId(contentId);
		if(doubanId==0){//如果对应关系中没有douban的ID，那么就看看中间层是否有
			doubanId = result.optInt("douban_num");
		}
		Cost _doubanCost = new Cost();
		buildDouban(result,doubanId);
		_doubanCost.updateEnd();
		
		//5、演员、主持人等信息
		buildPerson(result);
		
		//6、相关推荐
		buildRecommend(result,contentId);
		
		
		logger.info("ext:"+_extCost.getCost()+"::"+_showVideosInfoCost.getCost()+"::douban:"+_doubanCost.getCost());
		return result;
	}
	
	

	/**
	 * @param result
	 */
	private void buildPerson(JSONObject result) {
		String category = result.optString("showcategory");
		String person = "";
		if("电影".equals(category)){
			person = "performer";
		} else if ("电视剧".equals(category)) {
			person = "performer";
		} else if ("综艺".equals(category)) {
			person = "host";
		}
		
		List<String> personIds = getPersonsIds(result,person);
		
		if(!personIds.isEmpty()){
			JSONArray actors = new JSONArray();
			for (String pId : personIds) {
				JSONObject personObject = null;
				try {
					StringBuffer serverURI = new StringBuffer("http://");
					serverURI.append(
							org.apache.commons.lang.StringUtils.trimToEmpty(ExtServerConfig.getRandomServer()));
					serverURI.append(org.apache.commons.lang.StringUtils.trimToEmpty(ExtServerConfig.getInstance().getString("PERSON_DETAIL")));
					serverURI.append(pId);
					
					personObject = new JSONObject(new String(Wget.get(serverURI.toString())));
				} catch (Exception e) {
				}
				if(!JSONUtil.isEmpty(personObject)){
					JSONArray videos = new JSONArray();
					JSONArray movies = personObject.optJSONArray("MOVIE");
					JSONArray teleplaies = personObject.optJSONArray("TELEPLAY");
					JSONArray zyies = personObject.optJSONArray("VARIETY");
					
					Comparator<JSONObject> showComparator = new Comparator<JSONObject>(){
						@Override
						public int compare(JSONObject o1, JSONObject o2) {
							if(JSONUtil.isEmpty(o1) && JSONUtil.isEmpty(o2)){
								return 0;
							}
							if(JSONUtil.isEmpty(o1)){
								return 1;
							}
							if(JSONUtil.isEmpty(o2)){
								return -1;
							}
							int releaseyear1 = o1.optInt("releaseyear");
							int releaseyear2 = o2.optInt("releaseyear");
								
							return releaseyear1==releaseyear2?0:releaseyear2-releaseyear1;
						}
					};
						List<JSONObject> shows = new ArrayList<JSONObject>();
						if(!JSONUtil.isEmpty(movies)){
							for(int i=0;i<movies.length();i++){
								JSONObject show = movies.optJSONObject(i);
								try{
									show.put("showcategory","电影");
								}catch(Exception e){
								}
								shows.add(show);
							}
						}
						if(!JSONUtil.isEmpty(teleplaies)){
							for(int i=0;i<teleplaies.length();i++){
								JSONObject show = teleplaies.optJSONObject(i);
								try{
									show.put("showcategory","电视剧");
								}catch(Exception e){
								}
								shows.add(show);
							}
						}
						if(!JSONUtil.isEmpty(zyies)){
							for(int i=0;i<zyies.length();i++){
								JSONObject show = zyies.optJSONObject(i);
								try{
									show.put("showcategory","综艺");
								}catch(Exception e){
								}
								shows.add(show);
							}
						}
						if(!shows.isEmpty()){
							Collections.sort(shows,showComparator);
							
							int cnt = 3;
							
							for (int i = 0; i < shows.size() && i<cnt; i++) {
//								JSONObject video = new JSONObject();
								JSONObject tmp = shows.get(i);
								try {
									tmp.put("showcategory", (tmp instanceof JSONObject)?((JSONObject)tmp).optString("showcategory"):tmp);
									
									videos.put(tmp);
								} catch (JSONException e) {
								}
							}
						}
					
					
					personObject.remove("MOVIE");
					personObject.remove("TELEPLAY");
					personObject.remove("VARIETY");
					try {
						JSONObject detailJsonObject = personObject.optJSONObject("detail");
						if(!JSONUtil.isEmpty(detailJsonObject)){
							personObject.put("pic", detailJsonObject.optString("thumburl"));
						}else {
							continue;
						}
						personObject.remove("detail");
						personObject.put("video", videos);
					} catch (JSONException e) {
					}
					
					actors.put(personObject);
				}
			}
			
			if(!JSONUtil.isEmpty(actors)){
				try {
					result.put("actor", actors);
				} catch (JSONException e) {
				}
			}
		}
	}

	/**
	 * @param result
	 * @param person
	 * @return
	 *//*
	private List<String> getPersons(JSONObject result, String person) {
		JSONArray personArray = result.optJSONArray("person");
		List<String> names = new ArrayList<String>();
		if(JSONUtil.isEmpty(personArray)){
			return names;
		}
		for (int i = 0; i < personArray.length(); i++) {
			*//**
			 * [刘镇伟:director,周星驰:performer]
			 * [{id:1001,name:“周星驰”,type:“performer”,character:[“至尊宝”,”孙悟空”]},
			 * ｛id:1002,name:“刘镇伟”,type:“director”}
			 * ]
			 *//*
			Object object = personArray.opt(i);
			if(object instanceof JSONObject){
				JSONObject tmp = (JSONObject)object;
				if (person.equals(tmp.optString("type"))){
					names.add(tmp.optString("name"));
				}
			}else if(object instanceof String) {
				String tmp = (String)object;
				String[] person_info = tmp.split(":");

				String person_name = person_info[0];
				String person_type = person_info.length < 2 ? ""
						: person_info[1];

				if (person_type.equals(person)) {
					names.add(person_name);
				}
			}
		}
		return names.size()>3?names.subList(0, 3):names;
	}*/
	
	private List<String> getPersonsIds(JSONObject result, String person) {
		JSONArray personArray = result.optJSONArray("person");
		List<String> names = new ArrayList<String>();
		if(JSONUtil.isEmpty(personArray)){
			return names;
		}
		for (int i = 0; i < personArray.length(); i++) {
			/**
			 * [刘镇伟:director,周星驰:performer]
			 * [{id:1001,name:“周星驰”,type:“performer”,character:[“至尊宝”,”孙悟空”]},
			 * ｛id:1002,name:“刘镇伟”,type:“director”}
			 * ]
			 */
			Object object = personArray.opt(i);
			if(object instanceof JSONObject){
				JSONObject tmp = (JSONObject)object;
				if (person.equals(tmp.optString("type"))){
					names.add(tmp.optString("id"));
				}
			}
		}
		return names.size()>3?names.subList(0, 3):names;
	}

	/**
	 * @param result
	 * @param contentId
	 */
	private void buildRecommend(JSONObject result, int contentId) {
		if(contentId<=0){
			return;
		}
		
		try {
			result.put("recommendShows", recommendShows(contentId+""));
		} catch (JSONException e) {
		}
	}

	/**
	 * @param result
	 * @param doubanId
	 */
	private void buildDouban(JSONObject result, int doubanId) {
		if(doubanId==0){
			return;
		}
		JSONObject doubanInfo = new JSONObject();
		
		try {
			//1、电影信息
			JSONObject doubanJsonObject = DoubanWget.getMovie(doubanId+"");//http API
			doubanInfo.put("douban_id", doubanId);
			if(!JSONUtil.isEmpty(doubanJsonObject)){
				//评分
				if(!JSONUtil.isEmpty(doubanJsonObject, "gd:rating")){
					doubanInfo.put("avg_rating", doubanJsonObject.optJSONObject("gd:rating").optString("@average"));
				}
			}
			//2、评论信息
			JSONObject doubanReviews = DoubanWget.getMovieReviews(doubanId+"");
			doubanInfo.put("reviews", doubanReviews);
			
//			System.out.println(doubanJsonObject.toString(4));
//			System.out.println(doubanReviews.toString(4));
			result.put("douban", doubanInfo);
		} catch (Exception e) {
		}
		
		
	}

	/**
	 * 中间层获取顶、踩、评分等信息
	 * @param result
	 * @param contentId
	 */
	private void buildShow(JSONObject result, int contentId) {
		String SEARCH_HOST = "10.103.88.54";
		String SYNC_CALLER = "search_out";
		StringBuilder query = new StringBuilder("showid:");
		query.append(contentId);
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", "1");

		String fd_value = "pk_odshow showname showalias showcategory episode_total episode_collected episode_last" +
				" rating showtotal_up showtotal_down avg_rating reputation douban_num" +
				" area releaseyear releasedate tv_genre  movie_genre  variety_genre  anime_genre" +
				" language person issuer distributor production station tv_station update_notice showdesc ";
		params.put("fd", fd_value);
		params.put("cl", SYNC_CALLER);

		JSONObject jsonObject = NovaMiddleResource
		.search(SEARCH_HOST,"show","show", query.toString(), params);
		if(!SyncUtil.isEmpty(jsonObject)){
			JSONObject firstOne = jsonObject.optJSONArray("results").optJSONObject(0);
			for (Iterator iterator = firstOne.keys(); iterator.hasNext();) {
				String key = (String) iterator.next();
				try {
					if("rating".equals(key)){
						result.put("rating_all", firstOne.opt(key));
					}else {
						result.put(key, firstOne.opt(key));
					}
				} catch (JSONException e) {
				}
			}
		}
	}
	
	/**
	 * 模拟Ext的数据，这里先从中间层取
	 * @param result 
	 */
	private void buildBase2(JSONObject result, int pid, int contentId) {
		//1、取基本信息
		String SEARCH_HOST = "10.103.88.54";
		String SYNC_CALLER = "search_out";
		StringBuilder query = new StringBuilder("showid:");
		query.append(contentId);
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", "1");

		String fd_value = "pk_odshow showname showalias showcategory episode_total episode_collected episode_last" +
				" area releaseyear releasedate show_thumburl show_vthumburl tv_genre  movie_genre  variety_genre  anime_genre" +
				" language person issuer production station update_notice showdesc ";
		params.put("fd", fd_value);
		params.put("cl", SYNC_CALLER);

		JSONObject jsonObject = NovaMiddleResource
		.search(SEARCH_HOST,"show","show", query.toString(), params);
		if(!SyncUtil.isEmpty(jsonObject)){
			JSONObject firstOne = jsonObject.optJSONArray("results").optJSONObject(0);
			for (Iterator iterator = firstOne.keys(); iterator.hasNext();) {
				String key = (String) iterator.next();
				try {
					result.put(key, firstOne.opt(key));
				} catch (JSONException e) {
				}
			}
		}
		
		//获取视频链接
		/*JSONObject siteVideosArray = new JSONObject();
		
		StringBuilder videoQuery = new StringBuilder("show_id:");
		videoQuery.append(contentId);
		videoQuery.append(" show_videotype:正片");
		Map<String, String> videoParams = new LinkedHashMap<String, String>();
		videoParams.put("pn", "1");
		videoParams.put("pl", "1000");

		String videos_fd_value = "title thumburl state show_videostage show_videoseq show_videocompleted";
		videoParams.put("fd", videos_fd_value);
		videoParams.put("ob", "show_videoseq:asc");
		videoParams.put("cl", SYNC_CALLER);

		JSONObject videosJsonObject = NovaMiddleResource
		.search(SEARCH_HOST,"video","show", videoQuery.toString(), videoParams);
		try {
			if(!SyncUtil.isEmpty(videosJsonObject)){
					
					JSONArray siteVideos = videosJsonObject.optJSONArray("results");
					
					if(!JSONUtil.isEmpty(siteVideos)){
						//电视剧、综艺、动漫等一个节目多个视频的，根据展示未完成和完成，重新排序，并选择一个作为默认视频
						JSONObject siteObject = new JSONObject();
						siteObject.put("name", "优酷");
						siteObject.put("episodes", siteVideos);
						
						siteVideosArray.put("14", siteObject);
						
						JSONObject siteObject0 = new JSONObject();
						siteObject0.put("name", "56");
						siteObject0.put("episodes", siteVideos);
						siteVideosArray.put("2", siteObject0);
					}
					
					
			}
			result.put("videos", siteVideosArray);
		} catch (JSONException e) {
		}*/
		
		
		
	}

	/**
	 * 10.103.8.217/pro/search?pid=57606
	 * @param result 
	 * @param pid
	 * @return
	 */
	private int buildExt(JSONObject result, int pid) {
		StringBuffer serverURI = new StringBuffer("http://");
		serverURI.append(
				org.apache.commons.lang.StringUtils.trimToEmpty(ExtServerConfig.getRandomServer()));
		serverURI.append(org.apache.commons.lang.StringUtils.trimToEmpty(ExtServerConfig.getInstance().getString("PROGRAMMEPATH")));
		serverURI.append(pid);
		
		JSONObject programmeAllInfo = null;
		try {
			programmeAllInfo = new JSONObject(new String(Wget.get(serverURI.toString())));
			if(!JSONUtil.isEmpty(programmeAllInfo)){
				//节目信息
				JSONObject programmeJson = programmeAllInfo.optJSONObject("programme");
				result.put("show_thumburl", programmeJson.optString("pic").length()==0?ImageUtil.getDisplayUrl("0900641F464A911EDD00000000000000000000-0000-0000-0000-00009197BA80"):programmeJson.optString("pic"));
				if(programmeJson.optString("pic").length()==0){
					result.put("show_vthumburl", programmeJson.optString("vpic").length()==0?ImageUtil.getDisplayUrl("0900641F464A7BBE7400000000000000000000-0000-0000-0000-00005B2F7B0E"):programmeJson.optString("vpic"));
				}else {
					result.put("show_vthumburl", programmeJson.optString("vpic"));
				}
				
				
				//视频信息
				JSONObject programmeSite = programmeAllInfo.optJSONObject("ProgrammeSite");
				if(!JSONUtil.isEmpty(programmeSite)){
					for (Iterator iterator = programmeSite.keys(); iterator
							.hasNext();) {
						String key = (String) iterator.next();
						JSONObject tmp = programmeSite.optJSONObject(key);
						if(!JSONUtil.isEmpty(tmp)){
							if(JSONUtil.isEmpty(tmp,"episodes")){//如果没有视频，就删除该站点
								iterator.remove();
								continue;
							}
							tmp.put("name", getSiteName(key));
						}
						
					}
					
					result.put("videos", programmeSite);
				}
				
				//系列
				if(!JSONUtil.isEmpty(programmeAllInfo, "series")){
					result.put("series", programmeAllInfo.optJSONObject("series").optJSONArray("programmes"));
				}
				
				//中间层原始信息
				
				
				if(!JSONUtil.isEmpty(programmeJson)){
					return programmeJson.optInt("contentId");
				}
			}
		} catch (Exception e) {
		}
		return 0;
	}
	
	/**
	 * @param key
	 * @return
	 */
	private String getSiteName(String key) {
		return SiteManager.getInstance().getName(StringUtil.parseInt(key, 0));
	}

	/**
	 * 中间层获取咨询，花絮，mv
	 * @param result
	 * @param contentId
	 */
	private void buildShowVedio(JSONObject result, int contentId) {
		JSONObject showInfo = SyncUtil.buildProgrammePageVideoByID(contentId);
		if(SyncUtil.isEmpty(showInfo)){
			return;
		}
		
		JSONArray allVideos = showInfo.optJSONArray("results");
		if(JSONUtil.isEmpty(allVideos)){
			return;
		}
		
		Map<String, JSONArray> map = new HashMap<String, JSONArray>();
		JSONObject tmp = null;
		String videoType = null;
		JSONArray videoArr = null;
		for (int i = 0; i < allVideos.length(); i++) {
			tmp = allVideos.optJSONObject(i);
			videoType = tmp.optString("show_videotype");
			videoArr = map.get(videoType);
			if(videoArr==null){
				videoArr = new JSONArray();
				map.put(videoType, videoArr);
			}
			
			if(videoArr.length()<5){
				videoArr.put(tmp);
			}
			
			tmp = null;
			videoType = null;
			videoArr = null;
		}
		
		try {
			if(map.size()>0) {
				for (String key : map.keySet()) {
					result.put(key, map.get(key));
				}
			}
		
//			System.out.println(showInfo.toString(4));
		} catch (JSONException e) {
		}
	}
	

	//通过节目ID，查找对应的豆瓣ID，
	public static class ContentDoubanMap{
		private static final Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		static{
			init();
		}
		
		private static void init(){
		}
		
		public static int getDoubanId(int contentId){
			Object tmp = map.get(contentId);
			if(tmp==null){
				return 0;
			}else {
				return (Integer)tmp;
			}
		}
	}
	
	
	/**
	 * 页面右侧的相关推荐，站内也有这种推荐，但是在信息显示，数量上有些不同，现在先放这里
	 * @see NovaMiddleResourceUtil
	 * @param args
	 * @throws JSONException
	 */
	public static JSONArray recommendShows(String showids) {

		final int COUNT = 4;

		if (showids == null) {
			return null;
		}

		String showid = null;
		String[] showid_array = showids.split(",");
		for (int i = 0; i < showid_array.length; i++) {
			showid = showid_array[i].trim();
			if (!showid.isEmpty()) {
				break;
			}
		}

		if (showid == null || showid.isEmpty()) {
			return null;
		}

		// show信息
		String query = "showid:" + showid;

		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("fd",
				"showcategory area person movie_genre tv_genre variety_genre");

		JSONObject result = NovaMiddleResource.search("show", "show", query,
				params);
		JSONArray results = JSONUtil.getProperty(result, JSONArray.class,
				"results");

		if (JSONUtil.isEmpty(results)) {
			return null;
		}

		JSONObject show = results.optJSONObject(0);
		if (JSONUtil.isEmpty(show)) {
			return null;
		}

		String area = JSONUtil.join(show.optJSONArray("area"), ",");
		String showcategory = show.optString("showcategory");

		// 按人取
		TreeSet<String> persons = new TreeSet<String>();
		TreeSet<String> scenarist = new TreeSet<String>();// 编剧
		TreeSet<String> director = new TreeSet<String>();// 导演
		TreeSet<String> performer = new TreeSet<String>();// 演员
		TreeSet<String> host = new TreeSet<String>();// 主持人

		if (show.optJSONArray("person") != null) {
			JSONArray personArray = show.optJSONArray("person");
			for (int i = 0; i < personArray.length(); i++) {
				/**
				 * [刘镇伟:director,周星驰:performer]
				 * [{id:1001,name:“周星驰”,type:“performer”,character:[“至尊宝”,”孙悟空”]},
				 * ｛id:1002,name:“刘镇伟”,type:“director”}
				 * ]
				 */
				Object object = personArray.opt(i);
				if(object instanceof JSONObject){
					JSONObject person = (JSONObject)object;

					if ("scenarist".equals(person.optString("type")) && scenarist.size() < 1) {
						scenarist.add(person.optString("name"));
					}
					if ("director".equals(person.optString("type")) && director.size() < 1) {
						director.add(person.optString("name"));
					}
					if ("performer".equals(person.optString("type")) && performer.size() < 3) {
						performer.add(person.optString("name"));
					}
					if ("host".equals(person.optString("type")) && host.size() < 2) {
						host.add(person.optString("name"));
					}
				}else if (object instanceof String) {
					String person = (String)object;
					String[] person_info = person.split(":");

					String person_name = person_info[0];
					String person_type = person_info.length < 2 ? ""
							: person_info[1];

					if (person_type.equals("scenarist") && scenarist.size() < 1) {
						scenarist.add(person_name);
					}
					if (person_type.equals("director") && director.size() < 1) {
						director.add(person_name);
					}
					if (person_type.equals("performer") && performer.size() < 3) {
						performer.add(person_name);
					}
					if (person_type.equals("host") && host.size() < 2) {
						host.add(person_name);
					}
				}
				
			}
		}

		persons.addAll(scenarist);
		persons.addAll(director);
		persons.addAll(performer);
		persons.addAll(host);

		final String fd = "showname performer releasedate show_thumburl streamtypes paid showday_vv avg_rating reputation showcategory";

		JSONArray shows = new JSONArray();// 保存最后的结果
		if (!persons.isEmpty()) {
			query = "-showid:" + showid
					+ " state:normal allowfilter:1 hasvideotype:正片";
			if (area.length() > 0) {
				query += " area:" + area;
			}
			if (showcategory.length() > 0) {
				query += " showcategory:" + showcategory;
			}

			query += " person:"
					+ StringUtils.collectionToDelimitedString(persons, ",");

			params = new LinkedHashMap<String, String>();
			params.put("fd", fd);
			params.put("pl", "7");
			params.put("ob", "showday_vv:desc");

			result = NovaMiddleResource.search("show", "show", query, params);
			results = JSONUtil.getProperty(result, JSONArray.class, "results");

			if (!JSONUtil.isEmpty(results)) {
				shows = results;
			}
		}

		// 按分类取
		query = "";
		JSONArray movie_genre = show.optJSONArray("movie_genre");
		if (!JSONUtil.isEmpty(movie_genre)) {
			query += " movie_genre:" + JSONUtil.join(movie_genre, ",", 3);
		}

		JSONArray tv_genre = show.optJSONArray("tv_genre");
		if (!JSONUtil.isEmpty(tv_genre)) {
			query += " tv_genre:" + JSONUtil.join(tv_genre, ",", 3);
		}

		JSONArray variety_genre = show.optJSONArray("variety_genre");
		if (!JSONUtil.isEmpty(variety_genre)) {
			query += " variety_genre:" + JSONUtil.join(variety_genre, ",", 3);
		}

		if (query.length() > 0) {
			query += " -showid:" + showid
					+ " state:normal allowfilter:1 hasvideotype:正片";

			if (area.length() > 0) {
				query += " area:" + area;
			}
			if (showcategory.length() > 0) {
				query += " showcategory:" + showcategory;
			}

			params = new LinkedHashMap<String, String>();
			params.put("fd", fd);
			params.put("pl", "6");
			params.put("ob", "showday_vv:desc");

			result = NovaMiddleResource.search("show", "show", query, params);
			results = JSONUtil.getProperty(result, JSONArray.class, "results");

			if (!JSONUtil.isEmpty(results)) {
				if (JSONUtil.isEmpty(shows)) {
					shows = results;
				} else {
					for (int i = 0; i < results.length(); i++) {
						shows.put(results.opt(i));
					}
				}
			}
		}

		// 按人和分类取完后，去重
		shows = removeCopies(shows);

		// 少于COUNT个节目，补足
		if (shows.length() < COUNT) {
			query = "-showid:" + showid
					+ " state:normal allowfilter:1 hasvideotype:正片";

			if (showcategory.length() > 0) {
				query += " showcategory:" + showcategory;
			}

			params = new LinkedHashMap<String, String>();
			params.put("fd", fd);
			params.put("pl", String.valueOf((COUNT - shows.length()) * 2));
			params.put("ob", "showday_vv:desc");

			result = NovaMiddleResource.search("show", "show", query, params);
			results = JSONUtil.getProperty(result, JSONArray.class, "results");

			if (!JSONUtil.isEmpty(results)) {
				if (JSONUtil.isEmpty(shows)) {
					shows = results;
				} else {
					for (int i = 0; i < results.length(); i++) {
						shows.put(results.opt(i));
					}
				}
			}

			// 补足后，去重
			shows = removeCopies(shows);
		}

		if (shows.length() > COUNT) {
			JSONArray shows_COUNT = new JSONArray();
			for (int i = 0; i < shows.length() && i < COUNT; i++) {
				shows_COUNT.put(shows.opt(i));
			}

			shows = shows_COUNT;
		}

		return shows;
	}

	/**
	 * 去重
	 */
	private static JSONArray removeCopies(JSONArray shows) {

		Set<String> showidSet = new HashSet<String>();
		JSONArray shows_new = new JSONArray();

		for (int i = 0; i < shows.length(); i++) {
			JSONObject item = shows.optJSONObject(i);
			if (item == null) {
				continue;
			}

			String pk_odshow = item.optString("pk_odshow");
			if (!showidSet.contains(pk_odshow)) {
				showidSet.add(pk_odshow);
				shows_new.put(item);
			}
		}

		return shows_new;
	}
	
	
	public static void main(String[] args) throws JSONException {
//		ProgrammeServlet servlet = new ProgrammeServlet();
//		JSONObject jsonObject = servlet.handleProgramme(MyUtil.encodeVideoId(1));
//		
//		System.out.println(jsonObject.toString(4));
		
	}
	
}
