/**
 * 
 */
package com.youku.soku.newext.searcher;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.manage.util.ImageUtil;
import com.youku.soku.newext.util.JSONUtil;

/**
 * @author liuyunjian
 * 2011-5-11
 */
public class BaseSearcher {

	/**
	 * @param programme
	 * @param middJson 
	 * @return
	 */
	public static JSONObject getProgrammeJson(Programme programme, JSONObject middJson) {
		JSONObject programmeJson = new JSONObject();
		if(programme!=null){
			try {
				programmeJson.put("name", StringUtils.trimToEmpty(programme.getName()));
				programmeJson.put("contentId", programme.getContentId());
				programmeJson.put("episodeTotal", programme.getEpisodeTotal());
				programmeJson.put("programmeId", programme.getId());
				programmeJson.put("updateTime", programme.getUpdateTime());
				programmeJson.put("alias", programme.getAlias());
				programmeJson.put("url", programme.getPlayUrl());
			} catch (JSONException e) {
			}
		}
		if(middJson!=null){
			try {
				programmeJson.put("pic", PeopleSearcher.getLogo(middJson.optString("show_thumburl")));
				programmeJson.put("vpic", middJson.optString("show_vthumburl").length()==0?ImageUtil.getDisplayUrl("0900641F464A7BBE7400000000000000000000-0000-0000-0000-00005B2F7B0E"):middJson.optString("show_vthumburl"));
				programmeJson.put("brief", StringUtils.trimToEmpty(middJson.optString("showdesc")));
				double reputation = middJson.optDouble("reputation",0);
				if(reputation<1){
					reputation = 5.0;
				}
				programmeJson.put("score", reputation);
				programmeJson.put("performer",middJson.optJSONArray("performer"));
				programmeJson.put("releaseyear", middJson.optString("releaseyear"));
				JSONArray stations=middJson.optJSONArray("tv_station");
				JSONArray outStations=new JSONArray();
				if(stations!=null && stations.length()>0){
					for(int i=0;i<stations.length();i++	){
						outStations.put(stations.getJSONObject(i).optString("name"));
					}
				}
				programmeJson.put("station", outStations);
				
			} catch (JSONException e) {
			}
		}
		return programmeJson;
	}

	/**
	 * @param cate 
	 * @param middleResourceStr
	 * @return
	 */
	public static JSONObject getMiddleJson(JSONObject middJson, int cate) {
		JSONObject tmp = new JSONObject();
		try {
			tmp.put("area", middJson.optJSONArray("area"));
			tmp.put("director", middJson.optJSONArray("director"));
			tmp.put("host", middJson.optJSONArray("host"));
			switch (cate) {
			case 1:
				tmp.put("genre", middJson.optJSONArray("tv_genre"));
				break;
			case 2:
				tmp.put("genre", middJson.optJSONArray("movie_genre"));
				break;
			case 3:
				tmp.put("genre", middJson.optJSONArray("variety_genre"));
				break;
			case 5:
				tmp.put("genre", middJson.optJSONArray("anime_genre"));
				break;
			default:
				break;
			}
			
			tmp.put("showname", middJson.optString("showname"));
		} catch (JSONException e) {
		}
		return tmp;
	}

	/**
	 * @param episodeList
	 * @param eleSite 
	 * @return
	 */
	public static JSONArray getEpisodesJsonArray(
			List<ProgrammeEpisode> episodeList, JSONObject eleSite) {
		JSONArray episodeArr = new JSONArray();
		try {
			if (episodeList != null && episodeList.size() > 0) {
				for (ProgrammeEpisode episode : episodeList) {
					JSONObject eleJson = new JSONObject();
					
	//				有播放地址的影片才加入
					String url=StringUtils.trimToEmpty(episode
							.getUrl());
					if(url==null || url.length()<=0) continue;
						eleJson.put("url", url);
					
					eleJson.put("name", StringUtils.trimToEmpty(episode
							.getTitle()));
					eleJson.put("seconds", episode.getSeconds());
					
					eleJson.put("orderId", episode.getOrderId());
					eleJson.put("orderStage", episode.getOrderStage());
					
					episodeArr.put(eleJson);
				}
				eleSite.put("episodes", episodeArr);
			}
		} catch (JSONException e) {
		}
		return episodeArr;
	}

	/**
	 * @param programmeSiteArr
	 * @return
	 */
	public static String getPlayUrl(JSONObject programmeSiteArr) {
		if(JSONUtil.isEmpty(programmeSiteArr)){
			return "";
		}
		String firstKey = (String) programmeSiteArr.keys().next();
		JSONArray episodeArr = programmeSiteArr.optJSONObject(firstKey).optJSONArray("episodes");
		if(JSONUtil.isEmpty(episodeArr)){
			return "";
		}
		
		return episodeArr.optJSONObject(0).optString("url");
	}

}
