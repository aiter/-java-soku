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
import com.youku.soku.newext.info.AliasInfo;
import com.youku.soku.newext.info.AnimeInfo;
import com.youku.soku.newext.info.DocumentaryInfo;
import com.youku.soku.newext.info.ExtInfoHolder;
import com.youku.soku.newext.info.MovieInfo;
import com.youku.soku.newext.info.TeleplayInfo;
import com.youku.soku.newext.info.VarietyInfo;
import com.youku.soku.newext.util.ProgrammeSiteType;
import com.youku.soku.newext.util.StringUtil;

/**
 * @author tanxiuguang
 * create on Oct 13, 2011
 */
public class ProgrammeSearcher extends BaseSearcher {
	

	private static Log logger = LogFactory.getLog(ProgrammeSearcher.class);
	
	public static JSONArray searchAnime(String keyword, String site) {
		AnimeInfo info = ExtInfoHolder.getCurrentThreadLocal().animeInfo;
		return searchByName(keyword, site, info);
	}
	
	public static JSONArray searchTeleplay(String keyword, String site) {
		TeleplayInfo info = ExtInfoHolder.getCurrentThreadLocal().teleplayInfo;
		return searchByName(keyword, site, info);
	}
	
	public static JSONArray searchVariety(String keyword, String site) {
		VarietyInfo info = ExtInfoHolder.getCurrentThreadLocal().varietyInfo;
		return searchByName(keyword, site, info);
	}
	
	public static JSONArray searchMovie(String keyword, String site) {
		MovieInfo info = ExtInfoHolder.getCurrentThreadLocal().movieInfo;
		return searchByName(keyword, site, info);
	}
	
	public static JSONArray searchDocumentary(String keyword, String site) {
		DocumentaryInfo info = ExtInfoHolder.getCurrentThreadLocal().documentaryInfo;
		return searchByName(keyword, site, info);
	}

	/**
	 * 遇到异常可以返回null, 不应该throws. 确保其他频道检索的正常进行
	 * 
	 * @param keyword
	 * @return
	 */
	public static JSONArray searchByName(String keyword, String site, AliasInfo info) {

		if (keyword == null) {
			return null;
		}
		
//		 检索系列map,如果有结果则直接返回，不用搜索name_programmeSite表
		List<Programme> programmeList = new ArrayList<Programme>();
		programmeList = info.getSeries_programme().get(keyword.toLowerCase());

		JSONArray returnJsonArr = new JSONArray();
		if (programmeList != null && programmeList.size() > 0) {

			JSONObject eleJson = null;
			try {
				eleJson = genJson(programmeList.get(0), info, site);
			} catch (Exception e) {
				logger.error("生成查询结果json数据失败");
			}
			if (eleJson != null)
				returnJsonArr.put(eleJson);
		}

		if (returnJsonArr.length() > 0)
			return returnJsonArr;

		// 检索info.name_programmeSite
		programmeList = info.getName_programme().get(keyword.toLowerCase());
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

	

	// 根据teleplayResult来生成相应的json
	public static JSONObject genJson(Programme programme, AliasInfo info,String site)
			throws Exception {
		
		JSONObject returnJson = new JSONObject();
		List<Integer> programmeSiteIdList=new ArrayList<Integer>();
		
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

		//节目的所有站点
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
					if(programmeSite.getSortmode()==0){
						displayStatus = getUpdateStatus(info.getCate_id(), episodeTotal, episodeList.size(),episodeList.get(episodeList.size()-1));
					}else {
						displayStatus = getUpdateStatus(info.getCate_id(), episodeTotal, episodeList.size(),episodeList.get(0));
					}
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
//				处理结果
				List<String> tmpSiteList = getProSiteStrList(programmeSiteIdList);
				
				programmeJson.put("sites",tmpSiteList);
			}
		}

		return returnJson;
	}
	
	public static List<String> getProSiteStrList(List<Integer> programmeSiteIdList) {
		List<String> tmpSiteList = new ArrayList<String>();
		
		for(int i=0;i<programmeSiteIdList.size();i++){
			tmpSiteList.add(programmeSiteIdList.get(i).intValue()+":"+
					ProgrammeSiteType.siteMap.get(programmeSiteIdList.get(i).intValue()));
		}
		
		return tmpSiteList;
	}


	/**
	 * 排行榜
	 * @param programmeIdArr
	 * @param cate_id
	 * @return
	 */
	 public static JSONObject search(String[] programmeIdArr, int cate_id, AliasInfo info) {
		 logger.debug("the programmeIdarr:"+programmeIdArr.toString()+"  cate:"+cate_id);
		 
		 //AnimeInfo info = ExtInfoHolder.getCurrentThreadLocal().animeInfo;
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
