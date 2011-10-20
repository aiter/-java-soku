package com.youku.soku.newext.util.middleResource;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.sort.json.util.JSONUtil;
import com.youku.search.sort.servlet.search_page.util.NovaMiddleResource;

public class MiddleResourceUtil {
	public static final Log logger = LogFactory
			.getLog(MiddleResourceUtil.class);
	
	public static final String SHOW_FILEDS = "showkeyword showcategory showid pk_odshow showname  showalias showtotal_vv showtotal_comment tv_genre movie_genre  variety_genre anime_genre  "
		+ " releasedate releaseyear releasemonth  update_notice  reputation showsum_vv showday_vv showweek_vv "
		+ " director performer host area voice  showdesc avg_rating  show_thumburl  show_vthumburl anime_edition"
		+ " completed  episode_collected  firstepisode_videourl language showdesc station tv_station  streamtypes sortmode paid hasvideotype";

	//http://10.101.19.33/
	//show.show?
	//q=showcategory%3A%E7%94%B5%E8%A7%86%E5%89%A7&fc=&fd=&pn=1&pl=10&
	//	ob=showweek_vv%3Adesc&ft=json&cl=test_page&h=1
	public static void main(String[] args) throws JSONException {
		DOMConfigurator
				.configure("E:/workspace/Ext/WebRoot/WEB-INF/local-conf/log4j.xml");

//		String name = "周星驰";
//		StringBuffer query = new StringBuffer();
//
//		query.append(name);
//
//		Map<String, String> params = new LinkedHashMap<String, String>();
//		params.put("fd", "thumburl");
//		params.put("pn", "1");
//		params.put("pl", "10");
//		params.put("cl", "test_page");
//		params.put("ft", "json");
//
//		//	   &fd=showname area avg_rating director tv_genre 返回数据类型字段
//		//	   String resourceType,	String resourceSubType, String query, Map<String, String> params) {
//		JSONObject result = NovaMiddleResource.search("person", null, query
//				.toString(), params);
//
//		//	   JSONObject videoResultObject = NovaMiddleResource.search(
//		//				"video", "show", query, params);
//
//		if (result != null) {
//			try {
//				result = JSONUtil.getProperty(result, JSONArray.class,
//						"results").getJSONObject(0);
//				System.out.println("the result:" + result);
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else {
//			System.out.println("error!!!");
//		}
//		String fields="persondesc   gender  birthday  height  bloodtype nationality thumburl";
//		
//		JSONObject tmp=getPerson(name,fields);
//		System.out.println(tmp.toString()); 差人大佬博命仔
		
		int showid=41938;
		String fields=" completed releasedate";
		System.out.println(getProgrammeById(showid, fields).toString(4));
		System.out.println("over");

	}

	/**
	 * @param fiedls
	 * @param showid
	 * @return
	 */
	public static JSONObject getProgrammeById(int showid, String fields) {
		StringBuffer query = new StringBuffer();
		query.append("showid:" + showid);
		Map<String, String> params = new LinkedHashMap<String, String>();
		//	   params.put("fc", "");
		params.put("fd", fields);
		params.put("pn", "1");
		params.put("pl", "10");
		//		params.put("ob", "showweek_vv:desc");
		params.put("cl", "search_result");
		params.put("ft", "json");

		JSONObject result = new JSONObject();
		logger.debug("获取中间层数据  showId:" + showid + "   fields:" + fields);
		result = NovaMiddleResource.search("show", null, query.toString(),
				params);
		if (result != null){
			JSONArray jsonArr=(JSONArray)JSONUtil.getProperty(result, JSONArray.class,"results");
			if(jsonArr==null) return null;
			result = jsonArr.optJSONObject(0);
			
		}
			

		return result;
	}
	
	
	/**
	 * @param fiedls
	 * @param showid
	 * @return
	 */
	public static JSONArray getProgrammeByIds(int startShowId, int endShowId, int length) {
		
		
		StringBuffer query = new StringBuffer();
		query.append("showid:" + startShowId + "-" + endShowId);
		Map<String, String> params = new LinkedHashMap<String, String>();
		//	   params.put("fc", "");
		params.put("fd", SHOW_FILEDS);
		params.put("pn", "1");
		params.put("pl", length + "");
		//		params.put("ob", "showweek_vv:desc");
		params.put("cl", "search_result");
		params.put("ft", "json");

		JSONObject jsonObject = new JSONObject();
		logger.debug("获取中间层数据  showIds:" + startShowId + "-" + endShowId + "   fields:" + SHOW_FILEDS);
		jsonObject = NovaMiddleResource.search("show", null, query.toString(),
				params);
		
		if (jsonObject == null || jsonObject.isNull("total")) {
			for (int i = 0; i < 3; i++) {// 在线请求使用，请求2次
				jsonObject = NovaMiddleResource.search("show", null, query.toString(),
						params);
				if (jsonObject != null && !jsonObject.isNull("total")) {
					break;
				}
			}
		}
		
		if (jsonObject != null){
			JSONArray result=(JSONArray)JSONUtil.getProperty(jsonObject, JSONArray.class,"results");
			return result;
		}
			

		return null;
	}
	
	/**
	 * 查询节目花絮,预告片, 按total_vv排序，取前两个. 如果个数少于2个，不显示
	 * 
	 * @param showid
	 * @return
	 */
	public static JSONArray searchTidbitsAndTrailer(int startShowId, int endShowId, int length) {
		StringBuilder query = new StringBuilder("show_id:");
		query.append(startShowId + "-" + endShowId);
		query.append(" show_videotype:花絮,预告片 state:normal");
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", length + "");

		String fd_value = "show_id videoid title streamtypes thumburl seconds show_videotype";
		params.put("fd", fd_value);
		params.put("ob", "total_vv:desc");

		
		// 如果中间层，获取失败{超时，内部出错}，重试3次，结果为空不算超时
		JSONObject jsonObject = NovaMiddleResource.search("video", "show", query.toString(), params);
		if (jsonObject == null || jsonObject.isNull("total")) {
			for (int i = 0; i < 3; i++) {// 在线请求使用，请求2次
				jsonObject = NovaMiddleResource.search("video", "show", query.toString(), params);
				if (jsonObject != null && !jsonObject.isNull("total")) {
					break;
				}
			}
		}
		if (jsonObject != null) {
			JSONArray result = jsonObject.optJSONArray("results");
			if (result != null && result.length() >= 2) {
				return result;
			}
		}

		return null;
	}
	
	
	/**
	 * 查询节目语言版本，以第一集为准
	 * 
	 * @param showid
	 * @return
	 */
	public static JSONArray searchVideoLanguage(int startShowId, int endShowId, int length) {
		StringBuilder query = new StringBuilder("show_id:");
		query.append(startShowId + "-" + endShowId);
		query.append(" state:normal");
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", length + "");

		String fd_value = "show_id videoid audiolang";
		params.put("fd", fd_value);
		params.put("ob", "show_videoseq:asc");

		
		// 如果中间层，获取失败{超时，内部出错}，重试3次，结果为空不算超时
		JSONObject jsonObject = NovaMiddleResource.search("video", "show", query.toString(), params);
		if (jsonObject == null || jsonObject.isNull("total")) {
			for (int i = 0; i < 3; i++) {// 在线请求使用，请求2次
				jsonObject = NovaMiddleResource.search("video", "show", query.toString(), params);
				if (jsonObject != null && !jsonObject.isNull("total")) {
					break;
				}
			}
		}
		if (jsonObject != null) {
			JSONArray result = jsonObject.optJSONArray("results");
			if (result != null && result.length() >= 2) {
				return result;
			}
		}

		return null;
	}
	/**
	 * 查找节目看点
	 * @param show_id
	 * @return
	 */
	public static JSONArray searchVideoPoint(int startShowId, int endShowId, int length) {
		StringBuilder query = new StringBuilder("show_id:");
		query.append(startShowId + "-" + endShowId);
		query.append(" show_videotype:正片 state:normal haspoint:1");
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", length + "");

		String fd_value = "show_id point";
		params.put("fd", fd_value);
		params.put("ob", "total_vv:desc");

		// 如果中间层，获取失败{超时，内部出错}，重试3次，结果为空不算超时
		JSONObject jsonObject = NovaMiddleResource.search("video", "show", query.toString(), params);
		if (jsonObject == null || jsonObject.isNull("total")) {
			for (int i = 0; i < 3; i++) {// 在线请求使用，请求2次
				jsonObject = NovaMiddleResource.search("video", "show", query.toString(), params);
				if (jsonObject != null && !jsonObject.isNull("total")) {
					break;
				}
			}
		}
		if (jsonObject != null) {
			JSONArray result = jsonObject.optJSONArray("results");
			if (result != null) {
				return result;
			}
		}

		return null;
	}
	
	/**
	 * 查找节目看点
	 * @param show_id
	 * @return
	 */
	public static JSONArray searchVideoGuest(int startShowId, int endShowId, int length) {
		StringBuilder query = new StringBuilder("show_id:");
		query.append(startShowId + "-" + endShowId);
		query.append(" show_videotype:正片 state:normal category:综艺");
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", length + "");

		String fd_value = "show_id title guest show_videostage";
		params.put("fd", fd_value);
		params.put("ob", "total_vv:desc");

		// 如果中间层，获取失败{超时，内部出错}，重试3次，结果为空不算超时
		JSONObject jsonObject = NovaMiddleResource.search("video", "show", query.toString(), params);
		if (jsonObject == null || jsonObject.isNull("total")) {
			for (int i = 0; i < 3; i++) {// 在线请求使用，请求2次
				jsonObject = NovaMiddleResource.search("video", "show", query.toString(), params);
				if (jsonObject != null && !jsonObject.isNull("total")) {
					break;
				}
			}
		}
		if (jsonObject != null) {
			JSONArray result = jsonObject.optJSONArray("results");
			if (result != null) {
				return result;
			}
		}

		return null;
	}


	public static JSONObject getPerson(String elePerson,String fields) {
		StringBuffer query = new StringBuffer();
		query.append(elePerson);
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("fd", fields);
		params.put("pn", "1");
		params.put("pl", "10");
		params.put("cl", "search_result");
		params.put("ft", "json");
		JSONObject result = NovaMiddleResource.search("person", null, query
				.toString(), params);

		if (result != null) {
			JSONArray jsonArr=(JSONArray)JSONUtil.getProperty(result, JSONArray.class,"results");
			if(jsonArr==null) return null;
			result = jsonArr.optJSONObject(0);
			return result;
		} else {
			logger.error("查询人物中间层失败："+elePerson);
		}
		return null;

	}
	
	public static JSONObject getPersonById(int personId,String fields) {
		StringBuffer query = new StringBuffer();
		query.append("personid:").append(personId);
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("fd", fields);
		params.put("pn", "1");
		params.put("pl", "10");
		params.put("cl", "search_result");
		params.put("ft", "json");
		JSONObject result = NovaMiddleResource.search("person", null, query
				.toString(), params);

		if (result != null) {
			JSONArray jsonArr=(JSONArray)JSONUtil.getProperty(result, JSONArray.class,"results");
			if(jsonArr==null) return null;
			result = jsonArr.optJSONObject(0);
			return result;
		} else {
			logger.error("查询人物中间层失败："+personId);
		}
		return null;

	}
	
	public static JSONArray getPersonByIds(int startId, int endId, int length) {
		String fields="pk_odperson persondesc personname personalias gender  birthday homeplace height  bloodtype nationality occupation thumburl state total_vv";
		StringBuffer query = new StringBuffer();
		query.append("state:normal personid:").append(startId).append("-").append(endId);
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("fd", fields);
		params.put("pn", "1");
		params.put("pl", length + "");
		params.put("cl", "search_result");
		params.put("ft", "json");
		JSONObject result = NovaMiddleResource.search("person", null, query
				.toString(), params);

		if (result != null) {
			JSONArray jsonArr=(JSONArray)JSONUtil.getProperty(result, JSONArray.class,"results");
			if(jsonArr==null) return null;
			return jsonArr;
		} else {
			logger.error("查询人物中间层失败："+startId + "-" + endId);
		}
		return null;

	}
	
	
	/**
	 * 获取最小、最大的id
	 * @return
	 */
	public static int[] getPersonMinMaxIDs() {
		int[]ids = new int[]{1,500000};
		
		//最小ID
		int minId = getPersonID("personid:asc");
		if(minId>0){
			ids[0]=minId;
		}
		
		//最大ID
		int maxId = getPersonID("personid:desc");
		if(maxId>0){
			ids[1]=maxId;
		}
		return ids;
	}

	/**
	 * @param string
	 * @return
	 */
	private static int getPersonID(String ob) {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", "1");

		params.put("ob", ob);
		params.put("cl", "search_result");

		JSONObject jsonObject = NovaMiddleResource.search("person", null, "", params);
		if(jsonObject==null || jsonObject.isNull("results")){
			for (int i = 0; i < 5; i++) {
				jsonObject = NovaMiddleResource
				.search("person", null, "", params);
				if (jsonObject!=null && !jsonObject.isNull("results")) {
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		JSONArray showArray = jsonObject.optJSONArray("results");
		if (!JSONUtil.isEmpty(showArray)) {
			return showArray.optJSONObject(0).optInt("personid");
		}
		return 0;
	}

}
