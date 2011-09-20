package com.youku.soku.newext.searcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.soku.manage.util.ImageUtil;
import com.youku.soku.newext.info.ExtInfoHolder;
import com.youku.soku.newext.info.MovieInfo;
import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.Series;
import com.youku.search.sort.json.util.JSONUtil;
import com.youku.soku.newext.util.ProgrammeSiteType;
import com.youku.soku.newext.util.StringUtil;
import com.youku.soku.newext.util.comparator.SiteComparator;
import com.youku.soku.newext.util.middleResource.MiddleResourceUtil;

public class MovieSearcher {
	private static Log logger = LogFactory.getLog(MovieSearcher.class);
	
	

	/**
	 * 遇到异常可以返回null, 不应该throws. 确保其他频道检索的正常进行
	 * 
	 * @param keyword
	 * @return
	 */
	public static JSONArray searchByName(String keyword,String site) {

		if (keyword == null) {
			return null;
		}

		MovieInfo info = ExtInfoHolder.getCurrentThreadLocal().movieInfo;

		// 检索系列map,如果有结果则直接返回，不用搜索name_programmeSite表
		List<Programme> programmeList = new ArrayList<Programme>();

		// 检索info.name_programmeSite
		programmeList = info.name_programme.get(keyword.toLowerCase());
		if (programmeList != null && programmeList.size() > 0) {
			JSONArray returnJsonArr = new JSONArray();
			for (Programme programme : programmeList) {
				JSONObject eleJson = null;
				try {
					eleJson = genJson(programme, info,site);
				} catch (Exception e) {
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
	public static JSONObject genJson(Programme programme, MovieInfo info,String site)
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

		List<ProgrammeSite> programmeSiteList = info.programme_programmeSite
				.get(programme);
//		Collections.sort(programmeSiteList,new SiteComparator());
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
				
//				只有优酷视频才添加hd字段
				if(programmeSite.getSourceSite()==ProgrammeSiteType.优酷网.getValue()){
					eleSite.put("streamtypes", middJson.optJSONArray("streamtypes")==null?"[]":middJson.optJSONArray("streamtypes"));
					programmeJson.put("update_notice", StringUtils.trimToEmpty(middJson.optString("update_notice")));
				}
				
				eleSite.put("display_status", "正片");
				
				
				List<ProgrammeEpisode> episodeList = info.programmeSite_episode
						.get(programmeSite);
				
				JSONArray episodeArr = BaseSearcher.getEpisodesJsonArray(episodeList,eleSite);
				if(!com.youku.soku.newext.util.JSONUtil.isEmpty(episodeArr)){
					programmeSiteIdList.add(programmeSite.getSourceSite());
					programmeSiteArr.put(new Integer(programmeSite.getSourceSite()).toString(), eleSite);
				}

			}

			if(com.youku.soku.newext.util.JSONUtil.isEmpty(programmeSiteArr)){
				return null;
			}
			//TODO 计算节目的播放地址。
			programmeJson.put("url",BaseSearcher.getPlayUrl(programmeSiteArr));
			returnJson.put("ProgrammeSite", programmeSiteArr);
			
			
//			对站点排序，优酷第一
			if(programmeSiteIdList!=null && programmeSiteIdList.size()>0){
//				处理结果
				List<String> tmpSiteList=new ArrayList<String>();
				
				for(int i=0;i<programmeSiteIdList.size();i++){
					tmpSiteList.add(programmeSiteIdList.get(i).intValue()+":"+
							ProgrammeSiteType.siteMap.get(programmeSiteIdList.get(i).intValue()));
					
					if(logger.isDebugEnabled()){
						logger.debug("add :"+ProgrammeSiteType.siteMap.get(programmeSiteIdList.get(i).intValue()));
					}
				}
				
				programmeJson.put("sites",tmpSiteList);
			}
			
			
			

		}else{
			logger.error("该影片没有对应的programme_site:name："+programme.getName()+"   pid:"+programme.getId());
			
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
		 
		 MovieInfo info = ExtInfoHolder.getCurrentThreadLocal().movieInfo;
		 if(programmeIdArr==null || programmeIdArr.length<=0) return null;
		 JSONArray programmeArr=new JSONArray();
		 for(int i=0;i<programmeIdArr.length;i++){
			 
			 Programme programme=null;
			 logger.debug("the programmeId:"+programmeIdArr[i]);
			 programme = info.id_programme.get(Integer.valueOf(programmeIdArr[i]));
			 
//			 List<ProgrammeSite> programmeSiteList=info.programme_programmeSite.get(programme);
//			 if(programmeSiteList==null || programmeSiteList.size()<=0) continue;
//			 logger.debug("the programmeSiteList's size:"+programmeSiteList.size());
//			 
//			 programme=info.programmeSite_programme.get(programmeSiteList.get(0));
			 
			 
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

	/**
	 * 根据中间层的streamtypes 来判断是否高清
	 */
	public  static int genHdFlag(JSONObject middJson) {
		if(middJson==null) return 0;
		JSONArray typeArr=middJson.optJSONArray("streamtypes");
		if(typeArr==null || typeArr.length()<=0) return 0;
		try{
		for(int i=0;i<typeArr.length();i++){
			if(typeArr.getJSONObject(i).toString().equals("hd") ||typeArr.getJSONObject(i).toString().equals("hd2")){
				return 1;
			}
		}}catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
}
