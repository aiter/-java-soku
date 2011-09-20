/**
 * 
 */
package com.youku.soku.library.load.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.console.util.Wget;
import com.youku.search.sort.json.util.JSONUtil;
import com.youku.search.sort.servlet.search_page.util.NovaMiddleResource;
import com.youku.soku.library.Utils;

/**
 * 工具类，主要组装中间层的url
 * @author liuyunjian
 * 2011-2-22
 */
public class SyncUtil {
//	private final static String SYNC_HOST = "10.101.2.208";
	private final static String SYNC_HOST = "10.103.12.72";
	private final static String SEARCH_HOST = "10.103.88.54";
	private final static String SYNC_CALLER = "search_out";
	private final static String SYNC_CATEGORY = "showcategory:电视剧,电影,动漫,综艺,体育";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			JSONObject json  = buildEpisode(89063);
			json.toString(4);
			System.out.println(json.optString("total"));
//			System.out.println(buildProgramme(1, 10).toString(4));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过<b style="color:green;">最新更新</b>获取<b style="color:red;">节目</b>Json对象 
	 */
	public static JSONObject buildProgramme(int page,int length) {
		if(length>1000) length =1000;
		if(length<1) length =1;
		String query = SYNC_CATEGORY;
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", Integer.toString(page));
		params.put("pl", Integer.toString(length));

		String fd_value = "pk_odshow showname showalias showcategory episode_total copyright_status state"
						+" firstepisode_thumburl completed missvideo episode_collected showlastupdate";
		params.put("fd", fd_value);
		params.put("ob", "showlastupdate:desc");
		params.put("cl", SYNC_CALLER);

		//如果中间层，获取失败{超时，内部出错}，重试3次，结果为空不算超时
		JSONObject jsonObject = NovaMiddleResource
		.search(SYNC_HOST,"show","show", query, params);
		if(jsonObject==null || jsonObject.isNull("total")){
			for (int i = 0; i < 3; i++) {
//				System.out.println("re-try:"+(i+1));
				jsonObject = NovaMiddleResource
				.search(SYNC_HOST,"show","show", query, params);
				if (jsonObject!=null && !jsonObject.isNull("total")) {
					return jsonObject;
				}
			}
		}
		return jsonObject;
	}
	
	/**
	 * 更新<b style="color:green;">没有收录完成</b>的节目获取<b style="color:red;">节目</b>Json对象 
	 */
	public static JSONObject buildNotCompleteProgramme(int page,int length) {
		if(length>1000) length =1000;
		if(length<1) length =1;
		String query = SYNC_CATEGORY + " complete:0";
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", Integer.toString(page));
		params.put("pl", Integer.toString(length));

		String fd_value = "pk_odshow showname showalias showcategory episode_total copyright_status state"
						+" firstepisode_thumburl completed missvideo episode_collected showlastupdate";
		params.put("fd", fd_value);
		params.put("ob", "showweek_vv:desc");
		params.put("cl", SYNC_CALLER);

		//如果中间层，获取失败{超时，内部出错}，重试3次，结果为空不算超时
		JSONObject jsonObject = NovaMiddleResource
		.search(SYNC_HOST,"show","show", query, params);
		if(jsonObject==null || jsonObject.isNull("total")){
			for (int i = 0; i < 3; i++) {
//				System.out.println("re-try:"+(i+1));
				jsonObject = NovaMiddleResource
				.search(SYNC_HOST,"show","show", query, params);
				if (jsonObject!=null && !jsonObject.isNull("total")) {
					return jsonObject;
				}
			}
		}
		return jsonObject;
	}
	
	/**
	 * 通过<b style="color:green;">id范围</b>获取<b style="color:red;">节目</b>Json对象 
	 */
	public static JSONObject buildProgrammeID(int start,int end,int length) {
		
//		String query = "showid:"+start+"-"+end+SYNC_CATEGORY;
		StringBuilder query = new StringBuilder("showid:");
		query.append(start).append("-").append(end)
		.append(" ").append(SYNC_CATEGORY);
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", Integer.toString(length));

		String fd_value = "pk_odshow showname showalias showcategory episode_total copyright_status state"
						+" firstepisode_thumburl completed missvideo episode_collected";
		params.put("fd", fd_value);
		params.put("ob", "showlastupdate:desc");
		params.put("cl", SYNC_CALLER);

		//如果中间层，获取失败{超时，内部出错}，重试3次，结果为空不算超时
		JSONObject jsonObject = NovaMiddleResource
		.search(SYNC_HOST,"show","show", query.toString(), params);
		if(jsonObject==null || jsonObject.isNull("total")){
			for (int i = 0; i < 3; i++) {
//				System.out.println("re-try:"+(i+1));
				jsonObject = NovaMiddleResource
				.search(SYNC_HOST,"show","show", query.toString(), params);
				if (jsonObject!=null && !jsonObject.isNull("total")) {
					return jsonObject;
				}
			}
		}
		return jsonObject;
	}
	public static JSONObject buildProgrammeByID(int showid) {
		
//		String query = "showid:"+start+"-"+end+SYNC_CATEGORY;
		StringBuilder query = new StringBuilder("showid:");
		query.append(showid).append(" ").append(SYNC_CATEGORY);
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", "1");

		String fd_value = "pk_odshow showname showalias showcategory episode_total copyright_status state"
						+" firstepisode_thumburl completed missvideo episode_collected";
		params.put("fd", fd_value);
		params.put("ob", "showlastupdate:desc");
		params.put("cl", SYNC_CALLER);

		//如果中间层，获取失败{超时，内部出错}，重试3次，结果为空不算超时
		JSONObject jsonObject = NovaMiddleResource
		.search(SYNC_HOST,"show","show", query.toString(), params);
		if(jsonObject==null || jsonObject.isNull("total")){
			for (int i = 0; i < 3; i++) {
//				System.out.println("re-try:"+(i+1));
				jsonObject = NovaMiddleResource
				.search(SYNC_HOST,"show","show", query.toString(), params);
				if (jsonObject!=null && !jsonObject.isNull("total")) {
					return jsonObject;
				}
			}
		}
		return jsonObject;
	}
	public static JSONObject buildProgrammePageByID(int showid) {
		StringBuilder query = new StringBuilder("showid:");
		query.append(showid);
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", "1");

		String fd_value = "pk_odshow showname showtotal_up showtotal_down avg_rating douban_num";
		params.put("fd", fd_value);
		params.put("cl", SYNC_CALLER);

		//如果中间层，获取失败{超时，内部出错}，重试3次，结果为空不算超时
		JSONObject jsonObject = NovaMiddleResource
		.search(SEARCH_HOST,"show","show", query.toString(), params);
		if(jsonObject==null || jsonObject.isNull("total")){
			for (int i = 0; i < 2; i++) {//在线请求使用，请求2次
				jsonObject = NovaMiddleResource
				.search(SEARCH_HOST,"show","show", query.toString(), params);
				if (jsonObject!=null && !jsonObject.isNull("total")) {
					return jsonObject;
				}
			}
		}
		return jsonObject;
	}
	public static JSONObject buildProgrammePageVideoByID(int showid) {
		StringBuilder query = new StringBuilder("show_id:");
		query.append(showid);
		query.append(" show_videotype:花絮,MV,资讯,预告片 state:normal");
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", "1000");

		String fd_value = "videoid title streamtypes thumburl seconds show_videotype";
		params.put("fd", fd_value);
		params.put("ob", "createtime:desc");
		params.put("cl", SYNC_CALLER);

		//如果中间层，获取失败{超时，内部出错}，重试3次，结果为空不算超时
		JSONObject jsonObject = NovaMiddleResource
		.search(SEARCH_HOST,"video","show", query.toString(), params);
		if(jsonObject==null || jsonObject.isNull("total")){
			for (int i = 0; i < 2; i++) {//在线请求使用，请求2次
				jsonObject = NovaMiddleResource
				.search(SEARCH_HOST,"show","show", query.toString(), params);
				if (jsonObject!=null && !jsonObject.isNull("total")) {
					return jsonObject;
				}
			}
		}
		return jsonObject;
	}
	public static JSONObject buildProgrammeDoubanInfoByID(int showid) {
		
//		String query = "showid:"+start+"-"+end+SYNC_CATEGORY;
		StringBuilder query = new StringBuilder("showid:");
		query.append(showid);
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", "1");

		String fd_value = "showname showalias showcategory area language person director starring performer character releaseyear douban_num imdb_num";
		params.put("fd", fd_value);
		params.put("ob", "showlastupdate:desc");
		params.put("cl", SYNC_CALLER);

		//如果中间层，获取失败{超时，内部出错}，重试3次，结果为空不算超时
		JSONObject jsonObject = NovaMiddleResource
		.search(SYNC_HOST,"show","show", query.toString(), params);
		if(jsonObject==null || jsonObject.isNull("total")){
			for (int i = 0; i < 3; i++) {
//				System.out.println("re-try:"+(i+1));
				jsonObject = NovaMiddleResource
				.search(SYNC_HOST,"show","show", query.toString(), params);
				if (jsonObject!=null && !jsonObject.isNull("total")) {
					return jsonObject;
				}
			}
		}
		return jsonObject;
	}
	
	public static JSONObject buildProgrammeKeywordsByID(int showid) {
		
//		String query = "showid:"+start+"-"+end+SYNC_CATEGORY;
		StringBuilder query = new StringBuilder("showid:");
		query.append(showid);
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", "1");

		String fd_value = "showname showalias showsubtitle showkeyword series";
		params.put("fd", fd_value);
		params.put("cl", SYNC_CALLER);

		//如果中间层，获取失败{超时，内部出错}，重试3次，结果为空不算超时
		JSONObject jsonObject = NovaMiddleResource
		.search(SYNC_HOST,"show","show", query.toString(), params);
		if(jsonObject==null || jsonObject.isNull("total")){
			for (int i = 0; i < 3; i++) {
//				System.out.println("re-try:"+(i+1));
				jsonObject = NovaMiddleResource
				.search(SYNC_HOST,"show","show", query.toString(), params);
				if (jsonObject!=null && !jsonObject.isNull("total")) {
					return jsonObject;
				}
			}
		}
		return jsonObject;
	}
	
	
	/**
	 * 通过<b style="color:green;">节目id</b>获取<b style="color:red;">节目视频</b>Json对象 
	 */
	public static JSONObject buildEpisode(int programmeId) {
		
//		String query = "showid:"+start+"-"+end+SYNC_CATEGORY;
		StringBuilder query = new StringBuilder("show_id:");
		query.append(programmeId)
		.append(" state:normal show_videotype:正片");
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", "1000");

		String fd_value = "videoid title streamtypes seconds thumburl show_videoseq show_videostage ";
		params.put("fd", fd_value);
		params.put("ob", "videoid:asc");
		params.put("cl", SYNC_CALLER);

		//如果中间层，获取失败{超时，内部出错}，重试3次，结果为空不算超时
		JSONObject jsonObject = NovaMiddleResource
		.search(SYNC_HOST,"video","show", query.toString(), params);
		if(jsonObject==null || jsonObject.isNull("total")){
			for (int i = 0; i < 3; i++) {
//				System.out.println("re-try:"+(i+1));
				jsonObject = NovaMiddleResource
				.search(SYNC_HOST,"video","show", query.toString(), params);
				if (jsonObject!=null && !jsonObject.isNull("total")) {
					return jsonObject;
				}
			}
		}
		return jsonObject;
	}
	
	/**
	 * 通过<b style="color:green;">最新更新</b>获取<b style="color:red;">节目-视频所属的节目ID</b>Json对象 
	 * <br/>只查询
	 */
	public static JSONObject buildEpisode(int page,int length) {
		if(length>1000) length =1000;
		if(length<1) length =1;
		String query = "show_videotype:正片";
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", Integer.toString(page));
		params.put("pl", Integer.toString(length));

		String fd_value = "show_id show_collecttime ";
		params.put("fd", fd_value);
		params.put("ob", "show_collecttime:desc");
		params.put("cl", SYNC_CALLER);

		//如果中间层，获取失败{超时，内部出错}，重试3次，结果为空不算超时
		JSONObject jsonObject = NovaMiddleResource
		.search(SYNC_HOST,"video","show", query, params);
		if(jsonObject==null || jsonObject.isNull("total")){
			for (int i = 0; i < 3; i++) {
//				System.out.println("re-try:"+(i+1));
				jsonObject = NovaMiddleResource
				.search(SYNC_HOST,"video","show", query, params);
				if (jsonObject!=null && !jsonObject.isNull("total")) {
					return jsonObject;
				}
			}
		}
		return jsonObject;
	}
	
	public static JSONObject buildPerson(String personName) {
		String query = "state:normal personname:"+personName;
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", "1");

		String fd_value = "pk_odperson personname personalias thumburl gender birthday homeplace height bloodtype nationality occupation persondesc ";
		params.put("fd", fd_value);
		params.put("cl", SYNC_CALLER);

		//如果中间层，获取失败{超时，内部出错}，重试3次，结果为空不算超时
		JSONObject jsonObject = NovaMiddleResource
		.search(SEARCH_HOST,"person","person", query, params);
		if(jsonObject==null || jsonObject.isNull("total")){
			for (int i = 0; i < 3; i++) {
//				System.out.println("re-try:"+(i+1));
				jsonObject = NovaMiddleResource
				.search(SEARCH_HOST,"person","person", query, params);
				if (jsonObject!=null && !jsonObject.isNull("total")) {
					return jsonObject;
				}
			}
		}
		return jsonObject;
	}

	/**
	 * 获取最小、最大的id
	 * @return
	 */
	public static int[] getMinMaxIDs() {
		int[]ids = new int[]{1,500000};
		
		//最小ID
		int minId = getID("showid:asc");
		if(minId>0){
			ids[0]=minId;
		}
		
		//最大ID
		int maxId = getID("showid:desc");
		if(maxId>0){
			ids[1]=maxId;
		}
		return ids;
	}

	/**
	 * @param string
	 * @return
	 */
	private static int getID(String ob) {
		String query = SYNC_CATEGORY;
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", "1");

		params.put("ob", ob);
		params.put("cl", SYNC_CALLER);

		JSONObject jsonObject = NovaMiddleResource.search(SYNC_HOST,"show","show", query, params);
		if(jsonObject==null || jsonObject.isNull("results")){
			for (int i = 0; i < 3; i++) {
				jsonObject = NovaMiddleResource
				.search(SYNC_HOST,"show","show", query.toString(), params);
				if (jsonObject!=null && !jsonObject.isNull("results")) {
					break;
				}
			}
		}
		JSONArray showArray = jsonObject.optJSONArray("results");
		if (!JSONUtil.isEmpty(showArray)) {
			return showArray.optJSONObject(0).optInt("pk_odshow");
		}
		return 0;
	}
	
	/**
	 * 只用来验证中间层的结果是否为空
	 */
	public static boolean isEmpty(JSONObject jsonObject){
		if(JSONUtil.isEmpty(jsonObject)){
			return true;
		}
		
		JSONArray results = jsonObject.optJSONArray("results");
		if(JSONUtil.isEmpty(results)){
			return true;
		}
		
		return false;
	}
}
