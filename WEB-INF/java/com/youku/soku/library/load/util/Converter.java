/**
 * 
 */
package com.youku.soku.library.load.util;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.sort.json.util.JSONUtil;
import com.youku.soku.library.load.douban.DoubanWget;
import com.youku.soku.library.load.form.ProgrammeBo;
import com.youku.soku.library.load.form.ProgrammeDoubanBo;
import com.youku.soku.library.load.form.ProgrammeEpisodeBo;
import com.youku.soku.library.load.form.ProgrammeSiteBo;

/**
 * @author liuyunjian
 * 2011-2-22
 */
public class Converter {
	private static final Logger log = Logger.getLogger(Converter.class
			.getName());

	/**
	 * 转换节目
	 * @param json
	 * @param cateMap
	 * @return
	 */
	public static ProgrammeBo convertProgramme(JSONObject json,
			Map<String, Integer> cateMap) {
		if(json==null) {
			return null;
		}
		
		if(json.isNull("pk_odshow")||json.isNull("showname")||json.isNull("showcategory")||json.isNull("episode_total")){
			log.error("中间层 by id ---> pk_odshow or showname or showcategory or episode_total is null");
			return null;
		}
		int cate = 0;
		if(cateMap!=null){
			Integer tmp = cateMap.get(json.optString("showcategory"));
			if(tmp!=null){
				cate = tmp;
			}
		}
		ProgrammeBo programmeBo = new ProgrammeBo();
		programmeBo.contentId = json.optInt("pk_odshow");
		programmeBo.name = json.optString("showname");
		programmeBo.alias = getString(json.optJSONArray("showalias"));
		programmeBo.cate = cate;
		programmeBo.state = json.optString("state");
		programmeBo.episodeTotal = json.optInt("episode_total");
		programmeBo.source = SourceUtil.parse(json.optString("copyright_status"));
		
		return programmeBo;
	}
	/**
	 * @param optJSONArray
	 * @return
	 */
	private static String getString(JSONArray optJSONArray) {
		if(JSONUtil.isEmpty(optJSONArray)){
			return null;
		}
		StringBuilder sbBuilder = new StringBuilder();
		for (int i = 0; i < optJSONArray.length(); i++) {
			sbBuilder.append(optJSONArray.optString(i)).append("|");
		}
		if(sbBuilder.length()>1){
			return sbBuilder.substring(0,sbBuilder.length()-1);
		}
		return sbBuilder.toString();
	}
	/**
	 * 通过show_id,从中间层查找show信息 
	 */
	public static ProgrammeBo convertProgramme(JSONObject json,
			Map<String, Integer> cateMap,boolean needGetShow) {
		if(json==null) {
			log.error("中间层 show json is null");
			return null;
		}
		
		if(json.isNull("show_id")||json.optInt("show_id")<=0){
			log.error("中间层 show_id is null");
			return null;
		}
		
		//通过单个show_id，查询节目
		JSONObject showResultObject = SyncUtil.buildProgrammeByID(json.optInt("show_id"));
		
		if (showResultObject == null || showResultObject.isNull("total")) {
			log.error("中间层 by id ---> show is null");
			return null;
		}

		int totalNum = showResultObject.optInt("total");
		if(totalNum<=0) {
//			log.error("中间层 by id ---> total==0  "+json.optInt("show_id"));
			return null;
		}
		if (totalNum > 0) {
			JSONArray showArray = showResultObject.optJSONArray("results");
			if (JSONUtil.isEmpty(showArray)) {
				log.error("中间层 by id ---> results.length==0");
				return null;
			}
			
			//取返回结果的第一个节目进行转换
			JSONObject tmp = showArray.optJSONObject(0);
			for (Iterator iterator = tmp.keys(); iterator.hasNext();) {
				String key = (String) iterator.next();
				try {
					json.put(key, tmp.opt(key));
				} catch (JSONException e) {
				}
			}
			return convertProgramme(json,cateMap);
		}
		
		return null;
	}
	
	/**
	 * 转换节目-站点
	 * @param json
	 * @return
	 */
	public static ProgrammeSiteBo convertProgrammeSite(JSONObject json) {
		if(json==null) {
			return null;
		}
		
		ProgrammeSiteBo psBo = new ProgrammeSiteBo();
		psBo.firstLogo=json.optString("firstepisode_thumburl");
		psBo.midEmpty=json.optInt("missvideo");
		psBo.episodeCollected=json.optInt("episode_collected");
		psBo.completed=(json.optInt("completed")==1 && psBo.episodeCollected==json.optInt("episode_total"))?1:0;
		
		return psBo;
	}
	
	/**
	 * 转换节目-视频剧集
	 * @param json
	 * @return
	 */
	public static ProgrammeEpisodeBo convertProgrammeEpisode(JSONObject json) {
		if(json==null) {
			return null;
		}
		
		ProgrammeEpisodeBo peBo = new ProgrammeEpisodeBo();
		peBo.title = json.optString("title");
		peBo.url = "http://v.youku.com/v_show/id_"+json.optString("videoid")+".html";
		peBo.seconds = json.optDouble("seconds");
		peBo.logo = json.optString("thumburl");
		peBo.orderId = json.optInt("show_videoseq");
		peBo.orderStage = json.optInt("show_videostage",0);
		if(peBo.orderStage==0){
			peBo.orderStage = peBo.orderId;
		}
		JSONArray streamtypes = json.optJSONArray("streamtypes");
		if (JSONUtil.contain(streamtypes, "hd")) {
			peBo.hd = 1;
		}
		
		
		return peBo;
	}
	/**
	 * @param programmeBo
	 * @param item
	 * @return
	 */
	public static ProgrammeDoubanBo convertDoubanProgramme(
			ProgrammeBo programmeBo, JSONObject item) {
		if(programmeBo==null || JSONUtil.isEmpty(item)){
			return null;
		}
		ProgrammeDoubanBo pdBo = new ProgrammeDoubanBo();
		pdBo.pId = programmeBo.contentId;
		pdBo.pName = programmeBo.name;
		pdBo.doubanId = DoubanWget.getDoubanId(DoubanWget.getAttrBySearchItem(item, "id"));
		pdBo.doubanName = DoubanWget.getTitle(item);
		pdBo.cate = programmeBo.cate;
		return pdBo;
	}

}
