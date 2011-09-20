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

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.manage.util.ImageUtil;
import com.youku.soku.newext.info.AliasInfo;
import com.youku.soku.newext.info.AnimeInfo;
import com.youku.soku.newext.info.ExtInfoHolder;
import com.youku.soku.newext.info.MovieInfo;
import com.youku.soku.newext.info.PersonInfo;
import com.youku.soku.newext.info.TeleplayInfo;
import com.youku.soku.newext.info.VarietyInfo;
import com.youku.soku.newext.util.ChannelType;
import com.youku.soku.newext.util.JSONUtil;
import com.youku.soku.newext.util.StringUtil;
import com.youku.soku.newext.util.comparator.DownComparator;
import com.youku.soku.newext.util.comparator.EpisodeOrderComparator;

/**
 * 人物直达区搜索
 * @author User
 *
 */
public class PeopleSearcher {
	private static Log logger = LogFactory.getLog(PeopleSearcher.class);

	
	private static final int DEFAULT_PROGRAMME_COUNT = 5;
	public static JSONObject searchByName(String keyword,String site) {
		return searchByName(keyword,DEFAULT_PROGRAMME_COUNT,null,site);
	}
	public static JSONObject searchByName(String keyword,ChannelType type,String site) {
		return searchByName(keyword,DEFAULT_PROGRAMME_COUNT,type,site);
	}
	/**
	 * 遇到异常可以返回null, 不应该throws. 确保其他频道检索的正常进行
	 * 
	 * @param keyword
	 * @return
	 */
	public static JSONObject searchByName(String keyword,int limit,ChannelType type,String site) {

		if (keyword == null) {
			return null;
		}

		AliasInfo info = ExtInfoHolder.getCurrentThreadLocal().aliasInfo;
		PersonInfo personInfo = ExtInfoHolder.getCurrentThreadLocal().personInfo;

		List<Programme> programmeList = new ArrayList<Programme>();
		// 检索info.person_programme
		Set<Integer> idList = personInfo.nameIdsMap.get(keyword);
		if(idList==null || idList.size()<1){
			return null;
		}
		
		JSONObject personArray = new JSONObject();
		
		for (Integer personId : idList) {
			JSONObject personJson = new JSONObject();
			
			programmeList = personInfo.personproMap.get(personId);
			
			if (programmeList != null && programmeList.size() > 0) {
				JSONArray returnJsonArr = new JSONArray();
				
				int count = 1;
				if (type != null)logger.debug("limit:"+type.getValue());
				for (Programme programme : programmeList) {
					if (type != null && programme.getCate() != type.getValue()){
							continue;
					}
					
					logger.debug(programme.getCate());
					JSONObject eleJson = null;
					try {
						eleJson = genJson(programme, info,site);
					} catch (Exception e) {
						logger.error("生成查询结果json数据失败");
						e.printStackTrace();
						continue;
					}
					if (eleJson != null){
						returnJsonArr.put(eleJson);
					}else {
						continue;
					}
					
					if (limit > 0 && count ++ >= limit){
						break;
					}
				}
				
				try {
					personJson.put("episodes", returnJsonArr);
				} catch (JSONException e) {
				}
			}
			
			try {
				personArray.put(personId+"", personJson);
			} catch (JSONException e) {
			}
		}
		
		

		return personArray;
	}

	// 根据teleplayResult来生成相应的json
	public static JSONObject genJson(Programme programme, AliasInfo info,String site)
			throws Exception {
		if (programme == null || info == null)
			return null;
		
		if (programme.getCate() == ChannelType.MOVIE.getValue()) {
			JSONObject mjson = new JSONObject();
			String middStr = info.middMap.get(programme.getContentId());
			JSONObject middJson = new JSONObject(middStr);
			logger.debug(programme.getName());
			mjson.put("name", StringUtils.trimToEmpty(programme.getName()));
			mjson.put("pid", programme.getId());
			mjson.put("type", "电影");
			
			double seconds=PeopleSearcher.getSeconds(programme, info);
			if(seconds==0){
				logger.debug("seconds==0 return null!");
			}
			mjson.put("seconds",seconds);
			
			
			String siteUrl = getSitePlayUrl(programme,info,site);
			if(siteUrl==null || siteUrl.length()==0){
				return null;
			}
			
			mjson.put("url", siteUrl);
			
			String firstLogo=getFirstLogo(programme, info);
			
			if(firstLogo==null){
				logger.debug("firstLogo==0 return null!");
			}
			
			mjson.put("pic", firstLogo);
			
			if (middJson != null) {
				double reputation = middJson.optDouble("reputation",0);
				if(reputation<1){
					reputation = 5.0;
				}
				mjson.put("score", reputation);
				mjson.put("releaseyear", middJson.optInt("releaseyear"));
				mjson.put("streamtypes", middJson.optJSONArray("streamtypes")==null?"[]":middJson.optJSONArray("streamtypes"));
				mjson.put("vpic", middJson.optString("show_vthumburl"));
				mjson.put("defaultpic", firstLogo);
			} /*else {
				mjson.put("score", 0);
				mjson.put("releaseyear", 0);
//				mjson.put("pic", "");
			}*/
			

			return mjson;
		} else if (programme.getCate() == ChannelType.TELEPLAY.getValue()) {

			JSONObject tvjson = new JSONObject();
			String middStr = info.middMap.get(programme.getContentId());
			JSONObject middJson = new JSONObject(middStr);
			logger.debug(programme.getName());
			tvjson.put("name", StringUtils.trimToEmpty(programme.getName()));
			tvjson.put("pid", programme.getId());
			tvjson.put("episodeTotal", programme.getEpisodeTotal());
			tvjson.put("type", "电视剧");
			
			TeleplayInfo teleplayInfo=ExtInfoHolder.getCurrentThreadLocal().teleplayInfo;
			
			List<ProgrammeSite> psList=teleplayInfo.programme_programmeSite.get(programme);
			
			if(psList==null || psList.size()==0){
				logger.debug("psList.size=0 return null!");
				return null;
			}
			
			List<ProgrammeEpisode> episodeList=teleplayInfo.programmeSite_episode.get(psList.get(0));
			
			if(episodeList==null || episodeList.size()==0){
				logger.debug("episodeList.size=0 return null!");
				return null;
			}
			
			
			//TODO
			int episodeTotal = programme.getEpisodeTotal();
//			boolean isAsc = (episodeTotal>0 && episodeList.size()>=episodeTotal);
			String displayStatus= "";
			if(psList.get(0).getSortmode()==0){
				displayStatus=TeleplaySearcher.getUpdateStatus(episodeTotal, episodeList.size(),episodeList.get(episodeList.size()-1));
			}else {
				displayStatus=TeleplaySearcher.getUpdateStatus(episodeTotal, episodeList.size(),episodeList.get(0));
			}
			
			String siteUrl = getSitePlayUrl(programme,info,site);
			if(siteUrl==null || siteUrl.length()==0){
				return null;
			}
			
			tvjson.put("url", siteUrl);
			
			tvjson.put("display_status", displayStatus);
			
			String firstLogo=getFirstLogo(programme, info);
			
			if(firstLogo==null) {
				logger.debug("firstLogo = null return null!");
				return null;
			}
			tvjson.put("pic", firstLogo);
			
			
			if (middJson != null) {
				double reputation = middJson.optDouble("reputation",0);
				if(reputation<1){
					reputation = 5.0;
				}
				tvjson.put("score", reputation);
				tvjson.put("releaseyear", middJson.optInt("releaseyear"));
//				tvjson.put("pic", middJson.optString("show_thumburl"));
				tvjson.put("streamtypes", middJson.optJSONArray("streamtypes")==null?"[]":middJson.optJSONArray("streamtypes"));

			} /*else {
				tvjson.put("score", 0);
				tvjson.put("releaseyear", 0);
//				tvjson.put("pic", "");
			}*/
			return tvjson;

		}else if(programme.getCate() == ChannelType.ANIME.getValue()){

			JSONObject dmjson = new JSONObject();
			String middStr = info.middMap.get(programme.getContentId());
			JSONObject middJson = new JSONObject(middStr);
			logger.debug(programme.getName());
			dmjson.put("name", StringUtils.trimToEmpty(programme.getName()));
			dmjson.put("pid", programme.getId());
			dmjson.put("episodeTotal", programme.getEpisodeTotal());
			dmjson.put("type", "动漫");
			
			AnimeInfo animeInfo=ExtInfoHolder.getCurrentThreadLocal().animeInfo;
			
			List<ProgrammeSite> psList=animeInfo.programme_programmeSite.get(programme);
			
			if(psList==null || psList.size()==0){
				logger.debug("psList.size=0 return null!");
				return null;
			}
			
			List<ProgrammeEpisode> episodeList=animeInfo.programmeSite_episode.get(psList.get(0));
			
			if(episodeList==null || episodeList.size()==0){
				logger.debug("episodeList.size=0 return null!");
				return null;
			}
			
			int episodeTotal = programme.getEpisodeTotal();
//			boolean isAsc = (episodeTotal>0 && episodeList.size()>=episodeTotal);
			String displayStatus= "";
			if(psList.get(0).getSortmode()==0){
				displayStatus=TeleplaySearcher.getUpdateStatus(episodeTotal, episodeList.size(),episodeList.get(episodeList.size()-1));
			}else {
				displayStatus=TeleplaySearcher.getUpdateStatus(episodeTotal, episodeList.size(),episodeList.get(0));
			}
			
			String siteUrl = getSitePlayUrl(programme,info,site);
			if(siteUrl==null || siteUrl.length()==0){
				return null;
			}
			
			dmjson.put("url", siteUrl);
			
			dmjson.put("display_status", displayStatus);
			
			String firstLogo=getFirstLogo(programme, info);
			
			if(firstLogo==null) {
				logger.debug("firstLogo = null return null!");
				return null;
			}
			dmjson.put("pic", firstLogo);
			
			
			if (middJson != null) {
				double reputation = middJson.optDouble("reputation",0);
				if(reputation<1){
					reputation = 5.0;
				}
				dmjson.put("score",reputation);
				dmjson.put("releaseyear", middJson.optInt("releaseyear"));
//				dmjson.put("pic", middJson.optString("show_thumburl"));
				dmjson.put("streamtypes", middJson.optJSONArray("streamtypes")==null?"[]":middJson.optJSONArray("streamtypes"));

			} /*else {
				dmjson.put("score", 0);
				dmjson.put("releaseyear", 0);
//				dmjson.put("pic", "");
			}*/
			return dmjson;

		}else if(programme.getCate() == ChannelType.VARIETY.getValue()){

			JSONObject zyjson = new JSONObject();
			String middStr = info.middMap.get(programme.getContentId());
			JSONObject middJson = new JSONObject(middStr);
			logger.debug(programme.getName());
			zyjson.put("name", StringUtils.trimToEmpty(programme.getName()));
			zyjson.put("pid", programme.getId());
			zyjson.put("episodeTotal", programme.getEpisodeTotal());
			zyjson.put("type", "综艺");
			
			VarietyInfo varietyInfo=ExtInfoHolder.getCurrentThreadLocal().varietyInfo;
			
			List<ProgrammeSite> psList=varietyInfo.programme_programmeSite.get(programme);
			
			if(psList==null || psList.size()==0){
				logger.debug("psList.size=0 return null!");
				return null;
			}
			
			List<ProgrammeEpisode> episodeList=varietyInfo.programmeSite_episode.get(psList.get(0));
			
			if(episodeList==null || episodeList.size()==0){
				logger.debug("episodeList.size=0 return null!");
				return null;
			}
			
			
//			Collections.sort(episodeList, new DownComparator<ProgrammeEpisode>(new EpisodeOrderComparator()));
			int episodeTotal = programme.getEpisodeTotal();
			String displayStatus= "";
			int orderStage=0;
			if(psList.get(0).getSortmode()==0){
				displayStatus=TeleplaySearcher.getUpdateStatus(episodeTotal, episodeList.size(),episodeList.get(episodeList.size()-1));
				orderStage = episodeList.get(episodeList.size()-1).getOrderStage();
			}else {
				displayStatus=TeleplaySearcher.getUpdateStatus(episodeTotal, episodeList.size(),episodeList.get(0));
				orderStage = episodeList.get(0).getOrderStage();
			}
//			zyjson.put("episode_last_stage", episodeList.get(0).getOrderStage());
			zyjson.put("episode_last_stage", orderStage);
//			zyjson.put("display_status", "最新:"+episodeList.get(0).getOrderStage());
			zyjson.put("display_status", displayStatus);
			
			String siteUrl = getSitePlayUrl(programme,info,site);
			if(siteUrl==null || siteUrl.length()==0){
				return null;
			}
			
			zyjson.put("url", siteUrl);
			
			String firstLogo=getFirstLogo(programme, info);
			
			if(firstLogo==null) {
				logger.debug("firstLogo = null return null!");
				return null;
			}
			zyjson.put("pic", firstLogo);
			
			
			if (middJson != null) {
				double reputation = middJson.optDouble("reputation",0);
				if(reputation<1){
					reputation = 5.0;
				}
				zyjson.put("score", reputation);
				zyjson.put("releaseyear", middJson.optInt("releaseyear"));
//				zyjson.put("pic", middJson.optString("show_thumburl"));
				zyjson.put("streamtypes", middJson.optJSONArray("streamtypes")==null?"[]":middJson.optJSONArray("streamtypes"));

			} /*else {
				zyjson.put("score", 0);
				zyjson.put("releaseyear", 0);
//				zyjson.put("pic", "");
			}*/
			return zyjson;

		}
		
		return null;
	}
	
	/**
	 * @param info
	 * @param site
	 * @return
	 */
	private static String getSitePlayUrl(Programme programme,AliasInfo info, String site) {
		Set<Integer> siteSet = StringUtil.parseSite(site);
		if(siteSet!=null && siteSet.size()>0){
			List<ProgrammeSite> programmeSites = info.programme_programmeSite.get(programme);
			if(programmeSites!=null && programmeSites.size()>0){
				for (ProgrammeSite programmeSite : programmeSites) {
					if(siteSet.contains(programmeSite.getSourceSite())){
						List<ProgrammeEpisode> tmpEpisodeList = info.programmeSite_episode.get(programmeSite);
						if(tmpEpisodeList.size()>0){
							return tmpEpisodeList.get(0).getUrl();
						}
					}
				}
			}
			return null;
		}else {
			return programme.getPlayUrl();
		}
		
	}
	/**
	 * 得到该programme对应的firstLogo
	 * 明星无截图_128px.png       0900641F46497500B200000000000000000000-0000-0000-0000-0000F74CBDED
     * 明星无截图_70px.png        0900641F4649732D5A00000000000000000000-0000-0000-0000-00005EAC1379
     * 电影海报无截图_120x174.png 0900641F464A7BBE7400000000000000000000-0000-0000-0000-00005B2F7B0E
     * 普通无截图_128x96.png      0900641F464A911EDD00000000000000000000-0000-0000-0000-00009197BA80
	 * @param programme
	 * @param info
	 * @return
	 */
	public static String getFirstLogo(Programme programme, AliasInfo info){
		List<ProgrammeSite>  programmeSiteList= info.programme_programmeSite.get(programme);
		
		String mid = info.middMap.get(programme.getContentId());
		if(mid!=null && mid.length()>0){
			try {
				JSONObject midJsonObject = new JSONObject(mid);
				if(!JSONUtil.isEmpty(midJsonObject)){
					if(!JSONUtil.isEmpty(midJsonObject, "show_thumburl")){
						String pic = midJsonObject.optString("show_thumburl");
						if(pic!=null && pic.length()>10 && !isYokuDefaultPic(pic)){
							if(pic.indexOf("http:")==0){
								return StringUtils.trimToEmpty(pic);
							}else {
								return StringUtils.trimToEmpty(ImageUtil.getDisplayUrl(pic));
							} 
						}
					}
				}
			} catch (JSONException e) {
			}
		}
		
		if(programmeSiteList==null || programmeSiteList.size()==0) return null;
		String pic = programmeSiteList.get(0).getFirstLogo();
		if(pic!=null && pic.length()>10){
			if(!isYokuDefaultPic(pic)){
				if(pic.indexOf("http:")==0){
					return StringUtils.trimToEmpty(pic);
				}else {
					return StringUtils.trimToEmpty(ImageUtil.getDisplayUrl(pic));
				}
			}
		}
		
//		if(programme.getCate()==ChannelType.MOVIE.getValue()){
//			return ImageUtil.getDisplayUrl("0900641F464A7BBE7400000000000000000000-0000-0000-0000-00005B2F7B0E");
//		}else {
			return ImageUtil.getDisplayUrl("0900641F464A911EDD00000000000000000000-0000-0000-0000-00009197BA80");
//		}
	}
	
	/**
	 * @param optString
	 */
	public static String getLogo(String pic) {
		if(!isYokuDefaultPic(pic)){
			if(pic.indexOf("http:")==0){
				return StringUtils.trimToEmpty(pic);
			}else {
				return StringUtils.trimToEmpty(ImageUtil.getDisplayUrl(pic));
			} 
		}
		
		return ImageUtil.getDisplayUrl("0900641F464A911EDD00000000000000000000-0000-0000-0000-00009197BA80");
	}
	
	/**
	 * @param pic
	 * @return
	 */
	private static boolean isYokuDefaultPic(String pic) {
		if(pic.indexOf("051000004DACF16A9792732B23061177")>=0){
			return true;
		}
		if(pic.indexOf("0900641F464A6318D600000000000000000000-0000-0000-0000-000042145BB0")>=0){//老soku默认图
			return true;
		}
		return false;
	}
	/**
	 * 得到该programme对应的播放长度(该programme对应的第一个视频) 电影分类
	 * @param programme
	 * @param info
	 * @return
	 */
	public static double getSeconds(Programme programme, AliasInfo info){
		List<ProgrammeSite>  programmeSiteList= info.programme_programmeSite.get(programme);
		
		if(programmeSiteList==null || programmeSiteList.size()==0) return 0;
		
		ProgrammeSite ps=programmeSiteList.get(0);
		
		MovieInfo movieInfo= ExtInfoHolder.getCurrentThreadLocal().movieInfo;
		
		List<ProgrammeEpisode> programmeEpisodeList=movieInfo.programmeSite_episode.get(ps);
		
		if(programmeEpisodeList==null || programmeEpisodeList.size()==0) return 0;
			
		
		return programmeEpisodeList.get(0).getSeconds();
	}
	
}
