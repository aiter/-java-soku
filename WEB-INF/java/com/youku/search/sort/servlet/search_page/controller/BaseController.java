package com.youku.search.sort.servlet.search_page.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.config.Config;
import com.youku.search.sort.entity.CategoryMap.Category;
import com.youku.search.sort.json.util.JSONUtil;
import com.youku.search.sort.servlet.search_page.WebParam;
import com.youku.search.sort.servlet.search_page.util.KeywordFilter;
import com.youku.search.sort.servlet.search_page.util.NovaMiddleResource;
import com.youku.search.sort.servlet.search_page.util.NovaMiddleResourceUtil;
import com.youku.search.util.MyUtil;
import com.youku.search.util.StringUtil;
import com.youku.search.util.Wget;
import com.youku.soku.sort.word_match.MatcherResult;
import com.youku.soku.sort.word_match.Program;
import com.youku.soku.sort.word_match.ProgramMatcher;
import com.youku.soku.zhidaqu.v2.DictManager;
import com.youku.soku.zhidaqu.v2.Element;
import com.youku.soku.zhidaqu.v2.TokenType;

public class BaseController {
	public final Log logger = LogFactory.getLog(getClass());
	static final int MAX_PROGRAMME_COUNT = 5;

	public void commendOdshow(String keyword, Category category,
			JSONObject result) throws Exception {

		String keyword_old = keyword;
		if (keyword_old == null) {
			keyword_old = "";
		}

		keyword = KeywordFilter.filterSymbol(keyword_old);

		if (logger.isDebugEnabled()) {
			logger.debug("开始查询版权数据, old keyword: " + keyword_old
					+ ", valid keyword: " + keyword);
		}

		if (keyword.isEmpty()) {
			logger.debug("查询关键词为空, 返回空版权数据");
			return;
		}

		// commendOdshowNew(keyword,category,result);

		// TODO
		/*
		 * if (logger.isDebugEnabled()) { logger.debug("开始模糊匹配处理, keyword: " +
		 * keyword); }
		 * 
		 * List<Token> tokens = WordMatcher.processTokens(keyword);
		 * 
		 * if (logger.isDebugEnabled()) { logger.debug("模糊匹配处理初始结果, keyword: " +
		 * keyword + "; tokens: " + tokens); }
		 * 
		 * if (tokens == null || tokens.isEmpty()) { if
		 * (logger.isDebugEnabled()) { logger.debug("模糊匹配处理结果为空, 返回"); } return; }
		 */

		String firstNotPerson = keyword;
		// TODO TokenType firstNotPersonType = null;

		String firstPerson = keyword;

		// TODO
		/*
		 * for (Token token : tokens) { if (firstNotPerson != null &&
		 * firstPerson != null) { break; }
		 * 
		 * String value = token.getValue(); if (value == null ||
		 * value.isEmpty()) { continue; }
		 * 
		 * Element[] elements = token.getElements(); for (int i = 0; elements !=
		 * null && i < elements.length; i++) { if (firstNotPerson != null &&
		 * firstPerson != null) { break; }
		 * 
		 * Element e = elements[i]; if (e == null || e.getType() == null) {
		 * continue; } if (TokenType.PERSON == e.getType() && firstPerson ==
		 * null) { firstPerson = value; } if (TokenType.PERSON != e.getType() &&
		 * e.isRV() && firstNotPerson == null) { firstNotPerson = value;
		 * firstNotPersonType = e.getType(); } } }
		 * 
		 * if (logger.isDebugEnabled()) { logger.debug("模糊匹配处理最终结果, firstPerson: " +
		 * firstPerson + "; firstNotPerson: " + firstNotPerson + "/" +
		 * firstNotPersonType + "; tokens: " + tokens); }
		 */

		// 获取系列节目
		keyword = firstNotPerson;
		if (logger.isDebugEnabled()) {
			logger.debug("开始查询系列节目信息, keyword: " + keyword);
		}
		JSONObject result_seriesshow = getSeriesShow(keyword, category);
		if (!JSONUtil.isEmpty(result_seriesshow)) {

			result.put("series_odshows", result_seriesshow);

			// 指数统计用
			JSONArray showids = result_seriesshow.optJSONArray("showids");

			if (!JSONUtil.isEmpty(showids)) {
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < showids.length(); i++) {
					if (i > 0) {
						builder.append(",");
					}
					builder.append(showids.optString(i));
				}

				result.put("search_showids", builder.toString());
			}

			if (logger.isDebugEnabled()) {
				logger.debug("查询到了系列节目信息, keyword: " + keyword);
			}
			// return;
		}

		// 是否是人物
		keyword = firstPerson;
		if (logger.isDebugEnabled()) {
			logger.debug("开始查询人物信息, keyword: " + keyword);
		}
		JSONObject result_person = getOdshowsByPerson(keyword, category);

		JSONObject person_info = JSONUtil.getProperty(result_person,
				JSONObject.class, "person_info");

		JSONArray person_odshows = JSONUtil.getProperty(result_person,
				JSONArray.class, "results");

		if (!JSONUtil.isEmpty(person_info) && !JSONUtil.isEmpty(person_odshows)) {

			JSONObject person_alias = result_person
					.optJSONObject("person_alias");

			if (!JSONUtil.isEmpty(person_alias)) {
				JSONArray suggestionArray = new JSONArray();

				Object suggestion = result.opt("suggestion");

				if (suggestion instanceof String) {
					suggestionArray.put(suggestion);
					result.put("suggestion", suggestionArray);
				} else if (suggestion instanceof JSONArray) {
					suggestionArray = (JSONArray) suggestion;
				} else {
					result.put("suggestion", suggestionArray);
				}
				suggestionArray.put(result_person.optString("personname"));
			}

			result.put("person_odshows", person_odshows);
			result.put("person_info", person_info);

			if (logger.isDebugEnabled()) {
				logger.debug("查询到了人物信息, keyword: " + keyword);
			}
			return;
		}

		// 是否是节目
		keyword = firstNotPerson;
		if (logger.isDebugEnabled()) {
			logger.debug("开始查询节目信息, keyword: " + keyword);
		}

		JSONArray odshowids = new JSONArray();
		// 如果在系列中，展示了一步分数据，将这部分showids加入
		JSONObject resultSeriesshow = result.optJSONObject("series_odshows");
		if (resultSeriesshow != null) {
			odshowids = resultSeriesshow.optJSONArray("showids");
		}
		JSONObject odshows = getOdshows(keyword, category, resultSeriesshow);
		if (!JSONUtil.isEmpty(odshows)) {
			Iterator<String> keyIterator = odshows.keys();
			while (keyIterator.hasNext()) {
				String key = keyIterator.next();
				JSONObject odshow = odshows.optJSONObject(key);
				if (odshow == null) {
					continue;
				}

				odshowids.put(odshow.opt("showid"));
				JSONArray show_alias = odshow.optJSONArray("show_alias");
				if (!JSONUtil.isEmpty(show_alias)) {

					JSONArray suggestionArray = new JSONArray();

					Object suggestion = result.opt("suggestion");

					if (suggestion instanceof String) {
						suggestionArray.put(suggestion);
						result.put("suggestion", suggestionArray);
					} else if (suggestion instanceof JSONArray) {
						suggestionArray = (JSONArray) suggestion;
					} else {
						result.put("suggestion", suggestionArray);
					}
					suggestionArray.put(odshow.optString("showname"));
				}

				// int episode_total = odshow.optInt("episode_total");
				// if(episode_total == 0) {
				// episode_total = odshow.optInt("episode_last");
				// }
				if (odshow.optString("showcategory").equals("综艺")) {
					odshow.put("episodes", getSeriesShowVideo(odshow
							.optString("showid"), 0));
				} else {

					String query = "show_id:" + odshow.optString("pk_odshow")
							+ " show_videotype:正片";
					Map<String, String> params = new LinkedHashMap<String, String>();
					params.put("pn", "1");
					params.put("pl", "1000");

					String fd_value = "title state show_videostage show_videoseq show_videocompleted";
					params.put("fd", fd_value);

					params.put("ob", "show_videoseq:asc");
					params.put("cl", "search_result");

					JSONObject videoResultObject = NovaMiddleResource.search(
							"video", "show", query, params);
					JSONArray episodes = JSONUtil.getProperty(
							videoResultObject, JSONArray.class, "results");
					if (!JSONUtil.isEmpty(episodes)) {
						for (int i = 0; i < episodes.length(); i++) {
							JSONObject video = episodes.optJSONObject(i);
							if (video == null) {
								continue;
							}

							if (video.optInt("show_videostage") == 0) {
								video.put("show_videostage", 0);
							}
							if (video.optInt("show_videoseq") == 0) {
								video.put("show_videoseq", 0);
							}
							if (video.optInt("show_videocompleted") == 0) {
								video.put("show_videocompleted", 0);
							}

							int show_videostage = video
									.optInt("show_videostage");
							if (show_videostage == 0) {
								continue;
							}
							if (show_videostage < 10) {
								video.put("show_videostage", "0"
										+ show_videostage);
							} else {
								video.put("show_videostage", ""
										+ show_videostage);
							}
						}

						JSONArray odshow_episodes = new JSONArray();
						odshow.put("episodes", odshow_episodes);
						for (int i = 0; i < Integer.MAX_VALUE; i++) {
							JSONObject episodesObject = new JSONObject();
							JSONArray items = new JSONArray();
							for (int j = i * 30; j < (i + 1) * 30
									&& j < episodes.length(); j++) {
								items.put(episodes.optJSONObject(j));
							}
							if (items.length() == 0) {
								break;
							}

							JSONObject first = items.optJSONObject(0);
							JSONObject last = items.optJSONObject(items
									.length() - 1);

							StringBuilder title = new StringBuilder();
							title.append("第");
							title.append(first.optString("show_videostage"));
							if (items.length() > 1) {
								title.append("-");
								title.append(last.optString("show_videostage"));
							}
							title.append("集");

							episodesObject.put("title", title.toString());
							episodesObject.put("items", items);

							odshow_episodes.put(episodesObject);
						}

						odshow.put("onlyone", odshow_episodes.length() == 1);
					}

					//
					query = "show_id:" + odshow.optString("pk_odshow")
							+ " show_videotype:预告片 state:normal";

					params = new LinkedHashMap<String, String>();
					params.put("pn", "1");
					params.put("pl", "2");
					params.put("fd", "title");
					params.put("ob", "show_videoseq:asc");
					params.put("cl", "search_result");
					videoResultObject = NovaMiddleResource.search("video",
							"show", query, params);
					episodes = JSONUtil.getProperty(videoResultObject,
							JSONArray.class, "results");

					if (!JSONUtil.isEmpty(episodes)) {
						odshow.put("remains", episodes);
					}
				}

			}

			result.put("odshows", odshows);

			StringBuilder search_showids = new StringBuilder();
			for (int i = 0; i < odshowids.length(); i++) {
				if (i > 0) {
					search_showids.append(",");
				}
				search_showids.append(odshowids.optString(i));

			}
			result.put("search_showids", search_showids.toString());// 指数统计用

			if (logger.isDebugEnabled()) {
				logger.debug("查询到了节目信息, keyword: " + keyword);
			}
			return;
		}
	}

	/**
	 * @param keyword
	 * @param category
	 * @param result
	 */
	public void commendOdshowNew(String keyword, Category category,
			JSONObject result) {
		String keyword_old = keyword;
		if (keyword_old == null) {
			keyword_old = "";
		}

		keyword = KeywordFilter.filterSymbol(keyword_old);

		if (logger.isDebugEnabled()) {
			logger.debug("开始查询版权数据, old keyword: " + keyword_old
					+ ", valid keyword: " + keyword);
		}

		if (keyword.isEmpty()) {
			logger.debug("查询关键词为空, 返回空版权数据");
			return;
		}
		// TODO
		MatcherResult fuzzyResult = ProgramMatcher.getYoukuMatcher().match(
				StringUtils.trimToEmpty(keyword),null,20);
		if (fuzzyResult != null) {
//			logger.debug("fuzzyKey:" + fuzzyResult.getParamKeyAsString());
			List<Program> proList = fuzzyResult.getMatchResult();

			//logger.debug("the proList:" + Arrays.toString(proList.toArray()));

			if (proList != null && proList.size() > 0) {
				StringBuilder seriesQuery = new StringBuilder();
				StringBuilder showQuery = new StringBuilder();
				StringBuilder personQuery = new StringBuilder();
				seriesQuery.append("seriesid:");
				showQuery.append("showid:");
				personQuery.append("personalias:");
				boolean isHaveSeries = false;
				boolean isHaveShow = false;
				boolean isHavePerson = false;
				Program program = null;
				Map<Integer, String> seriesNameMap = new HashMap<Integer, String>();   //系列Id和对于的系列名，用于确认默认显示的节目
				for (int i = 0; i < proList.size(); i++) {
					program = proList.get(i);
					// logger.debug("the key: "+program.getKeyword());
					// logger.debug("the type:
					// "+program.getElement().getType());
					// logger.debug("the series:
					// "+program.getElement().getSeries());
					if (TokenType.PERSON.equals(program.getElement().getType())) {
						personQuery.append("\"").append(program.getKeyword())
								.append("\"|");
						isHavePerson = true;
					} else if (program.getElement().getProgramId() == 0
							&& program.getElement().getSeries() > 0) {
						seriesQuery.append(program.getElement().getSeries())
								.append(",");
						seriesNameMap.put(program.getElement().getSeries(), program.getKeyword());
						isHaveSeries = true;
					} else {
						showQuery.append(program.getElement().getProgramId())
								.append(",");
						isHaveShow = true;
					}
				}
				if (seriesQuery.lastIndexOf(",") == seriesQuery.length() - 1) {
					seriesQuery = seriesQuery
							.deleteCharAt(seriesQuery.length() - 1);
				}
				if (showQuery.lastIndexOf(",") == showQuery.length() - 1) {
					showQuery = showQuery.deleteCharAt(showQuery.length() - 1);
				}
				if (personQuery.lastIndexOf("|") == personQuery.length() - 1) {
					personQuery = personQuery
							.deleteCharAt(personQuery.length() - 1);
				}
				// logger.debug("series-id-list:"+seriesQuery.toString());
				// logger.debug("query-id-list:"+showQuery.toString());
				// logger.debug("person:"+personQuery.toString());
				if (!isHaveSeries && !isHaveShow && !isHavePerson) {
					return;
				}

				String categoryStr = "电影,电视剧,综艺,体育,教育,动漫";
				if (category != null) {
					// 2011.3.17 目前节目只支持，电影,电视剧,综艺,体育,教育
					if ("电影".equals(category.name)) {
						categoryStr = category.name;
					} else if ("电视剧".equals(category.name)) {
						categoryStr = category.name;
					} else if ("综艺".equals(category.name)) {
						categoryStr = category.name;
					} else if ("体育".equals(category.name)) {
						categoryStr = category.name;
					} else if ("教育".equals(category.name)) {
						categoryStr = category.name;
					} else if ("动漫".equals(category.name)) {
						categoryStr = category.name;
					} else {
						return;
					}
				}

				JSONArray showsArray = null;
				Map<String, String> params = new LinkedHashMap<String, String>();
				params.put("pn", "1");
				params.put("pl", "20");// 模糊匹配，最多返回20个节目信息
				params
						.put(
								"fd",
								"showname showalias releaseyear releasemonth episode_total episode_last completed showcategory showtotal_vv showtotal_comment reputation"
										+ " performer tv_genre movie_genre  variety_genre anime_genre sports_genre edu_genre mv_genre allowfilter "
										+ " director  host teacher screenwriter executive_producer issuer distributor production station tv_station update_notice hasvideotype"
										+ " area showdesc show_thumburl show_vthumburl firstepisode_videourl trailer_videourl streamtypes sortmode paid seriesid series showsum_vv showtotal_comment");
				params.put("ob", "releasedate:desc showseq:desc");
				params.put("cl", "search_result");

				/**
				 * 1、综艺才展示系列 2、系列中展示过的节目，下面的节目也不展示（需要排重） 3、站内外的数据需要排重
				 * 4、站内外的数据需要都按时间倒排
				 */

				Set<Integer> showedIdsSet = new HashSet<Integer>();
				/** 1、查找节目信息 */
				if (isHaveSeries) {
					seriesQuery.append(" state:normal allowfilter:1").append(
							" showcategory:").append(categoryStr);
					logger.info("isHaveSeries" + isHaveSeries);
					JSONObject showsResultObject = NovaMiddleResource.search(
							"show", "show", seriesQuery.toString(), params);
					// logger.debug("the seriesQuery:
					// "+seriesQuery.toString());

					showsArray = JSONUtil.getProperty(showsResultObject,
							JSONArray.class, "results");
					if (null != showsArray && showsArray.length() > 0) {
						// 如果有系列节目，而且包含综艺分类的数据，那么就只去这个系列中的一个节目数据
						JSONArray newArray = new JSONArray();
						Set<Integer> seriesSet = new HashSet<Integer>();
						Set<Integer> seriesIdWithSameProgramme = new HashSet<Integer>();
						for (int i = 0; i < showsArray.length(); i++) {
							JSONObject showJsonObject = showsArray.optJSONObject(i);
							int seriesid = showJsonObject.optInt("seriesid", 0);
							if(showJsonObject.optString("showname").equals(seriesNameMap.get(seriesid))) {
								seriesIdWithSameProgramme.add(seriesid);
							}
						}
						for (int i = 0; i < showsArray.length(); i++) {
							JSONObject showJsonObject = showsArray.optJSONObject(i);
							int seriesid = showJsonObject.optInt("seriesid", 0);
							showedIdsSet.add(showJsonObject.optInt("pk_odshow"));// 用于排重
							if (seriesid != 0) {
								if ("综艺".equals(showJsonObject
										.optString("showcategory"))) {
									if (!seriesSet.contains(seriesid)) {
										if(seriesIdWithSameProgramme.contains(seriesid)) {
											if(showJsonObject.optString("showname").equals(seriesNameMap.get(seriesid))) {
												newArray.put(showJsonObject);
												seriesSet.add(seriesid);
											}
										} else {
											newArray.put(showJsonObject);
											seriesSet.add(seriesid);
										}
									}
								} else {
									newArray.put(showJsonObject);
								}
							} else {
								newArray.put(showJsonObject);
							}
						}
						showsArray = newArray;
					}
				} else {
					logger.debug("没有系列");
				}

				if (isHaveShow) {

					showQuery.append(" state:normal ").append(
							" showcategory:").append(categoryStr);
					// logger.debug("have show: "+showQuery.toString());
					params.put("pl", "20");
					JSONObject showsResultObject = NovaMiddleResource.search(
							"show", "show", showQuery.toString(), params);

					if (JSONUtil.isEmpty(showsArray)) {
						showsArray = JSONUtil.getProperty(showsResultObject,
								JSONArray.class, "results");
						
						
						JSONArray tmp=new JSONArray();
						if(!JSONUtil.isEmpty(showsArray)){
							for(int i=0;i<showsArray.length();i++){
								if(showsArray.optJSONObject(i).optString("showcategory").equals("动漫")){
									tmp.put(showsArray.optJSONObject(i));
								}else if(showsArray.optJSONObject(i).optInt("allowfilter")==1){
									tmp.put(showsArray.optJSONObject(i));
								}
							
							}
							
						}
						
						showsArray=tmp;
						
					} else {

						JSONArray alltmp = JSONUtil.getProperty(showsResultObject,
								JSONArray.class, "results");
						JSONArray tmp=new JSONArray();
//						将非动漫的allowfilter=0的去掉
						if(!JSONUtil.isEmpty(alltmp)){
							for(int i=0;i<alltmp.length();i++){
								if(alltmp.optJSONObject(i).optString("showcategory").equals("动漫")){
									tmp.put(alltmp.optJSONObject(i));
								}else if(alltmp.optJSONObject(i).optInt("allowfilter")==1){
									tmp.put(alltmp.optJSONObject(i));
								}
							
							}
							
						}						
						if (!JSONUtil.isEmpty(tmp)) {
							for (int i = 0; i < tmp.length(); i++) {
								showsArray.put(tmp.opt(i));
								showedIdsSet.add(tmp.optJSONObject(i).optInt(
										"pk_odshow"));// 用于排重
							}
						}
					}
				}

				if (!JSONUtil.isEmpty(showsArray)) {

					StringBuilder showidsBbuilder = new StringBuilder();

					for (int i = 0; i < showsArray.length(); i++) {
						JSONObject showObject = showsArray.optJSONObject(i);
						if (JSONUtil.isEmpty(showObject)) {
							continue;
						}
						/** 1.2、查找节目是否在系列中，如果是，查找系列下的其他节目 */
						int seriesid = showObject.optInt("seriesid", 0);
						if (seriesid > 0) {
							String query = "seriesid:" + seriesid + " -showid:"
									+ showObject.optInt("pk_odshow")
									+ " state:normal allowfilter:1";
							// logger.debug("the query:"+query);
							params = new LinkedHashMap<String, String>();
							params.put("pn", "1");
							params.put("pl", "10");
							params.put("ob", "showseq:desc");
							params.put("fd", "showname showid");
							params.put("cl", "search_result");

							logger.info("查找系列下的节目");
							JSONObject showResultObject = NovaMiddleResource
									.search("show", "show", query, params);
							
							logger.info("showResultObject" + showResultObject);

							JSONArray showArray = JSONUtil.getProperty(
									showResultObject, JSONArray.class,
									"results");

							try {
								showObject.put("series", showArray);
							} catch (JSONException e) {

							}
						}
						/** 1.3、查找节目的视频 */
						String query = "show_id:"
								+ showObject.optInt("pk_odshow")
								+ " show_videotype:正片 state:normal";
						params = new LinkedHashMap<String, String>();
						params.put("pn", "1");
						params.put("pl", "1000");

						String fd_value = "title thumburl state show_videostage show_videoseq show_videocompleted";
						params.put("fd", fd_value);
						params.put("ob", "show_videoseq:"
								+ (showObject.optInt("sortmode") == 1 ? "desc"
										: "asc"));
						params.put("cl", "search_result");

						JSONObject videoResultObject = NovaMiddleResource
								.search("video", "show", query, params);

						JSONArray videoArray = null;
						try {
							if (null != videoResultObject) {
								videoArray = videoResultObject
									.optJSONArray("results");
								showObject.put("videos", videoArray);
							}
						} catch (JSONException e) {
						}

						logger.debug("showObject.optString(showcategory)" + showObject.optString("showcategory"));
						
						if (shouldTrailerDisplay(showObject.optString("showcategory"), showObject.optInt("releaseyear"), showObject.optInt("releasemonth"))) {
							/** 1.4、电影分类，需要相关片段。花絮，预告片 */
							query = "show_id:" + showObject.optString("pk_odshow") + " show_videotype:预告片 state:normal";
							params = new LinkedHashMap<String, String>();
							params.put("pn", "1");
							params.put("pl", "2");
							params.put("fd", "title");
							params.put("ob", "show_videoseq:asc");
							params.put("cl", "search_result");
							videoResultObject = NovaMiddleResource.search("video", "show", query, params);
							videoArray = JSONUtil.getProperty(videoResultObject, JSONArray.class, "results");
							if (!JSONUtil.isEmpty(videoArray)) {
								try {
									showObject.put("remains", videoArray);
								} catch (JSONException e) {
								}
							}
						}
						// 1.6、统计所有的showid
						if (i > 0) {
							showidsBbuilder.append(",");
						}
						showidsBbuilder.append(showObject.optString("showid"));
					}

					try {
						result
								.put("search_showids", showidsBbuilder
										.toString());

					} catch (JSONException e) {
					}
				}

				// 2011.6.8 将站外直达区合并到直达区中,未合并人物直达区
				JSONObject outSearchResult = getOutSearchResult(keyword, "2");

				handleExt(showsArray, outSearchResult, showedIdsSet, keyword);
				
				if(showsArray!=null){
					for(int i=0;i<showsArray.length();i++){
						JSONObject showObject=showsArray.optJSONObject(i);
						// 1.5、设置一些需要计算或设置的属性
						try {
							setDisplayStatus(showObject, fuzzyResult);
						} catch (JSONException e) {
							logger.error(e.getMessage(),e);
						}
					}
				}

				// 根据releaseyear 重新排序
				showsArray = sortShows(showsArray);
				try {
					if (!JSONUtil.isEmpty(outSearchResult)
							&& !JSONUtil.isEmpty(outSearchResult, "ext")) {
						JSONObject newOutSearchResult = new JSONObject();
						newOutSearchResult.put("ext", outSearchResult
								.opt("ext"));
						result.put("outSearchResult", newOutSearchResult);
					}
					result.put("odshows", showsArray);
				} catch (JSONException e) {
				}

				/** 2、处理人物 */
				if (isHavePerson) {
					personQuery.append(" state:normal");

					params = new LinkedHashMap<String, String>();
					params.put("pn", "1");
					params.put("pl", "5");
					params.put("fd", "personid personname thumburl week_vv");
					params.put("cl", "search_result");
					params.put("ob", "week_vv:desc");

					JSONObject personsResultObject = NovaMiddleResource.search(
							"person", "person", personQuery.toString(), params);

					JSONArray personsArray = JSONUtil.getProperty(
							personsResultObject, JSONArray.class, "results");
					if (!JSONUtil.isEmpty(personsArray)) {
						Set<String> personNameSet = new HashSet<String>();  //人物如果同名，取week_vv比较高的那个
						for (int i = 0; i < personsArray.length(); i++) {
							JSONObject personObject = personsArray
									.optJSONObject(i);
							if (JSONUtil.isEmpty(personObject)) {
								continue;
							}

							String personName = personObject.optString("personname");
							
							if(personNameSet.contains(personName)) {
								continue;
							}
							
							personNameSet.add(personName);
							
							boolean isPersonAccurateMatch = false;
							logger.debug(proList);
							if (proList != null && proList.size() > 0) {
								for (int j = 0; j < proList.size(); j++) {
									program = proList.get(j);
									logger.debug("program.getKeyword()" + program.getKeyword());
									if(program.getKeyword() != null && program.getKeyword().equals(personName)) {
										isPersonAccurateMatch = true;
									}
								}
							}
							
							if(!isPersonAccurateMatch) {
								continue;
							}
						

							String query = "person:"
									+ personName
									+ " state:normal allowfilter:1 hasvideotype:正片,预告片 showcategory:"
									+ categoryStr;
							params = new LinkedHashMap<String, String>();
							params.put("pn", "1");
							params.put("pl", "1000");
							params
									.put(
											"fd",
											"showname showalias releaseyear releasemonth episode_total episode_last completed episode_collected showcategory  reputation"
													+ "   show_thumburl show_vthumburl firstepisode_videourl trailer_videourl streamtypes  paid person hasvideotype ");
							params.put("ob", "releasedate:desc");
							params.put("cl", "search_result");

							JSONObject showsResultObject = NovaMiddleResource
									.search("show", "show", query, params);

							showsArray = JSONUtil.getProperty(
									showsResultObject, JSONArray.class,
									"results");
							showsArray = filterPerson(personObject
									.optString("personid"), showsArray);
							try {
								personObject.put("highLightName",
										highlight(keyword));
								if (!JSONUtil.isEmpty(showsArray)) {
									personObject.put("videos", showsArray);
								}
							} catch (JSONException e) {
							}
						}

						try {
							result.put("person_odshows", personsArray);
						} catch (JSONException e) {
						}
					}
				}
			}
		} else {
			logger.debug("没有模糊匹配的结果");
		}
	}
	
	private boolean shouldTrailerDisplay(String showCategory, int showYear, int showMonth) {
		logger.debug("showCategory: " + showCategory + "showYear" + showYear + "showMonth" + showMonth);
		Calendar c = Calendar.getInstance();
		int currentYear = c.get(Calendar.YEAR);
		int currentMonth = c.get(Calendar.MONTH) + 1;
		
		return showCategory.equals("电影") && showYear == currentYear && (currentMonth - showMonth) < 6;
	}

	/**
	 * @param showsArray
	 */
	private JSONArray sortShows(JSONArray showsArray) {
		if (JSONUtil.isEmpty(showsArray)) {
			return showsArray;
		}

		List<JSONObject> list = new ArrayList<JSONObject>();

		for (int i = 0; i < showsArray.length(); i++) {
			list.add(showsArray.optJSONObject(i));
		}

		Collections.sort(list, new Comparator<JSONObject>() {
			@Override
			public int compare(JSONObject o1, JSONObject o2) {
				if (JSONUtil.isEmpty(o1) && JSONUtil.isEmpty(o2)) {
					return 0;
				}
				if (JSONUtil.isEmpty(o1)) {
					return -1;
				}
				if (JSONUtil.isEmpty(o2)) {
					return 1;
				}

				return o2.optInt("releaseyear", 0)
						- o1.optInt("releaseyear", 0);
			}
		});

		JSONArray tmp = new JSONArray();
		for (JSONObject job : list) {
			tmp.put(job);
		}
		return tmp;
	}

	/**
	 * @param showsArray
	 * @param outSearchResult
	 * @param showedIdsSet
	 * @param keyword
	 */
	private void handleExt(JSONArray showsArray, JSONObject outSearchResult,
			Set<Integer> showedIdsSet, String keyword) {
		if (JSONUtil.isEmpty(showsArray)) {
			showsArray = new JSONArray();
		}

		if (JSONUtil.isEmpty(outSearchResult)) {
			return;
		}

		JSONObject extInfo = outSearchResult.optJSONObject("ext_info");
		if (JSONUtil.isEmpty(extInfo)) {
			return;
		}

		for (Iterator iter = extInfo.keys(); iter.hasNext();) {
			JSONObject searchshow = extInfo.optJSONObject(iter.next()
					.toString());
			if (JSONUtil.isEmpty(searchshow))
				continue;
			String showcategory = searchshow.optString("nameTypes");
			if (!showcategory.equals("ANIME")) {
				continue;
			}
			JSONArray searchShowsArray = searchshow.optJSONArray(showcategory);
			if (!JSONUtil.isEmpty(searchShowsArray)) {
				for (int i = 0; i < searchShowsArray.length(); i++) {
					JSONObject showObject = searchShowsArray.optJSONObject(i);
					JSONObject middJson = showObject.optJSONObject("midd");
					JSONObject proJson = showObject.optJSONObject("programme");
					JSONObject siteJson = showObject
							.optJSONObject("ProgrammeSite");

					if (JSONUtil.isEmpty(middJson) || JSONUtil.isEmpty(proJson)
							|| JSONUtil.isEmpty(siteJson)) {
						continue;
					}

					JSONObject odshow = new JSONObject();
					if (showedIdsSet.contains(proJson.optInt("contentId"))) {
						continue;
					}

					if (JSONUtil.isEmpty(siteJson, "14")) {
						continue;
					}
					JSONObject videoJsonObject = siteJson.optJSONObject("14");

					// 将站外的直达区数据属性，对应到站内的数据属性上
					try {
						odshow.put("pk_odshow", proJson.optString("contentId"));
						odshow.put("pk_grogramme", proJson
								.optString("programmeId"));
						String showname = proJson.optString("name");
						odshow.put("showname", showname);
						JSONArray aliasArray = getAlias(proJson
								.optString("alias"));
						odshow.put("showalias", aliasArray);
						if (MyUtil.isSimilar(keyword,showname)) {
							odshow.put("highLightName", highlight(showname,
									keyword));
						} else {
							// 找别名
							if (!JSONUtil.isEmpty(aliasArray)) {
								for (int j = 0; j < aliasArray.length(); j++) {
									String aliasName = aliasArray.optString(j);
									if (aliasName.startsWith(keyword)) {
										odshow.put("name", aliasName);
										odshow.put("highLightName", highlight(
												aliasName, keyword));
										break;
									}
								}
							}

							odshow.put("aliasName", showname);
						}

						odshow.put("releaseyear", proJson
								.optString("releaseyear"));
						odshow.put("episode_total", proJson
								.optString("episodeTotal"));
						odshow.put("showcategory", getCategory(showcategory));
						odshow.put("reputation", proJson.opt("score"));
						odshow.put("performer", proJson.opt("performer"));
						odshow.put("host", proJson.opt("host"));
						if (JSONUtil.isEmpty(odshow, "host")) {
							odshow.put("host", middJson.opt("host"));
						}
						odshow.put(getGenreName(showcategory), middJson
								.opt("genre"));
						odshow.put("director", middJson.opt("director"));
						odshow.put("update_notice", proJson
								.opt("update_notice"));
						odshow.put("area", middJson.opt("area"));
						odshow.put("showdesc", proJson.opt("brief"));
						odshow.put("show_thumburl", proJson.opt("pic"));
						odshow.put("show_vthumburl", proJson.opt("vpic"));

						odshow.put("streamtypes", videoJsonObject
								.opt("streamtypes"));
						odshow.put("display_status", videoJsonObject
								.opt("display_status"));
						
						logger.debug("display_status" + odshow.optString("display_status"));

						JSONArray episodesArray = videoJsonObject
								.optJSONArray("episodes");
						if (JSONUtil.isEmpty(episodesArray)) {
							continue;
						}
						JSONArray videosArray = new JSONArray();
						String firstEpisodeVideoUrl = null;
						int episodeLast = 0;
						for (int j = 0; j < episodesArray.length(); j++) {
							JSONObject video = episodesArray.optJSONObject(j);
							int videoid = MyUtil.decodeVideoUrl(video
									.optString("url"));
							video.put("pk_video", videoid);
							video.put("videoid", MyUtil.encodeVideoId(videoid));
							video.put("title", video.optString("name"));
							video.put("show_videoseq", video
									.optString("orderId"));
							video.put("show_videostage", video
									.optString("orderStage"));
							
							if(video.optInt("orderStage") > episodeLast) {
								episodeLast = video.optInt("orderStage");
							}

							videosArray.put(video);
							if(firstEpisodeVideoUrl == null) {
								firstEpisodeVideoUrl = video.optString("url");
							}
						}
						
						odshow.put("videos", videosArray);
						odshow.put("firstepisode_videourl", firstEpisodeVideoUrl);
						odshow.put("episode_last", episodeLast);
					} catch (JSONException e) {
					}
					showsArray.put(odshow);
					/**
					 * episode_last ！！ completed ！！ showtotal_vv ！！
					 * showtotal_comment ！！ reputation ！！ performer
					 * 
					 * screenwriter executive_producer issuer distributor
					 * production station tv_station
					 * 
					 * hasvideotype sortmode
					 * 
					 * ");
					 */
				}
			}
		}

	}

	/**
	 * @param optString
	 * @return
	 */
	private JSONArray getAlias(String alias) {
		if (alias == null || alias.length() == 0) {
			return null;
		}

		String[] aliasArr = alias.split("\\|");
		JSONArray aliasArray = new JSONArray();
		for (String string : aliasArr) {
			aliasArray.put(string);
		}

		return aliasArray;
	}

	/**
	 * @param showcategory
	 * @return
	 */
	private String getGenreName(String showcategory) {
		if (showcategory == null || showcategory.length() == 0) {
			return "";
		}
		if ("MOVIE".equals(showcategory)) {
			return "movie_genre";
		} else if ("TELEPLAY".equals(showcategory)) {
			return "tv_genre";
		} else if ("VARIETY".equals(showcategory)) {
			return "variety_genre";
		} else if ("ANIME".equals(showcategory)) {
			return "anime_genre";
		}
		return "";
	}

	/**
	 * @param showcategory
	 * @return
	 */
	private String getCategory(String showcategory) {
		if (showcategory == null || showcategory.length() == 0) {
			return "";
		}
		if ("MOVIE".equals(showcategory)) {
			return "电影";
		} else if ("TELEPLAY".equals(showcategory)) {
			return "电视剧";
		} else if ("VARIETY".equals(showcategory)) {
			return "综艺";
		} else if ("ANIME".equals(showcategory)) {
			return "动漫";
		}

		return "";
	}

	/**
	 * @param showObject
	 * @param fuzzyResult
	 * @throws JSONException
	 */
	private void setDisplayStatus(JSONObject showObject,
			MatcherResult fuzzyResult) throws JSONException {
		JSONArray hasvideotype = showObject.optJSONArray("hasvideotype");
		if (hasvideotype == null) {
			hasvideotype = new JSONArray();
			showObject.put("hasvideotype", hasvideotype);
		}
		
		logger.debug("showObject.optString display status" + showObject.optString("display_status"));
		if (showObject.optString("display_status").length() == 0) {   //如果是中间层获取的数据，这是字段是设置好的，不需要再处理
			if (showObject.optString("firstepisode_videourl", "").length() > 0) {
				// if (JSONUtil.contain(hasvideotype, "正片")) {
				if ("电影".equals(showObject.optString("showcategory"))) {
					showObject.put("display_status", "正片");
				} else {
					int completed = showObject.optInt("completed");
					int episode_total = showObject.optInt("episode_total");
					int episode_last = showObject.optInt("episode_last");

					if (completed == 1 && episode_total > 0) {
						if (episode_last > 0 && episode_total != episode_last && String.valueOf(episode_last).length() < 4) {
							showObject.put("display_status", "更新至" + episode_last);
						} else {
							showObject.put("display_status", episode_total + "集全");
							showObject.put("update_notice", "");
						}
					} else if (episode_last > 0) {
						String episode_last_str = String.valueOf(episode_last);
						if (episode_last_str.length() == 8) {// 8位日期综艺
							StringBuilder last_stage = new StringBuilder();
							last_stage.append(episode_last_str.substring(2, 4));
							last_stage.append("-");
							last_stage.append(episode_last_str.substring(4, 6));
							last_stage.append("-");
							last_stage.append(episode_last_str.substring(6, 8));

							showObject.put("episode_last_stage", last_stage);
							showObject.put("display_status", "更新至" + last_stage + "期");
						} else {
							showObject.put("display_status", "更新至" + episode_last + "集");
						}
					} else {
						showObject.put("display_status", "预告");
					}
				}
			} else if (JSONUtil.contain(hasvideotype, "预告片")) {
				showObject.put("display_status", "预告");
			} else {
				showObject.put("display_status", "资料");
			}
		}
		// 直接命中第几集或者某一期
		if (fuzzyResult != null && fuzzyResult.getLikeResult() != null
				&& fuzzyResult.getLikeResult().getProgram() != null) {

			// 直接命中第几集
			if (fuzzyResult.getLikeResult().getEpisode() != null) {
				Element[] elements = DictManager.getElements(fuzzyResult
						.getLikeResult().getProgram());
				int i = 0;
				for (; i < elements.length; i++) {
					Element element = elements[i];
					if (element.getType() == TokenType.TELEPLAY
							|| element.getType() == TokenType.ANIME) {
						break;
					}
				}

				showObject.put("currentEpisodeNumber", fuzzyResult
						.getLikeResult().getEpisode().getNormalValue());
			}
			// 命中综艺节目某天的节目
			else if (fuzzyResult.getLikeResult().getDate() != null) {
				Element[] elements = DictManager.getElements(fuzzyResult
						.getLikeResult().getProgram());
				int i = 0;
				for (; i < elements.length; i++) {
					Element element = elements[i];
					if (element.getType() == TokenType.VARIETY) {
						break;
					}
				}

				showObject.put("currentEpisodeNumber", fuzzyResult
						.getLikeResult().getDate().getNormalValue());
			}

		} else {
			// logger.debug("没有直接命中");
		}

		String name = StringUtils.trimToEmpty(showObject.optString("showname"));

		if (fuzzyResult != null && fuzzyResult.getPrefixWord() != null) {
			if (MyUtil.isSimilar(fuzzyResult.getPrefixWord(),name)) {
				showObject.put("highLightName", highlight(name, fuzzyResult
						.getPrefixWord()));
			} else {
				// 找别名
				JSONArray alias = showObject.optJSONArray("showalias");
				if (!JSONUtil.isEmpty(alias)) {
					for (int i = 0; i < alias.length(); i++) {
						String aliasName = alias.optString(i);
						if (MyUtil.isSimilar(fuzzyResult.getPrefixWord().toLowerCase(),aliasName )) {
							showObject.put("name", aliasName);
							showObject.put("highLightName", highlight(
									aliasName, fuzzyResult.getPrefixWord()));
							break;
						}
					}
				}

				showObject.put("aliasName", name);
			}

		}

	}

	/**
	 * @param optString
	 * @param showsArray
	 */
	private JSONArray filterPerson(String personid, JSONArray showsArray) {
		if (JSONUtil.isEmpty(showsArray)) {
			return showsArray;
		}
		if (personid == null || personid.length() == 0) {
			return showsArray;
		}

		JSONArray newShowsArray = new JSONArray();
		JSONObject showObject = null;
		for (int i = 0; i < showsArray.length(); i++) {
			showObject = showsArray.optJSONObject(i);
			if (JSONUtil.isEmpty(showObject)) {
				continue;
			}
			if(showObject.optString("firstepisode_videourl").length() == 0 && !shouldTrailerDisplay(showObject.optString("showcategory"), showObject.optInt("releaseyear"), showObject.optInt("releasemonth"))) {
				continue;
			}

			// 设置一些需要计算或设置的属性
			try {
				setDisplayStatus(showObject, null);
			} catch (JSONException e) {
			}

			JSONArray personArray = showObject.optJSONArray("person");
			if (JSONUtil.isEmpty(personArray)) {
				continue;
			}

			JSONObject personObject = null;

			for (int j = 0; j < personArray.length(); j++) {
				personObject = personArray.optJSONObject(j);
				if (personid.equals(personObject.optString("id"))) {
					newShowsArray.put(showObject);
					break;
				}
			}
			
			
		}

		return newShowsArray;
	}

	/**
	 * 获取系列节目
	 */
	public JSONObject getSeriesShow(String keyword, Category category)
			throws Exception {

		if (keyword == null || keyword.isEmpty()) {
			return null;
		}

		// 1 根据关键词找到一个series
		keyword = keyword.replace("全集", "");

		String query = "serieskeyword:" + keyword
				+ " state:normal showtotal:1-";

		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", "3");
		params.put("fd", "seriesname serieskeyword");
		params.put("cl", "search_result");

		JSONObject showseriesResultObject = NovaMiddleResource.search(
				"showseries", "showseries", keyword, params);

		JSONArray showseriesArray = JSONUtil.getProperty(
				showseriesResultObject, JSONArray.class, "results");

		if (JSONUtil.isEmpty(showseriesArray)) {
			return null;
		}

		JSONObject theShowseriesObject = null;

		// seriesname匹配
		for (int i = 0; i < showseriesArray.length(); i++) {
			JSONObject showseriesObject = showseriesArray.optJSONObject(i);
			if (showseriesObject == null) {
				continue;
			}

			String seriesname = showseriesObject.optString("seriesname");
			seriesname = seriesname.replace(" ", "");
			showseriesObject.put("seriesname", seriesname);

			if (seriesname.equals(keyword.replace(" ", ""))) {
				String seriesname_h = "<span class=\"highlight\">" + seriesname
						+ "</span>";
				showseriesObject.put("seriesname_h", seriesname_h);
				theShowseriesObject = showseriesObject;
				break;
			}
		}

		// serieskeyword匹配
		for (int i = 0; theShowseriesObject == null
				&& i < showseriesArray.length(); i++) {

			JSONObject showseriesObject = showseriesArray.optJSONObject(i);
			if (showseriesObject == null) {
				continue;
			}

			JSONArray serieskeywordArray = showseriesObject
					.optJSONArray("serieskeyword");

			if (JSONUtil.isEmpty(serieskeywordArray)) {
				continue;
			}

			for (int j = 0; j < serieskeywordArray.length(); j++) {
				String word = serieskeywordArray.optString(j);
				word = word.replace(" ", "");

				if (!word.equals(keyword.replace(" ", ""))) {
					continue;
				}

				String series_keyword = "<span class=\"highlight\">" + word
						+ "</span>";
				showseriesObject.put("series_keyword", series_keyword);

				String seriesname = showseriesObject.optString("seriesname");
				String regex = "(" + Pattern.quote(word) + ")";
				String seriesname_h = seriesname.replaceAll(regex,
						"<span class=\"highlight\">$1</span>");
				showseriesObject.put("seriesname_h", seriesname_h);

				theShowseriesObject = showseriesObject;
				break;
			}
		}

		if (theShowseriesObject == null) {
			return null;
		}

		// 2 根据找到的一个series，继续找属于他的show
		String categoryStr = "综艺,电视剧";
		if (category != null) {
			// 2011.3.17 目前序列节目只支持，综艺,电视剧
			if ("电视剧".equals(category.name)) {
				categoryStr = category.name;
			} else if ("综艺".equals(category.name)) {
				categoryStr = category.name;
			} else {
				return null;
			}
		}
		query = "seriesid:" + theShowseriesObject.optString("seriesid")
				+ " showcategory:" + categoryStr
				+ " state:normal allowfilter:1";

		params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", "7");
		params.put("ob", "showseq:desc");
		params.put("fd", "showname showcategory streamtypes episode_collected");
		params.put("cl", "search_result");

		JSONObject showResultObject = NovaMiddleResource.search("show", "show",
				query, params);

		JSONArray showArray = JSONUtil.getProperty(showResultObject,
				JSONArray.class, "results");

		if (JSONUtil.isEmpty(showArray)) {
			return null;
		}

		JSONArray showidArray = new JSONArray();
		JSONArray showArrayNew = new JSONArray();
		for (int i = 0; i < showArray.length(); i++) {
			JSONObject item = showArray.optJSONObject(i);
			if (item == null) {
				continue;
			}
			if (item.optInt("episode_collected") == 0) {
				continue;
			}

			showidArray.put(item.optString("showid"));
			showArrayNew.put(item);

			String showname = item.optString("showname");
			showname = showname.replace(theShowseriesObject
					.optString("seriesname"), "");
			item.put("showname", showname);
			item.put("showcateid", getCateId(item.optString("showcategory")));
		}

		if (showidArray.length() == 0) {
			return null;
		}

		// 3 查询第一个show对应的videos
		String firstShowid = showidArray.optString(0);
		JSONObject videos = getSeriesShowVideo(firstShowid, 0);

		theShowseriesObject.put("showids", showidArray);
		theShowseriesObject.put("shows", showArrayNew);
		theShowseriesObject.put("videos", videos);

		return theShowseriesObject;
	}

	/**
	 * 临时用，将中间层的分类，对应到搜索这边的分类ID，现在只处理几种
	 * 
	 * @param optString
	 * @return
	 */
	private int getCateId(String category) {
		if (category == null || category.length() == 0) {
			return 0;
		}
		if ("电视剧".equals(category)) {
			return 97;
		} else if ("电影".equals(category)) {
			return 96;
		} else if ("综艺".equals(category)) {
			return 85;
		} else if ("体育".equals(category)) {
			return 98;
		} else if ("教育".equals(category)) {
			return 87;
		}
		return 0;
	}

	/**
	 * 查询给定的showid的videos
	 */
	public JSONObject getSeriesShowVideo(String showid, int lastseq)
			throws Exception {

		// 1 找到showid对应的show的详细信息
		String query = "showid:" + showid;
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", "1");

		String fdValue = "state completed streamtypes sortmode episode_collected episode_total episode_last showcategory reputation showtotal_up showtotal_down performer host update_notice";
		params.put("fd", fdValue);

		params.put("cl", "search_result");

		JSONObject showResultObject = NovaMiddleResource.search("show", "show",
				query, params);

		JSONArray showArray = showResultObject.optJSONArray("results");

		if (JSONUtil.isEmpty(showArray)) {
			return null;
		}

		JSONObject showObject = showArray.optJSONObject(0);
		if (showObject == null
				|| !showObject.optString("state").equals("normal")) {
			return null;
		}

		int completed = showObject.optInt("completed");
		int episode_total = showObject.optInt("episode_total");
		int episode_last = showObject.optInt("episode_last");
		int hd = 0;
		int showcateid = getCateId(showObject.optString("showcategory"));

		JSONArray streamtypes = showObject.optJSONArray("streamtypes");
		if (streamtypes != null) {
			int length = streamtypes.length();
			for (int i = 0; i < length; i++) {
				String value = streamtypes.optString(i);
				if ("hd".equals(value)) {
					hd = 1;
					break;
				}
			}
		}

		int sortmode = showObject.optInt("sortmode");
		int episode_collected = showObject.optInt("episode_collected");

		// 2 查询show下的video
		query = "show_id:" + showid + " show_videotype:正片";
		params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		if (lastseq == 0) {
			params.put("pl", "24");
		} else {
			params.put("pl", "500");

			if (sortmode == 1) {
				query += " show_videoseq:" + "-" + (lastseq - 1);
			} else {
				query += " show_videoseq:" + (lastseq + 1) + "-";
			}
		}

		String fd_value = "title thumburl state show_videostage show_videoseq show_videocompleted";
		params.put("fd", fd_value);
		params.put("ob", "show_videoseq:" + (sortmode == 1 ? "desc" : "asc"));
		params.put("cl", "search_result");

		JSONObject videoResultObject = NovaMiddleResource.search("video",
				"show", query, params);

		if (JSONUtil.isEmpty(videoResultObject)) {
			return null;
		}
		JSONArray videoArray = videoResultObject.optJSONArray("results");

		if (JSONUtil.isEmpty(videoArray)) {
			return null;
		}

		int usemore = videoResultObject.optInt("total") > 24 ? 1 : 0;

		JSONObject firstVideo = videoArray.optJSONObject(0);
		JSONObject lastVideo = videoArray
				.optJSONObject(videoArray.length() - 1);

		lastseq = lastVideo.optInt("show_videoseq");

		JSONObject resultObject = new JSONObject();
		resultObject.put("hd", hd);
		resultObject.put("new", completed == 1 ? 0 : 1);
		resultObject.put("usemore", usemore);
		resultObject.put("lastseq", lastseq);
		resultObject.put("currentshowid", showid);
		resultObject.put("showcateid", showcateid);
		double repu = showObject.optDouble("reputation", 0);
		if (repu < 1) {
			repu = 5;
		}
		resultObject.put("avg_rating", new DecimalFormat("###.0").format(repu));
		resultObject.put("showtotal_up", showObject.opt("showtotal_up"));
		resultObject.put("showtotal_down", showObject.opt("showtotal_down"));
		resultObject.put("showcategory", showObject.opt("showcategory"));
		resultObject.put("update_notice", showObject.opt("update_notice"));
		if ("综艺".equals(showObject.optString("showcategory"))) {
			resultObject.put("person", getArrayString(showObject
					.optJSONArray("host")));
		} else {
			resultObject.put("person", getArrayString(showObject
					.optJSONArray("performer")));
		}

		for (int i = 0; i < videoArray.length(); i++) {

			JSONObject video = videoArray.optJSONObject(i);
			if (video == null) {
				continue;
			}

			if (video.optInt("show_videostage") == 0) {
				video.put("show_videostage", 0);
			}

			if (video.optInt("show_videoseq") == 0) {
				video.put("show_videoseq", 0);
			}

			if (video.optInt("show_videocompleted") == 0) {
				video.put("show_videocompleted", 0);
			}

			int show_videostage = video.optInt("show_videostage");
			if (show_videostage == 0) {
				continue;
			}

			if (show_videostage < 9999) {
				int max_len = String.valueOf(episode_collected).length();
				int this_len = String.valueOf(show_videostage).length();
				int fillzero = max_len - this_len;

				StringBuilder show_videostage_new = new StringBuilder();
				for (int j = 0; j < fillzero; j++) {
					show_videostage_new.append("0");
				}
				show_videostage_new.append(video.optInt("show_videostage"));
				show_videostage_new.append("集");

				video.put("show_videostage", show_videostage_new.toString());
				continue;
			}

			if (String.valueOf(show_videostage).length() == 8) {
				String show_videostage_s = String.valueOf(show_videostage);
				StringBuilder show_videostage_new = new StringBuilder();
				show_videostage_new.append(show_videostage_s.substring(0, 4));
				show_videostage_new.append("-");
				show_videostage_new.append(show_videostage_s.substring(4, 6));
				show_videostage_new.append("-");
				show_videostage_new.append(show_videostage_s.substring(6, 8));
				video.put("show_videostage", show_videostage_new.toString());
			}
		}

		resultObject.put("showthumburl", firstVideo.optString("thumburl"));
		// resultObject.put("firststage",
		// firstVideo.optString("show_videostage"));
		if (completed == 1 && episode_total > 0) {
			if (episode_last > 0 && episode_total != episode_last
					&& String.valueOf(episode_last).length() < 4) {
				resultObject.put("firststage", firstVideo
						.optString("show_videostage"));
			} else {
				resultObject.put("firststage", episode_total + "集全");
				resultObject.put("update_notice", "");
			}
		} else {
			resultObject.put("firststage", firstVideo
					.optString("show_videostage"));
		}
		resultObject.put("firsttitle", firstVideo.optString("title"));
		resultObject.put("firstvideoid", firstVideo.optString("videoid"));

		if (!firstVideo.optString("state").equals("normal")
				&& firstVideo.optString("state").equals("limited")
				|| firstVideo.optInt("show_videocompleted") == 0) {
			resultObject.put("firstvideodisabled", 1);
		} else {
			resultObject.put("firstvideodisabled", 0);
		}

		resultObject.put("videos", videoArray);
		return resultObject;
	}

	/**
	 * @param opt
	 * @return
	 */
	private JSONArray getArrayString(JSONArray persons) {
		if (JSONUtil.isEmpty(persons)) {
			return null;
		}
		JSONArray tmpArray = new JSONArray();
		for (int i = 0; i < persons.length(); i++) {
			Object object = persons.opt(i);
			if (object instanceof JSONObject) {
				JSONObject person = (JSONObject) object;
				tmpArray.put(person.optString("name"));
			} else {
				tmpArray.put(object);
			}
		}
		return tmpArray;
	}

	public JSONObject getOdshowsByPerson(String personname, Category category)
			throws Exception {

		if (personname == null || personname.isEmpty()) {
			return null;
		}

		String query = "personalias:" + personname + " episode_showcount:1-";

		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", "5");

		String fd_value = "personname personalias thumburl movie_showcount tv_showcount variety_showcount mv_showcount sports_showcount";
		params.put("fd", fd_value);
		params.put("cl", "search_result");

		JSONObject result = NovaMiddleResource.search("person", null, query,
				params);
		JSONArray results = result.optJSONArray("results");
		if (JSONUtil.isEmpty(results)) {
			return null;
		}

		JSONObject person_info = null;

		personname = personname.replace(" ", "");
		personname = personname.replace("?", "·");

		// 先查正名
		for (int i = 0; i < results.length(); i++) {
			JSONObject person = results.optJSONObject(i);
			if (person == null) {
				continue;
			}

			if (!person.optString("personname").replace(" ", "").replace("?",
					"·").equals(personname)) {
				continue;
			}

			person_info = person;
			break;
		}

		// 再查别名
		for (int i = 0; person_info == null && i < results.length(); i++) {
			JSONObject person = results.optJSONObject(i);
			if (person == null) {
				continue;
			}

			JSONArray personalias = person.optJSONArray("personalias");
			if (personalias == null) {
				continue;
			}

			for (int j = 0; j < personalias.length(); j++) {
				String alias = personalias.optString(j);
				if (!alias.replace(" ", "").replace("?", "·")
						.equals(personname)) {
					continue;
				}

				person_info = person;
				break;
			}
		}

		if (person_info == null) {
			return null;
		}

		// 取热播节目
		String categoryStr = "电影,电视剧,综艺,体育,教育";
		if (category != null) {
			// 2011.3.17 目前人物只支持，电影,电视剧,综艺,体育,教育
			if ("电影".equals(category.name)) {
				categoryStr = category.name;
			} else if ("电视剧".equals(category.name)) {
				categoryStr = category.name;
			} else if ("综艺".equals(category.name)) {
				categoryStr = category.name;
			} else if ("体育".equals(category.name)) {
				categoryStr = category.name;
			} else if ("教育".equals(category.name)) {
				categoryStr = category.name;
			} else {
				return null;
			}
		}
		query = "person:" + person_info.optString("personname")
				+ " showcategory:" + categoryStr
				+ " allowfilter:1 state:normal";
		params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", "4");

		fd_value = "showname show_thumburl completed episode_last streamtypes releaseyear releasemonth hasvideotype paid";
		params.put("fd", fd_value);

		params.put("ob", "showsum_vv:desc");
		params.put("cl", "search_result");

		result = listOdShow(query, params);

		// 有无预告片
		query = "person:" + person_info.optString("personname")
				+ " showcategory:" + categoryStr
				+ " hasvideotype:预告片 -hasvideotype:正片 state:normal";
		params.put("pn", "1");
		params.put("pl", "1");
		params.put("fd", "showid");
		params.put("cl", "search_result");

		JSONObject trailer = NovaMiddleResource.search("show", "show", query,
				params);
		int trailer_total = trailer.optInt("total");
		JSONArray trailer_results = trailer.optJSONArray("results");
		if (trailer_total > 0 && !JSONUtil.isEmpty(trailer_results)) {
			person_info.put("trailer_showcount", trailer_total);
		}

		if (JSONUtil.isEmpty(result.optJSONArray("results"))
				&& person_info.optInt("trailer_showcount") == 0) {
			return null;
		}

		// 热播里最少有一个有正片才显示
		int hotenabled = 0;
		if (!JSONUtil.isEmpty(result.optJSONArray("results"))) {
			JSONArray result_results = result.optJSONArray("results");
			for (int j = 0; hotenabled == 0 && j < result_results.length(); j++) {
				JSONObject hotshow = result_results.optJSONObject(j);
				if (hotshow == null) {
					continue;
				}

				JSONArray hasvideotype = hotshow.optJSONArray("hasvideotype");
				if (JSONUtil.contain(hasvideotype, "正片")) {
					hotenabled = 1;
					break;
				}
			}
		}
		person_info.put("hotenabled", hotenabled);

		// 不显示tab
		if (result.optInt("total") <= 4) {
			person_info.put("movie_showcount", 0);
			person_info.put("tv_showcount", 0);
			person_info.put("variety_showcount", 0);
			person_info.put("mv_showcount", 0);
			person_info.put("sports_showcount", 0);
		}

		if (person_info.optInt("movie_showcount") > 0) {
			if (!hasPersonShow(person_info.optString("personname"), "电影")) {
				person_info.put("movie_showcount", 0);
			}
		}
		if (person_info.optInt("tv_showcount") > 0) {
			if (!hasPersonShow(person_info.optString("personname"), "电视剧")) {
				person_info.put("tv_showcount", 0);
			}
		}
		if (person_info.optInt("variety_showcount") > 0) {
			if (!hasPersonShow(person_info.optString("personname"), "综艺")) {
				person_info.put("variety_showcount", 0);
			}
		}
		if (person_info.optInt("mv_showcount") > 0) {
			person_info.put("mv_showcount", 0);// 屏蔽音乐
			if (!hasPersonShow(person_info.optString("personname"), "音乐")) {
				person_info.put("mv_showcount", 0);
			}
		}
		if (person_info.optInt("sports_showcount") > 0) {
			if (!hasPersonShow(person_info.optString("personname"), "体育")) {
				person_info.put("sports_showcount", 0);
			}
		}

		result.put("person_info", person_info);
		return result;
	}

	/**
	 * 根据查询表达式，获得节目信息
	 */
	public JSONObject listOdShow(String query, Map<String, String> params)
			throws Exception {

		if (params == null) {
			params = new LinkedHashMap<String, String>();
		}
		String fd_value = params.get("fd");
		if (fd_value == null) {
			fd_value = "";
		}

		fd_value += " hasvideotype episode_total completed episode_last showcategory showlastupdate";
		params.put("fd", fd_value);

		JSONObject result = NovaMiddleResource.search("show", null, query,
				params);
		JSONArray results = JSONUtil.getProperty(result, JSONArray.class,
				"results");

		if (JSONUtil.isEmpty(results)) {
			return result;
		}

		for (int i = 0; i < results.length(); i++) {
			JSONObject item = results.optJSONObject(i);
			if (JSONUtil.isEmpty(item)) {
				continue;
			}
			item.put("showcateid", getCateId(item.optString("showcategory")));

			item.put("display_status", "");
			JSONArray hasvideotype = item.optJSONArray("hasvideotype");
			if (hasvideotype == null) {
				hasvideotype = new JSONArray();
				item.put("hasvideotype", hasvideotype);
			}

			if (JSONUtil.contain(hasvideotype, "正片")) {

				if ("电影".equals(item.optString("showcategory"))) {
					item.put("display_status", "正片");
				} else {
					int completed = item.optInt("completed");
					int episode_total = item.optInt("episode_total");
					int episode_last = item.optInt("episode_last");

					if (completed == 1 && episode_total > 0) {
						if (episode_last > 0 && episode_total != episode_last
								&& String.valueOf(episode_last).length() < 4) {
							item.put("display_status", "更新至" + episode_last);
						} else {
							item.put("display_status", episode_total + "集全");
							item.put("update_notice", "");
						}
					} else if (episode_last > 0) {
						String episode_last_str = String.valueOf(episode_last);
						if (episode_last_str.length() == 8) {// 8位日期综艺
							StringBuilder last_stage = new StringBuilder();
							last_stage.append(episode_last_str.substring(2, 4));
							last_stage.append("-");
							last_stage.append(episode_last_str.substring(4, 6));
							last_stage.append("-");
							last_stage.append(episode_last_str.substring(6, 8));

							item.put("episode_last_stage", last_stage);
							item.put("display_status", "更新至" + last_stage);
						} else {
							item.put("display_status", "更新至" + episode_last);
						}
					} else {
						item.put("display_status", "预告");
					}
				}
			} else if (JSONUtil.contain(hasvideotype, "预告片")) {
				item.put("display_status", "预告");
			} else {
				item.put("display_status", "资料");
			}
		}

		return result;
	}

	boolean hasPersonShow(String personname, String showcategory) {
		String query = "person:" + personname + " showcategory:" + showcategory
				+ " allowfilter:1 state:normal";

		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", "1");
		params.put("fd", "showid");
		params.put("cl", "search_result");

		JSONObject result = NovaMiddleResource.search("show", "show", query,
				params);

		JSONArray results = JSONUtil.getProperty(result, JSONArray.class,
				"results");

		return !JSONUtil.isEmpty(results);
	}

	/**
	 * 
	 * 2011.2.15 添加过滤在序列中显示的节目
	 * 
	 * @param keyword
	 * @param catename
	 * @param resultSeriesshow
	 * @return
	 * @throws Exception
	 */
	public JSONObject getOdshows(String keyword, Category category,
			JSONObject resultSeriesshow) throws Exception {

		if (keyword == null || keyword.isEmpty()) {
			return null;
		}

		keyword = keyword.replace("全集", "");

		if (keyword.length() == 0) {
			return null;
		}

		JSONArray odshowids = new JSONArray();
		if (resultSeriesshow != null) {
			odshowids = resultSeriesshow.optJSONArray("showids");
		}

		String src_keyword = keyword;// 保留相对原始的keyword

		String categoryStr = "电影,电视剧,综艺,体育,教育";
		if (category != null) {
			// 2011.3.17 目前节目只支持，电影,电视剧,综艺,体育,教育
			if ("电影".equals(category.name)) {
				categoryStr = category.name;
			} else if ("电视剧".equals(category.name)) {
				categoryStr = category.name;
			} else if ("综艺".equals(category.name)) {
				categoryStr = category.name;
			} else if ("体育".equals(category.name)) {
				categoryStr = category.name;
			} else if ("教育".equals(category.name)) {
				categoryStr = category.name;
			} else {
				return null;
			}
		}

		String query = "showcategory:" + categoryStr + " showkeyword:"
				+ keyword + " allowfilter:1 state:normal";

		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", "10");

		String fd_value = "showname showkeyword showalias show_thumburl completed episode_last episode_total director performer streamtypes showsum_vv showcategory showdesc paid firstepisode_videourl trailer_videourl reputation showtotal_up showtotal_down update_notice";
		params.put("fd", fd_value);

		params.put("ob", "showday_vv:desc");
		params.put("cl", "search_result");

		JSONObject result = listOdShow(query, params);
		JSONArray results = JSONUtil.getProperty(result, JSONArray.class,
				"results");

		if (JSONUtil.isEmpty(results)) {
			return null;
		}

		boolean isInc = false;
		if (keyword.length() >= 2) { // 长度决定完全相等还是包含关系
			isInc = true;
		}
		// TODO isInc = false;// 注：因为前面使用了模糊匹配算法，这里取消原来的"包含关系判断"

		int result_count = results.length();
		keyword = keyword.replace(" ", "");

		JSONObject foundResult = new JSONObject();

		for (int i = 0; i < result_count; i++) {
			JSONObject item = results.optJSONObject(i);
			if (item == null) {
				continue;
			}
			item.put("showcateid", getCateId(item.optString("showcategory")));
			double repu = item.optDouble("reputation", 0);
			if (repu < 1) {
				repu = 5;
			}
			item.put("avg_rating", new DecimalFormat("###.0").format(repu));
		}

		// showname完全匹配
		for (int i = 0; i < result_count; i++) {
			if (foundResult.length() > 1) {
				break;
			}

			JSONObject item = results.optJSONObject(i);
			if (item == null) {
				continue;
			}

			if (JSONUtil.contain(odshowids, item.optString("showid"))) {
				continue;
			}

			if (item.optString("showname").toLowerCase().replace(" ", "")
					.equals(keyword)) {
				String showname_h = "<span class=\"highlight\">"
						+ item.optString("showname") + "</span>";
				item.put("showname_h", showname_h);

				foundResult.put(item.optString("pk_odshow"), item);
			}
		}

		// showkeyword完全匹配
		if (foundResult.length() < 2) {
			for (int i = 0; i < result_count; i++) {
				if (foundResult.length() > 1) {
					break;
				}

				JSONObject item = results.optJSONObject(i);
				if (item == null) {
					continue;
				}

				if (JSONUtil.contain(odshowids, item.optString("showid"))) {
					continue;
				}

				if (foundResult.has(item.optString("pk_odshow"))) {
					continue;
				}

				JSONArray showkeyword = item.optJSONArray("showkeyword");
				if (JSONUtil.isEmpty(showkeyword)) {
					continue;
				}
				for (int j = 0; j < showkeyword.length(); j++) {
					String word = showkeyword.optString(j).toLowerCase();
					if (word.replace(" ", "").equals(keyword)) {
						String word_h = "<span class=\"highlight\">" + word
								+ "</span>";
						item.put("show_keyword", word_h);

						String regex = "(" + Pattern.quote(word) + ")";
						String showname_h = item.optString("showname")
								.replaceAll(regex,
										"<span class=\"highlight\">$1</span>");
						item.put("showname_h", showname_h);

						foundResult.put(item.optString("pk_odshow"), item);
						break;
					}
				}
			}
		}

		// showname左对齐匹配
		if (foundResult.length() < 2 && isInc) {
			for (int i = 0; i < result_count; i++) {
				if (foundResult.length() > 1) {
					break;
				}

				JSONObject item = results.optJSONObject(i);
				if (item == null) {
					continue;
				}

				if (JSONUtil.contain(odshowids, item.optString("showid"))) {
					continue;
				}

				if (foundResult.has(item.optString("pk_odshow"))) {
					continue;
				}

				if (item.optString("showname").toLowerCase()
						.startsWith(keyword)) {
					String regex = "(" + Pattern.quote(src_keyword) + ")";
					String showname_h = item.optString("showname").replaceAll(
							regex, "<span class=\"highlight\">$1</span>");
					item.put("showname_h", showname_h);

					foundResult.put(item.optString("pk_odshow"), item);
				}
			}
		}

		// showalias匹配
		if (foundResult.length() < 2) {
			for (int i = 0; i < result_count; i++) {
				if (foundResult.length() > 1) {
					break;
				}

				JSONObject item = results.optJSONObject(i);
				if (item == null) {
					continue;
				}

				if (JSONUtil.contain(odshowids, item.optString("showid"))) {
					continue;
				}

				if (foundResult.has(item.optString("pk_odshow"))) {
					continue;
				}

				JSONArray showalias = item.optJSONArray("showalias");
				if (JSONUtil.isEmpty(showalias)) {
					continue;
				}

				for (int j = 0; j < showalias.length(); j++) {
					String alias = showalias.optString(j).toLowerCase();

					if (isInc) {
						if (alias.replace(" ", "").startsWith(keyword)) {
							String regex = "(" + Pattern.quote(src_keyword)
									+ ")";
							String show_alias = alias.replaceAll(regex,
									"<span class=\"highlight\">$1</span>");
							item.put("show_alias", show_alias);

							foundResult.put(item.optString("pk_odshow"), item);
							break;
						}
					} else {
						if (alias.equals(keyword)) {
							String show_alias = "<span class=\"highlight\">"
									+ alias + "</span>";
							item.put("show_alias", show_alias);

							foundResult.put(item.optString("pk_odshow"), item);
							break;
						}
					}
				}
			}
		}

		if (foundResult.length() == 0) {
			return null;
		}

		return foundResult;
	}

	public void nullResultPage(WebParam param, JSONObject result)
			throws Exception {

		JSONObject outSearchResult = getOutSearchResult(param.getQ(), "1");
		result.put("outSearchResult", outSearchResult);

		JSONObject items = JSONUtil.getProperty(outSearchResult,
				JSONObject.class, "items");

		if (JSONUtil.isEmpty(items)) {
			JSONArray videos = NovaMiddleResourceUtil.recommendVideos();
			result.put("recommendVideos", videos);
		}
	}

	/**
	 * @param because
	 *            使用站外搜索原因 1无结果 2无版权
	 */
	public JSONObject getOutSearchResult(String keyword, String because) {

		if (keyword == null || keyword.isEmpty()) {
			return null;
		}
		// 2011.5.13 改为搜索新soku的ext数据
		JSONObject searchResult = new JSONObject();
		JSONObject ext = new JSONObject();
		MatcherResult fuzzyResult = ProgramMatcher.getSokuMatcher().match(
				StringUtils.trimToEmpty(keyword));
		String fuzzyKey = null;
		if (fuzzyResult != null) {
			fuzzyKey = fuzzyResult.getParamKeyAsString();
			// logger.info(keyword + ": 转换后的关键字为：" + fuzzyKey);

			// 调用Ext服务器来返回结果
			if (fuzzyKey != null && fuzzyKey.length() > 0) {
				StringBuffer serverURI = new StringBuffer("http://");
				serverURI.append(
						StringUtils.trimToEmpty(Config.getSokuExtHost()))
						.append("/");
				serverURI.append("/namearr/search");
				serverURI.append("?size=1000&names=").append(
						StringUtil.urlEncode(fuzzyKey, "utf-8"));

				// logger.debug("test111:::"+serverURI);

				try {
					byte[] bytes = Wget.get(serverURI.toString(), 3000);
					ext = new JSONObject(new String(bytes, "utf-8"));

					// filter(fuzzyResult,ext,keyword);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

		if (!JSONUtil.isEmpty(ext)) {
			// try {
			// logger.debug(ext.toString(4));
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			JSONArray extArray = new JSONArray();
			JSONObject movieObject = null;
			JSONObject tvObject = null;
			JSONObject animaObject = null;
			JSONObject varityObject = null;
			Set<Integer> proIDSet = new HashSet<Integer>();
			for (Iterator iterator = ext.keys(); iterator.hasNext();) {
				String key = (String) iterator.next();
				JSONObject showJsonObject = ext.optJSONObject(key);
				if (!JSONUtil.isEmpty(showJsonObject)) {
					try {
						String nameTypes = showJsonObject
								.optString("nameTypes");
						if ("MOVIE".equals(nameTypes)) {
							int count = getCount(showJsonObject
									.optJSONArray(nameTypes), proIDSet);
							if (count <= 0)
								continue;
							if (movieObject == null) {
								movieObject = new JSONObject();
								movieObject.put("logo", showJsonObject
										.optJSONArray(nameTypes).optJSONObject(
												0).optJSONObject("programme")
										.optString("pic"));
								movieObject.put("cat", "电影");
								movieObject.put("url", "/v?keyword="
										+ StringUtil
												.urlEncode(keyword, "utf-8")
										+ "&cateid=2");
								movieObject.put("count", count);
							} else {
								movieObject.put("count", movieObject
										.optInt("count")
										+ count);
							}
						} else if ("TELEPLAY".equals(nameTypes)) {
							int count = getCount(showJsonObject
									.optJSONArray(nameTypes), proIDSet);
							if (count <= 0)
								continue;
							if (tvObject == null) {
								tvObject = new JSONObject();
								tvObject.put("logo", showJsonObject
										.optJSONArray(nameTypes).optJSONObject(
												0).optJSONObject("programme")
										.optString("pic"));
								tvObject.put("cat", "电视剧");
								tvObject.put("url", "/v?keyword="
										+ StringUtil
												.urlEncode(keyword, "utf-8")
										+ "&cateid=1");
								tvObject.put("count", count);
							} else {
								tvObject.put("count", tvObject.optInt("count")
										+ count);
							}
						} else if ("VARIETY".equals(nameTypes)) {
							int count = getCount(showJsonObject
									.optJSONArray(nameTypes), proIDSet);
							if (count <= 0)
								continue;
							if (varityObject == null) {
								varityObject = new JSONObject();
								varityObject.put("logo", showJsonObject
										.optJSONArray(nameTypes).optJSONObject(
												0).optJSONObject("programme")
										.optString("pic"));
								varityObject.put("cat", "综艺");
								varityObject.put("url", "/v?keyword="
										+ StringUtil
												.urlEncode(keyword, "utf-8")
										+ "&cateid=3");
								varityObject.put("count", count);
							} else {
								varityObject.put("count", varityObject
										.optInt("count")
										+ count);
							}
						} else if ("ANIME".equals(nameTypes)) {
							int count = getCount(showJsonObject
									.optJSONArray(nameTypes), proIDSet);
							if (count <= 0)
								continue;
							if (animaObject == null) {
								animaObject = new JSONObject();
								animaObject.put("logo", showJsonObject
										.optJSONArray(nameTypes).optJSONObject(
												0).optJSONObject("programme")
										.optString("pic"));
								animaObject.put("cat", "动漫");
								animaObject.put("url", "/v?keyword="
										+ StringUtil
												.urlEncode(keyword, "utf-8")
										+ "&cateid=5");
								animaObject.put("count", count);
							} else {
								animaObject.put("count", animaObject
										.optInt("count")
										+ count);
							}
						} else if ("PERSON".equals(nameTypes)) {
							JSONObject personObjects = showJsonObject
									.getJSONObject(nameTypes);
							if (!JSONUtil.isEmpty(personObjects)) {
								String firstPersonKey = (String) personObjects
										.keys().next();
								JSONArray episodes = personObjects
										.getJSONObject(firstPersonKey)
										.optJSONArray("episodes");
								if (!JSONUtil.isEmpty(episodes)) {
									for (int i = 0; i < episodes.length(); i++) {
										JSONObject episode = episodes
												.optJSONObject(i);
										int pid = episode.optInt("pid");
										if (pid > 0) {
											if (proIDSet.contains(pid)) {
												continue;
											} else {
												proIDSet.add(pid);
											}
										}

										if ("电影".equals(episode
												.optString("type"))) {
											if (movieObject == null) {
												movieObject = new JSONObject();
												movieObject.put("logo", episode
														.optString("pic"));
												movieObject.put("cat", "电影");
												movieObject
														.put(
																"url",
																"/v?keyword="
																		+ StringUtil
																				.urlEncode(
																						keyword,
																						"utf-8")
																		+ "&cateid=2");
												movieObject.put("count", 1);
											} else {
												movieObject
														.put(
																"count",
																movieObject
																		.optInt("count") + 1);
											}
										} else if ("电视剧".equals(episode
												.optString("type"))) {
											if (tvObject == null) {
												tvObject = new JSONObject();
												tvObject.put("logo", episode
														.optString("pic"));
												tvObject.put("cat", "电视剧");
												tvObject
														.put(
																"url",
																"/v?keyword="
																		+ StringUtil
																				.urlEncode(
																						keyword,
																						"utf-8")
																		+ "&cateid=1");
												tvObject.put("count", 1);
											} else {
												tvObject.put("count", tvObject
														.optInt("count") + 1);
											}
										} else if ("综艺".equals(episode
												.optString("type"))) {
											if (varityObject == null) {
												varityObject = new JSONObject();
												varityObject
														.put(
																"logo",
																episode
																		.optString("pic"));
												varityObject.put("cat", "综艺");
												varityObject
														.put(
																"url",
																"/v?keyword="
																		+ StringUtil
																				.urlEncode(
																						keyword,
																						"utf-8")
																		+ "&cateid=3");
												varityObject.put("count", 1);
											} else {
												varityObject
														.put(
																"count",
																varityObject
																		.optInt("count") + 1);
											}
										} else if ("动漫".equals(episode
												.optString("type"))) {
											if (animaObject == null) {
												animaObject = new JSONObject();
												animaObject.put("logo", episode
														.optString("pic"));
												animaObject.put("cat", "动漫");
												animaObject.put("url", 
														"/v?keyword="+ 
														StringUtil.urlEncode(keyword,"utf-8")+ 
														"&cateid=5");
												animaObject.put("count", 1);
											} else {
												if (null == movieObject) {
													animaObject.put("count",1);
												} else {
													animaObject.put("count",
															movieObject.optInt("count") + 1);
												}
											}
										}
									}
								}
							}

						}
					} catch (JSONException e) {
					}

				}
			}
			if (movieObject != null) {
				extArray.put(movieObject);
			}
			if (tvObject != null) {
				extArray.put(tvObject);
			}
			if (varityObject != null) {
				extArray.put(varityObject);
			}
			if (animaObject != null) {
				extArray.put(animaObject);
			}

			if (!JSONUtil.isEmpty(extArray)) {
				try {
					searchResult.put("ext", extArray);
					searchResult.put("ext_info", ext);
				} catch (JSONException e) {
				}
			}

			// try {
			// logger.debug(extArray.toString(4));
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}

		// StringBuilder builder = new StringBuilder();
		// try {
		// ArrayList<String> ips = new ArrayList<String>();
		// // ips.add("10.102.23.31");
		// // ips.add("10.102.23.32");
		// // ips.add("10.102.23.41");
		// // ips.add("10.102.23.42");
		//			
		// //2011.2.15 http请求soku的内网ip 改一下：原来是10.102.23.31 32 41 42的轮循
		// ，改成：10.103.8.51 10.103.8.52 10.103.8.53 三台机器的轮循
		// // ips.add("10.103.8.51");
		// // ips.add("10.103.8.52");
		// // ips.add("10.103.8.53");
		// ips.addAll(Arrays.asList(Config.getSokuHosts()));
		//			
		//			
		// builder.append("http://");
		// builder.append(ips.get(new Random().nextInt(ips.size())));
		// builder.append("/9/5/2/7/search?");
		// builder.append("keyword=" + WebUtils.urlEncode(keyword));
		// builder.append("&");
		// builder.append("curpage=1&");
		// builder.append("pagesize=8&");
		// builder.append("hl=true&");
		//
		// builder.append("_because=");
		// builder.append(because);
		// builder.append("&");
		//
		// builder.append("exclude_sites=14&");// 不要搜索优酷的
		// //2011.3.7 去掉页面的日志记录，这里恢复记录日志
		// //builder.append("_ignore_log=1&");// 不要记录日志
		//
		// byte[] bytes = Wget.get(builder.toString(), 3000);
		// JSONObject jsonObject = new JSONObject(new String(bytes, "utf-8"));
		// return jsonObject;
		//
		// } catch (Exception e) {
		// logger.error(builder.toString()+"::"+e.getMessage(), e);
		// }

		return searchResult;
	}

	/**
	 * @param optJSONArray
	 * @return
	 */
	private int getCount(JSONArray optJSONArray, Set<Integer> proIDSet) {
		if (JSONUtil.isEmpty(optJSONArray)) {
			return 0;
		}
		int cnt = 0;
		for (int i = 0; i < optJSONArray.length(); i++) {
			if (!JSONUtil.isEmpty(optJSONArray.optJSONObject(i))
					&& !JSONUtil.isEmpty(optJSONArray.optJSONObject(i),
							"programme")) {
				int showid = optJSONArray.optJSONObject(i).optJSONObject(
						"programme").optInt("programmeId");
				if (proIDSet.contains(showid)) {
					continue;
				} else {
					cnt++;
					proIDSet.add(showid);
				}
			}
		}

		return cnt;
	}

	private static String highlight(String searchKey) {
		return "<span class=\"highlight\">" + searchKey + "</span>";
	}

	//不区分大小写
	private static String highlight(String name,String searchKey){
		return name.replaceFirst("(?i)"+searchKey,"<span class=\"highlight\">$0</span>");
	}

	public JSONObject get_bar(String barname) {

		barname = KeywordFilter.filterSymbol(barname);

		if (barname.isEmpty()) {
			return null;
		}

		// 1
		String query = "barname:" + barname;
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", "10");

		String fdValue = "barid barname category subcategory createtime closed subjectcount videocount membercount total_pv day_pv";
		params.put("fd", fdValue);

		params.put("ob", "barid:asc");
		params.put("cl", "search_result");

		JSONObject barResultObject = NovaMiddleResource.search("bar", null,
				query, params);

		JSONArray barArray = JSONUtil.getProperty(barResultObject,
				JSONArray.class, "results");

		if (JSONUtil.isEmpty(barArray)) {
			return null;
		}

		JSONObject bar = null;
		for (int i = 0; i < barArray.length(); i++) {
			JSONObject item = barArray.optJSONObject(i);
			if (item == null) {
				continue;
			}

			if (item.optString("barname").equalsIgnoreCase(barname)) {
				bar = item;
				break;
			}
		}

		return bar;
	}
}