package com.youku.soku.newext.loader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.library.load.Series;
import com.youku.soku.newext.info.AliasInfo;
import com.youku.soku.newext.info.TypeConstant.ProgrammeState;
import com.youku.soku.newext.util.DbUtil;
import com.youku.soku.newext.util.ProgrammeSiteType;
import com.youku.soku.newext.util.comparator.SiteComparator;
import com.youku.soku.newext.util.middleResource.MiddleResourceUtil;
import com.youku.soku.util.SiteUtil;

/**
 * 父类加载器
 */
public class AliasInfoLoader {

	private static Log logger = LogFactory.getLog(AliasInfoLoader.class);

	final int MAX_COUNT = Integer.MAX_VALUE;
	final int limit = 1000;

	private static final String TRAILER_FLAG = "预告片";
	private static final String TIDBI_FLAG = "花絮";
	private static final String MOVIE_CATEGORY = "电影";

	public int load(AliasInfo aliasInfo, MiddTierResourceBuilder middTierResourceBuilder) {

		CmsLoader cmsLoader = new CmsLoader();
		Map<String, List<JSONObject>> cmsContent = new HashMap<String, List<JSONObject>>();

		
		
		try {
			cmsContent = cmsLoader.getAllCms();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.info("开始加载 （遍历programme 表，加载5个map） 信息 ...");
		Criteria criteria = new Criteria();
		//criteria.add(ProgrammePeer.ID, 115168);
		criteria.add(ProgrammePeer.STATE, ProgrammeState.NORMAL);
		criteria.add(ProgrammePeer.BLOCKED, 0);
		criteria.addAscendingOrderByColumn(ProgrammePeer.ID);

		// yanjg
		// int[]
		// ints={113208,113770,113771,113809,113814,113815,114764,65183,109483,126598,122604,64430};
		// criteria.addIn(ProgrammePeer.ID, ints);

		int validCount = 0;

		List<Programme> list = null;
		try {
			list = ProgrammePeer.doSelect(criteria);
		} catch (TorqueException e) {
			logger.error("获取programme list失败");
			return -1;
		}

	
		for (Programme programme : list) {
			logger.debug("Load programme: " + programme.getName());
			aliasInfo.id_programme.put(programme.getId(), programme);
			// programme_series
			addProgrammeSeries(programme, aliasInfo);

			
			
			// middMap
			JSONObject showJsonInfo = middTierResourceBuilder.getShowDetailMap().get(programme.getContentId());
			if(showJsonInfo == null) {
				continue;
			}
			aliasInfo.middMap.put(programme.getContentId(), showJsonInfo.toString());
			
			addProProgrammeSite(programme, aliasInfo);

			/**
			 * 专题json结构 pk_cms: "2825" cmsid: "2825" title: "好莱坞高清电影频道"
			 * -keywords: [ "好莱坞电影" ] desc: "好莱坞电影 大片汇集" deschead:
			 * "好莱坞电影 大片汇集" cmsurl: "http://movie.youku.com/hollywood"
			 * state: "published" createtime: "2011-07-06 15:27:02"
			 * updatetime: "2011-08-15 14:41:02" publishtime:
			 * "2011-08-15 16:35:35" type: "topic" thumburl:
			 * "http://res.mfs.ykimg.com/051000004E298EA59792730725098A56.jpg"
			 */

			String showHasVideoType = showJsonInfo.optString("hasvideotype");
			//如果节目有花絮或预告片，通过中间层查到相关结果
			if (showHasVideoType.contains(TIDBI_FLAG) || showHasVideoType.contains(TRAILER_FLAG)) {
				JSONArray tidbitAndTrailer = middTierResourceBuilder.getTidbitsAndTrailerMap().get(programme.getContentId());
				if (tidbitAndTrailer != null) {
					JSONArray tidbit = new JSONArray();
					JSONArray trailer = new JSONArray();
					for (int i = 0; i < tidbitAndTrailer.length(); i++) {
						JSONObject obj = tidbitAndTrailer.optJSONObject(i);
						if (obj.optString("show_videotype").equals(TIDBI_FLAG)) {
							tidbit.put(obj);
						} else if (obj.optString("show_videotype").equals(TRAILER_FLAG)) {
							if(obj.optString("videoid").length() > 0 && StringUtils.isEmpty(programme.getTrailerUrl())) {
								programme.setTrailerUrl("http://v.youku.com/v_show/id_" + obj.optString("videoid") + ".html");
								programme.setTrailerPic(obj.optString("thumburl"));
							}
							//programme.setTrailer6MonthAgo(isTrailer6MonthAgo(showJsonInfo.optInt("releaseyear"), showJsonInfo.optInt("releasemonth")));
						}
					}
					if(tidbit.length() > 0) {
						programme.setTidbit(tidbit.toString());
					}
					
					
				}
			}
			
			//如果节目类型是电影，去中间层查找看点
			if(MOVIE_CATEGORY.equals(showJsonInfo.optString("showcategory"))) {
				JSONArray videoInfoArray = middTierResourceBuilder.getVideoPointMap().get(programme.getContentId());
				if(videoInfoArray != null) {
					JSONArray points = new JSONArray();
					for(int i = 0; i < videoInfoArray.length(); i++) {
						JSONObject videoInfo = videoInfoArray.optJSONObject(i);
						JSONArray videoPointsArray = videoInfo.optJSONArray("point");
						if(videoPointsArray != null) {
							for(int j = 0; j < videoPointsArray.length(); j++) {
								JSONObject videoPoint = videoPointsArray.optJSONObject(j);
								if("story".equals(videoPoint.optString("ctype"))){
									points.put(videoPoint);
								}
							}
						}
						
					}
					if(points.length() > 0) {
						programme.setPoints(points.toString());
						logger.info("Programme has Point: " + programme.getName() + programme.getContentId());
					}
				}
			}
			
			//获取节目语言版本
			JSONArray videoLanguageJSONArray = middTierResourceBuilder.getVideoLanguageMap().get(programme.getContentId());
			if(videoLanguageJSONArray != null) {
				for(int i = 0; i < videoLanguageJSONArray.length(); i++) {
					JSONObject videoInfo = videoLanguageJSONArray.optJSONObject(i);
					JSONArray videoLang = videoInfo.optJSONArray("audiolang");
					
					if(videoLang != null) {
						programme.setVideoLang(videoLang.toString());
					}
				}
			}
			
			List<JSONObject> specialTopicList = cmsContent.get(showJsonInfo.optString("showname"));
			
			if (specialTopicList != null) {
				JSONArray specialTopic = new JSONArray();
				for (JSONObject obj : specialTopicList) {
					specialTopic.put(obj);
				}
				programme.setSpecialTopic(specialTopic.toString());
			}
			validCount++;

		}

		logger.info("counts:" + validCount);

		logger.info("加载 AliasInfo 5个map结束；遍历programme表结束");

		return validCount;
	}

	public static JSONObject addMiddMap(Programme programme, AliasInfo aliasInfo) {
		if (programme == null)
			return null;
		
		JSONObject middleResourceJson = MiddleResourceUtil.getProgrammeById(programme.getContentId(), MiddleResourceUtil.SHOW_FILEDS);
		if (middleResourceJson != null) {
			if (logger.isDebugEnabled()) {
				logger.info("添加中间层资源：" + programme.getName());
			}
			aliasInfo.middMap.put(programme.getContentId(), middleResourceJson.toString());
		} else {
			logger.info("添加中间层资源-失败-show_id：" + programme.getContentId() + " programme_id:" + programme.getId());
		}

		return middleResourceJson;
	}

	public static void addProProgrammeSite(Programme programme, AliasInfo aliasInfo) {
		if (programme == null)
			return;

		List<ProgrammeSite> list = DbUtil.getProgrammeSiteListByProgramme(programme);

		String middStr = aliasInfo.middMap.get(programme.getContentId());
		int programmeSortmode = 0;
		int programmePaid = 0;
		if (middStr != null && middStr.length() > 0) {
			try {
				JSONObject middJson = new JSONObject(middStr);
				programmeSortmode = middJson.optInt("sortmode", programmeSortmode);
				programmePaid = middJson.optInt("paid", programmePaid);
			} catch (JSONException e) {
			}
		}
		programme.setPaid(programmePaid);
		/*
		 * if(programmePaid==1){//所有收费节目都不加载 return; }
		 */

		if (list != null && list.size() > 0) {
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				ProgrammeSite programmeSite = (ProgrammeSite) iterator.next();
				
				//控制加载链接的站点,使用和页面一直的sitename，防止页面上的站点出现null
				if(SiteUtil.getSiteName(programmeSite.getSourceSite()) == null) {
					continue;
				}
				
				programmeSite.setSortmode(programmeSortmode);
				// 添加站点节目-->视频列表
				// TODO 可以根据条件，使视频是倒排还是正排***************************************

				List<ProgrammeEpisode> programmeEpisodeList = null;
				// 只有电影加载order_id=0的视频列表
				programmeEpisodeList = DbUtil.getProgrammeEpisode(programmeSite, programmeSortmode == 1, programme.getCate());

				if (programmeEpisodeList == null || programmeEpisodeList.size() == 0) {
					// 如果没有视频列表。移除这个站点
					iterator.remove();
					if (logger.isDebugEnabled()) {
						logger.info("programmeEpisode 不存在： programme's id:" + programme.getId() + "  programmeSite's id:"
								+ programmeSite.getId());
					}
					continue;
				}

				programmeSite.setEpisodeCollected(programmeEpisodeList.size());// 设置站点收录的集数
				if (programme.getEpisodeTotal() > 0) {
					/** total为0的都未完成。收录集数大于total的，收录完成 */
					programmeSite.setCompleted(programmeSite.getEpisodeCollected() >= programme.getEpisodeTotal() ? 1 : 0);
				} else {
					programmeSite.setCompleted(0);
				}

				// 根据搜索是否完成，对视频列表排序
				// if(programmeSite.getCompleted()>0){
				// Collections.sort(programmeEpisodeList, new
				// EpisodeOrderComparator());
				// }else {
				// Collections.sort(programmeEpisodeList, new
				// DownComparator<ProgrammeEpisode>(new
				// EpisodeOrderComparator()));
				// }

				aliasInfo.programmeSite_episode.put(programmeSite, programmeEpisodeList);
			}

			// 去掉无视频链接的节目站点后
			if (!list.isEmpty()) {
				/*
				 * for (ProgrammeSite programmeSite : list) { //添加站点节目-->节目对应关系
				 * aliasInfo.programmeSite_programme.put(programmeSite,
				 * programme); }
				 */

				// 检查是否只有综合站点和另一个站点,是的话，直接移除综合站点
				if (list.size() == 2) {
					for (Iterator iterator = list.iterator(); iterator.hasNext();) {
						ProgrammeSite programmeSite = (ProgrammeSite) iterator.next();
						if (programmeSite.getSourceSite() == ProgrammeSiteType.综合.getValue()) {
							iterator.remove();
						}
					}
				}

				// 将站点排序
				Collections.sort(list, new SiteComparator());

				// 设置节目的默认播放链接
				List<ProgrammeEpisode> programmeEpisodeList = aliasInfo.programmeSite_episode.get(list.get(0));
				if (programmeEpisodeList != null && programmeEpisodeList.size() > 0) {
					programme.setPlayUrl(programmeEpisodeList.get(0).getUrl());
					programme.setPlayUrlSiteId(list.get(0).getSourceSite());
				}

				// 添加节目-->节目站点对应关系。
				aliasInfo.programme_programmeSite.put(programme, list);
			}
		}

	}

	private List<ProgrammeSite> sortList(List<ProgrammeSite> list) {
		// TODO Auto-generated method stub
		if (list == null || list.size() <= 0)
			return null;
		List<ProgrammeSite> resultList = new ArrayList<ProgrammeSite>();
		for (ProgrammeSite tmpSite : list) {
			if (tmpSite.getSourceSite() == ProgrammeSiteType.优酷网.getValue()) {
				resultList.add(0, tmpSite);
			} else {
				resultList.add(tmpSite);
			}
		}

		return resultList;

	}

	public static void addProgrammeSeries(Programme programme, AliasInfo aliasInfo) {
		if (programme == null)
			return;

		Series series = DbUtil.getSeriesByProgramme(programme);
		if (series != null && series.getId() > 0)
			aliasInfo.programme_series.put(programme, series);

	}

	private void loadMiddResource(AliasInfo aliasInfo) {
		// TODO Auto-generated method stub
		logger.info("开始加载AliasInfo的中间层map ");

		Criteria criteria = new Criteria();
		criteria.add(ProgrammePeer.STATE, ProgrammeState.NORMAL);
		criteria.add(ProgrammePeer.BLOCKED, 0);
		criteria.addAscendingOrderByColumn(ProgrammePeer.ID);

		criteria.setLimit(limit);
		int validCount = 0;
		for (int offset = 0; validCount < MAX_COUNT; offset += limit) {
			criteria.setOffset(offset);
			List<Programme> list = null;
			try {
				list = ProgrammePeer.doSelect(criteria);
			} catch (TorqueException e) {
				logger.error("获取programme list失败");
				e.printStackTrace();
				return;
			}

			if (list == null || list.size() <= 0)
				break;

			for (Programme programme : list) {

				String fields = "showname  showalias  tv_genre movie_genre  variety_genre anime_genre  releasedate "
						+ " director performer host area  showdesc";
				JSONObject middleResourceJson = MiddleResourceUtil.getProgrammeById(programme.getContentId(), fields);
				if (middleResourceJson != null) {

					aliasInfo.middMap.put(programme.getContentId(), middleResourceJson.toString());
				}

			}

			if (list.size() < limit) {
				break;
			}

		}

		logger.info("AliasInfo的中间层map 加载结束");

	}

	/**
	 * 填充 programme_programmeSite programme_series
	 * 
	 * @param programme
	 * @param aliasInfo
	 */
	public void addProgramme(Programme programme, AliasInfo aliasInfo) {
		if (programme == null)
			return;

		Criteria criteria = new Criteria();
		// order by order_id asc, completed desc, blocked 0, mid_empty
		// asc,source asc
		// update_time desc
		criteria.add(ProgrammeSitePeer.FK_PROGRAMME_ID, programme.getId());
		criteria.addAscendingOrderByColumn(ProgrammeSitePeer.ORDER_ID);
		criteria.addDescendingOrderByColumn(ProgrammeSitePeer.COMPLETED);
		criteria.add(ProgrammeSitePeer.BLOCKED, 0);
		criteria.addAscendingOrderByColumn(ProgrammeSitePeer.MID_EMPTY);
		criteria.addAscendingOrderByColumn(ProgrammeSitePeer.SOURCE);
		criteria.addDescendingOrderByColumn(ProgrammeSitePeer.UPDATE_TIME);

		List<ProgrammeSite> list = null;
		try {
			list = ProgrammeSitePeer.doSelect(criteria);
		} catch (TorqueException e) {
			logger.error("获取 programme site list 失败：programme's id:" + programme.getId());
			e.printStackTrace();
			return;
		}

		aliasInfo.programme_programmeSite.put(programme, list);

		Series series = DbUtil.getSeriesByProgramme(programme);

		if (series != null)
			aliasInfo.programme_series.put(programme, series);

	}

	/**
	 * 查询节目花絮,预告片, 按total_vv排序，取前两个. 如果个数少于2个，不显示
	 * 
	 * @param showid
	 * @return
	 *//*
	public static JSONArray searchTidbitsAndTrailer(int show_id) {
		StringBuilder query = new StringBuilder("show_id:");
		query.append(show_id);
		query.append(" show_videotype:花絮,预告片 state:normal");
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", "2");

		String fd_value = "videoid title streamtypes thumburl seconds show_videotype";
		params.put("fd", fd_value);
		params.put("ob", "total_vv:desc");

		// 如果中间层，获取失败{超时，内部出错}，重试3次，结果为空不算超时
		JSONObject jsonObject = NovaMiddleResource.search("video", "show", query.toString(), params);
		if (jsonObject == null || jsonObject.isNull("total")) {
			for (int i = 0; i < 3; i++) {// 在线请求使用，请求2次
				jsonObject = NovaMiddleResource.search("show", "show", query.toString(), params);
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
	*//**
	 * 查找节目看点
	 * @param show_id
	 * @return
	 *//*
	public static JSONArray searchVideoPoint(int show_id) {
		StringBuilder query = new StringBuilder("show_id:");
		query.append(show_id);
		query.append(" show_videotype:正片 state:normal");
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", "2");

		String fd_value = "videoid title streamtypes thumburl seconds show_videotype point";
		params.put("fd", fd_value);
		params.put("ob", "total_vv:desc");

		// 如果中间层，获取失败{超时，内部出错}，重试3次，结果为空不算超时
		JSONObject jsonObject = NovaMiddleResource.search("video", "show", query.toString(), params);
		logger.info(query.toString());
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
	}*/

	
	private boolean isTrailer6MonthAgo(int showYear, int showMonth) {
		Calendar c = Calendar.getInstance();
		int currentYear = c.get(Calendar.YEAR);
		int currentMonth = c.get(Calendar.MONTH) + 1;
		
		return (showYear == currentYear) && (currentMonth - showMonth) > 6;
	}

}
