package com.youku.soku.newext.searcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.Series;
import com.youku.soku.newext.info.DocumentaryInfo;
import com.youku.soku.newext.info.ExtInfoHolder;
import com.youku.soku.newext.util.ProgrammeSiteType;
import com.youku.soku.newext.util.StringUtil;

public class DocumentarySearcher {
	private static Log logger=LogFactory.getLog(DocumentarySearcher.class);

	/**
	 * 遇到异常可以返回null, 不应该throws. 确保其他频道检索的正常进行
	 * @param keyword
	 * @return
	 */
	public static JSONArray searchByName(String keyword,String site){

		if (keyword == null) {
			return null;
		}

		DocumentaryInfo info = ExtInfoHolder.getCurrentThreadLocal().documentaryInfo;

//		 检索系列map,如果有结果则直接返回，不用搜索name_programmeSite表
		List<Programme> programmeList = new ArrayList<Programme>();
		programmeList = info.series_programme.get(keyword.toLowerCase());
		
		JSONArray returnJsonArr = new JSONArray();
		if (programmeList != null && programmeList.size() > 0) {
			
			JSONObject eleJson = null;
			try {
				eleJson = genJson(programmeList.get(0), info,site);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("生成查询结果json数据失败");
				e.printStackTrace();
			}
			if (eleJson != null)
				returnJsonArr.put(eleJson);
		}
		
		if(returnJsonArr.length()>0) return returnJsonArr;

//		 检索info.name_programmeSite
		programmeList=info.name_programme.get(keyword.toLowerCase());
		if (programmeList != null && programmeList.size() > 0) {
			for (Programme programme : programmeList) {
				JSONObject eleJson = null;
				try {
					eleJson = genJson(programme, info,site);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("生成查询结果json数据失败");
					e.printStackTrace();
					continue;
				}
				if (eleJson != null)
					returnJsonArr.put(eleJson);

			}
			return returnJsonArr;
		}

		return null;
	}
	
public  static JSONObject genJson(Programme programme, DocumentaryInfo info,String site) throws Exception {
	JSONObject returnJson = new JSONObject();
	List<Integer> programmeSiteIdList=new ArrayList<Integer>();
	
	//综艺处理系列
	Series series=info.programme_series.get(programme);
	if(series!=null && series.getId()>0){
		JSONObject seriesJson=new JSONObject();
		seriesJson.put("id", series.getId());
		seriesJson.put("name",series.getName());
		seriesJson.put("currentProgramme", programme.getId());
		List<Programme> proList=info.series_programme.get(StringUtils.trimToEmpty(series.getName()));
		if(proList!=null && proList.size()>0){
			JSONArray proJsonArr=new JSONArray();
			for(Programme tmpPro: proList){
				if(tmpPro.getId()==programme.getId()) continue;
				JSONObject tmp=new JSONObject();
				tmp.put("id", tmpPro.getId());
				tmp.put("name", tmpPro.getName());
				String middleResourceStr = info.middMap.get(tmpPro.getContentId());
				JSONObject middJson=new JSONObject();
				if (middleResourceStr != null) {
					middJson=new JSONObject(middleResourceStr);
					tmp.put("showid", middJson.optString("showid"));
				}
				
				proJsonArr.put(tmp);
			}
		   seriesJson.put("programmes", proJsonArr);
		}
		
		returnJson.put("series", seriesJson);
	}
	
	//获取一个节目的中间层信息
	String middleResourceStr = info.middMap.get(programme.getContentId());
	JSONObject middJson=new JSONObject();
	if (middleResourceStr != null) {
		middJson=new JSONObject(middleResourceStr);
		JSONObject tmp = BaseSearcher.getMiddleJson(middJson,programme.getCate());
		
		returnJson.put("midd", tmp);
	}
	
	//从节目信息中获取programme信息
	JSONObject programmeJson = BaseSearcher.getProgrammeJson(programme,middJson);
	returnJson.put("programme", programmeJson);

	List<ProgrammeSite> programmeSiteList = info.programme_programmeSite
			.get(programme);
	Set<Integer> siteSet = StringUtil.parseSite(site);
	if (programmeSiteList != null && programmeSiteList.size() > 0) {
		JSONObject programmeSiteArr = new JSONObject();

		for (ProgrammeSite programmeSite : programmeSiteList) {
			if(siteSet!=null && !siteSet.contains(programmeSite.getSourceSite())){
				continue;
			}
			
			JSONObject eleSite = new JSONObject();
			eleSite.put("firstLogo", StringUtils.trimToEmpty(programmeSite
					.getFirstLogo()));
			if(programmeSite.getSourceSite()==ProgrammeSiteType.优酷网.getValue() && programmeSite.getCompleted() != 1){
				eleSite.put("streamtypes", middJson.optJSONArray("streamtypes")==null?"[]":middJson.optJSONArray("streamtypes"));
				programmeJson.put("update_notice", StringUtils.trimToEmpty(middJson.optString("update_notice")));
			}
			
			int episodeTotal=programme.getEpisodeTotal();
			List<ProgrammeEpisode> episodeList = info.programmeSite_episode
					.get(programmeSite);
			
			if (episodeList != null && episodeList.size() > 0) {
				String displayStatus= "";
				int orderStage=0;
				if(programmeSite.getSortmode()==0){
					displayStatus=TeleplaySearcher.getUpdateStatus(episodeTotal, episodeList.size(),episodeList.get(episodeList.size()-1));
					orderStage = episodeList.get(episodeList.size()-1).getOrderStage();
				}else {
					displayStatus=TeleplaySearcher.getUpdateStatus(episodeTotal, episodeList.size(),episodeList.get(0));
					orderStage = episodeList.get(0).getOrderStage();
				}
				eleSite.put("episode_last_stage", orderStage);
				eleSite.put("display_status", displayStatus);
			}
			
			JSONArray episodeArr = BaseSearcher.getEpisodesJsonArray(episodeList,eleSite);
			if(!com.youku.soku.newext.util.JSONUtil.isEmpty(episodeArr)){
				programmeSiteIdList.add(programmeSite.getSourceSite());
				programmeSiteArr.put(new Integer(programmeSite.getSourceSite()).toString(), eleSite);
			}
		}

		if(com.youku.soku.newext.util.JSONUtil.isEmpty(programmeSiteArr)){
			return null;
		}
		programmeJson.put("url",BaseSearcher.getPlayUrl(programmeSiteArr));
		returnJson.put("ProgrammeSite", programmeSiteArr);

		
		if(programmeSiteIdList!=null && programmeSiteIdList.size()>0){
//			处理结果
			List<String> tmpSiteList=new ArrayList<String>();
			
			for(int i=0;i<programmeSiteIdList.size();i++){
				
				tmpSiteList.add(programmeSiteIdList.get(i).intValue()+":"+
						ProgrammeSiteType.siteMap.get(programmeSiteIdList.get(i).intValue()));
				logger.debug("add :"+ProgrammeSiteType.siteMap.get(programmeSiteIdList.get(i).intValue()));
			}
			
			programmeJson.put("sites",tmpSiteList);
		}

	}

	return returnJson;
}
	

	/**
	 * 排行榜
	 * @param programmeIdArr
	 * @param cate_id
	 * @return
	 */
	 public static JSONObject search(String[] programmeIdArr, int cate_id) {
		 logger.debug("the programmeIdarr:"+programmeIdArr.toString()+"  cate:"+cate_id);
		 
		 DocumentaryInfo info = ExtInfoHolder.getCurrentThreadLocal().documentaryInfo;
		 if(programmeIdArr==null || programmeIdArr.length<=0) return null;
		 JSONArray programmeArr=new JSONArray();
		 for(int i=0;i<programmeIdArr.length;i++){
			 Programme programme=null;
			 logger.debug("the programmeId:"+programmeIdArr[i]);
			 programme = info.id_programme.get(Integer.valueOf(programmeIdArr[i]));
			 
			 if(programme==null) continue;
			 logger.debug("the programme's name:"+programme.getName());
			 JSONObject programmeJson=null;
			try {
				programmeJson = genJson(programme,info,null);
				logger.debug("the programme'name:"+programme.getName()+"   url:"+programme.getPlayUrl());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("生成programmeJson失败： programme's id:"+programme.getId());
			}
			 if(programmeJson!=null) programmeArr.put(programmeJson);
			 
		 }
		 
		 JSONObject returnJson=new JSONObject();
		 try {
			returnJson.put("array", programmeArr);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return returnJson;
	 }
}
