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

public class MiddleResourceUtil {
	public static final Log logger = LogFactory
			.getLog(MiddleResourceUtil.class);

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

}
