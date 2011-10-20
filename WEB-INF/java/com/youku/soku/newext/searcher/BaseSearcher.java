/**
 * 
 */
package com.youku.soku.newext.searcher;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.newext.util.JSONUtil;
import com.youku.soku.newext.util.ProgrammeSiteType;
import com.youku.top.util.VideoType;

/**
 * @author liuyunjian 2011-5-11
 */
public class BaseSearcher {

	private static Log logger = LogFactory.getLog(BaseSearcher.class);

	/**
	 * @param programme
	 * @param middJson
	 * @return
	 */
	public static final String FEATURE_FLAG = "正片";

	public static JSONObject getProgrammeJson(Programme programme, JSONObject middJson) {
		JSONObject programmeJson = new JSONObject();
		if (programme != null) {
			try {
				programmeJson.put("name", StringUtils.trimToEmpty(programme.getName()));
				programmeJson.put("contentId", programme.getContentId());
				programmeJson.put("episodeTotal", programme.getEpisodeTotal());
				programmeJson.put("programmeId", programme.getId());
				programmeJson.put("updateTime", programme.getUpdateTime());
				programmeJson.put("alias", programme.getAlias());
				// programmeJson.put("url", programme.getPlayUrl());
				programmeJson.put("tpic", programme.getTrailerPic());
				if (programme.getSpecialTopic() != null) {
					programmeJson.put("specialTopic", new JSONArray(programme.getSpecialTopic()));
				}
				if (programme.getTidbit() != null) {
					programmeJson.put("tidbit", new JSONArray(programme.getTidbit()));
				}
				if (programme.getRecommendation() != null) {
					programmeJson.put("recommendation", new JSONArray(programme.getRecommendation()));
				}
				if (programme.getTrailerUrl() != null) {
					programmeJson.put("trailerUrl", programme.getTrailerUrl());
				}
				if (programme.getPoints() != null) {
					programmeJson.put("points", new JSONArray(programme.getPoints()));
				}
				if (programme.getVideoLang() != null) {
					programmeJson.put("audiolang", new JSONArray(programme.getVideoLang()));
				}

			} catch (JSONException e) {
			}
		}
		if (middJson != null) {
			try {
				programmeJson.put("pic", middJson.optString("show_thumburl"));
				programmeJson.put("vpic", middJson.optString("show_vthumburl"));
				programmeJson.put("brief", StringUtils.trimToEmpty(middJson.optString("showdesc")));
				double reputation = middJson.optDouble("reputation", 0);
				if (reputation < 1) {
					reputation = 5.0;
				}
				programmeJson.put("score", reputation);
				programmeJson.put("performer", middJson.optJSONArray("performer"));
				programmeJson.put("releaseyear", middJson.optString("releaseyear"));
				programmeJson.put("releasedate", middJson.optString("releasedate"));
				programmeJson.put("totalvv", middJson.optInt("showsum_vv"));
				programmeJson.put("showdayvv", middJson.optInt("showday_vv"));
				programmeJson.put("commentsCount", middJson.optInt("showtotal_comment"));
				programmeJson.put("paid", middJson.optInt("paid"));
				// 如果节目站内有正片，设置showid，站内搜索跳到站内详情页
				String showHasVideoType = middJson.optString("hasvideotype");
				if (showHasVideoType.contains(FEATURE_FLAG)) {
					programmeJson.put("showid", middJson.optString("showid"));
				}
				JSONArray stations = middJson.optJSONArray("tv_station");
				JSONArray outStations = new JSONArray();
				if (stations != null && stations.length() > 0) {
					for (int i = 0; i < stations.length(); i++) {
						outStations.put(stations.getJSONObject(i).optString("name"));
					}
				}
				programmeJson.put("station", outStations);
				programmeJson.put("showkeyword", middJson.optJSONArray("showkeyword"));
			} catch (JSONException e) {
			}
		}
		return programmeJson;
	}

	public static JSONObject wrapProgrammeSiteJson(int cateId, JSONObject programmeJson, int episodeTotal, JSONObject middJson, List<ProgrammeSite> programmeSiteList,
			List<ProgrammeEpisode> episodeList) throws JSONException {
		if (programmeSiteList != null && programmeSiteList.size() > 0) {
			List<Integer> programmeSiteIdList = new ArrayList<Integer>();
			JSONObject programmeSiteArr = new JSONObject();

			for (ProgrammeSite programmeSite : programmeSiteList) {

				JSONObject eleSite = new JSONObject();

				eleSite.put("firstLogo", StringUtils.trimToEmpty(programmeSite.getFirstLogo()));
				if (programmeSite.getSourceSite() == ProgrammeSiteType.优酷网.getValue() && programmeSite.getCompleted() != 1) {
					eleSite.put("streamtypes", middJson.optJSONArray("streamtypes") == null ? "[]" : middJson.optJSONArray("streamtypes"));
					programmeJson.put("update_notice", StringUtils.trimToEmpty(middJson.optString("update_notice")));
				}

				if (episodeList != null && episodeList.size() > 0) {
					String displayStatus = "";
					if (programmeSite.getSortmode() == 0) {
						displayStatus = getUpdateStatus(cateId, episodeTotal, episodeList.size(), episodeList.get(episodeList.size() - 1));
					} else {
						displayStatus = getUpdateStatus(cateId, episodeTotal, episodeList.size(), episodeList.get(0));
					}
					eleSite.put("display_status", displayStatus);
				}

				JSONArray episodeArr = BaseSearcher.getEpisodesJsonArray(episodeList, eleSite);
				if (!com.youku.soku.newext.util.JSONUtil.isEmpty(episodeArr)) {
					programmeSiteIdList.add(programmeSite.getSourceSite());
					programmeSiteArr.put(new Integer(programmeSite.getSourceSite()).toString(), eleSite);
				}

			}

			if (com.youku.soku.newext.util.JSONUtil.isEmpty(programmeSiteArr)) {
				return null;
			}

			programmeJson.put("url", BaseSearcher.getPlayUrl(programmeSiteArr));
			// returnJson.put("ProgrammeSite", programmeSiteArr);

			if (programmeSiteIdList != null && programmeSiteIdList.size() > 0) {
				// 处理结果
				List<String> tmpSiteList = new ArrayList<String>();

				for (int i = 0; i < programmeSiteIdList.size(); i++) {
					tmpSiteList.add(programmeSiteIdList.get(i).intValue() + ":" + ProgrammeSiteType.siteMap.get(programmeSiteIdList.get(i).intValue()));
					logger.debug("add :" + ProgrammeSiteType.siteMap.get(programmeSiteIdList.get(i).intValue()));
				}

				programmeJson.put("sites", tmpSiteList);
			}
			
			return programmeSiteArr;
		}
		
		return null;
	}

	/**
	 * 
	 * @param completed
	 * @param total
	 *            节目总集数
	 * @param esize
	 *            视频列表集数
	 * @param lastOrder
	 *            最后一集的序号
	 * @return
	 */
	public static String getUpdateStatus(int cateId, int total, int esize, ProgrammeEpisode pEpisode) {
		
		if(cateId == VideoType.movie.getValue()) {
			return "正片";
		}
		
		if (pEpisode == null) {
			return esize + "集全";
		}
		int lastOrder = pEpisode.getOrderId();
		if (pEpisode.getOrderStage() > 0) {
			lastOrder = pEpisode.getOrderStage();
		}
		if (total > 0) {
			if (esize >= total) {
				return esize + "集全";
			} else {
				return "更新至" + lastOrder + (lastOrder > 2000 ? "" : "集");
			}
		} else {
			return "更新至" + lastOrder + (lastOrder > 2000 ? "" : "集");
		}
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
			tmp.put("voice", middJson.optJSONArray("voice"));
			JSONArray animeEditions = middJson.optJSONArray("anime_edition");
			if (animeEditions != null && animeEditions.length() > 0) {
				// JSONObject edition = animeEditions.optJSONObject(0);
				tmp.put("anime_edition", animeEditions.optString(0));
			}

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
	public static JSONArray getEpisodesJsonArray(List<ProgrammeEpisode> episodeList, JSONObject eleSite) {
		JSONArray episodeArr = new JSONArray();
		try {
			if (episodeList != null && episodeList.size() > 0) {
				for (ProgrammeEpisode episode : episodeList) {
					JSONObject eleJson = new JSONObject();

					// 有播放地址的影片才加入
					String url = StringUtils.trimToEmpty(episode.getUrl());
					if (url == null || url.length() <= 0)
						continue;
					eleJson.put("url", url);

					eleJson.put("name", StringUtils.trimToEmpty(episode.getTitle()));
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
		if (JSONUtil.isEmpty(programmeSiteArr)) {
			return "";
		}
		String firstKey = (String) programmeSiteArr.keys().next();
		JSONArray episodeArr = programmeSiteArr.optJSONObject(firstKey).optJSONArray("episodes");
		if (JSONUtil.isEmpty(episodeArr)) {
			return "";
		}

		return episodeArr.optJSONObject(0).optString("url");
	}

}
