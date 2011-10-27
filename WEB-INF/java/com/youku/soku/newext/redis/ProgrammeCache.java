package com.youku.soku.newext.redis;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;

import com.youku.soku.library.load.Programme;
import com.youku.soku.newext.info.AliasInfo;
import com.youku.soku.newext.info.AnimeInfo;
import com.youku.soku.newext.info.DocumentaryInfo;
import com.youku.soku.newext.info.ExtInfoHolder;
import com.youku.soku.newext.info.MovieInfo;
import com.youku.soku.newext.info.PersonInfo;
import com.youku.soku.newext.info.TeleplayInfo;
import com.youku.soku.newext.info.VarietyInfo;
import com.youku.soku.newext.loader.FileLoaderAndSaver;
import com.youku.soku.newext.loader.UpdateRecommendation;
import com.youku.soku.newext.searcher.AnimeSearcher;
import com.youku.soku.newext.searcher.DocumentarySearcher;
import com.youku.soku.newext.searcher.MovieSearcher;
import com.youku.soku.newext.searcher.PeopleSearcher;
import com.youku.soku.newext.searcher.ProgrammeSearcher;
import com.youku.soku.newext.searcher.TeleplaySearcher;
import com.youku.soku.newext.searcher.VarietySearcher;
import com.youku.soku.newext.util.ChannelType;
import com.youku.soku.newext.util.RedisCacheUtil;

/**
 * @author tanxiuguang create on Oct 4, 2011
 */
public class ProgrammeCache {

	private Log logger = LogFactory.getLog(this.getClass());

	private Jedis jedis = new Jedis("10.103.8.53");

	public void catchProgrammeId(AliasInfo aliasInfo) {
		for (Integer programmeId : aliasInfo.id_programme.keySet()) {
			Programme programme = aliasInfo.id_programme.get(programmeId);
			JSONObject resultJson = null;
			if (programme != null) {

				MovieInfo minfo = null;
				TeleplayInfo tinfo = null;
				AnimeInfo ainfo = null;
				VarietyInfo vinfo = null;
				DocumentaryInfo docinfo = null;
				try {
					if (programme.getCate() == ChannelType.MOVIE.getValue()) {
						/** 电影 */
						minfo = ExtInfoHolder.getCurrentThreadLocal().movieInfo;
						resultJson = ProgrammeSearcher.genJson(programme, minfo, null);
						resultJson.put("channel", ChannelType.MOVIE.name());
					} else if (programme.getCate() == ChannelType.TELEPLAY.getValue()) {
						/** 电视剧 */
						tinfo = ExtInfoHolder.getCurrentThreadLocal().teleplayInfo;
						resultJson = ProgrammeSearcher.genJson(programme, tinfo, null);
						resultJson.put("channel", ChannelType.TELEPLAY.name());
					} else if (programme.getCate() == ChannelType.VARIETY.getValue()) {
						/** 综艺 */
						vinfo = ExtInfoHolder.getCurrentThreadLocal().varietyInfo;
						resultJson = ProgrammeSearcher.genJson(programme, vinfo, null);
						resultJson.put("channel", ChannelType.VARIETY.name());
					} else if (programme.getCate() == ChannelType.ANIME.getValue()) {
						/** 动漫 */
						ainfo = ExtInfoHolder.getCurrentThreadLocal().animeInfo;
						resultJson = ProgrammeSearcher.genJson(programme, ainfo, null);
						resultJson.put("channel", ChannelType.ANIME.name());
					} else if (programme.getCate() == ChannelType.DOCUMENTARY.getValue()) {
						docinfo = ExtInfoHolder.getCurrentThreadLocal().documentaryInfo;
						resultJson = ProgrammeSearcher.genJson(programme, docinfo, null);
						resultJson.put("channel", ChannelType.DOCUMENTARY.name());
					}

					jedis.set(programmeId + "", resultJson.toString());
				} catch (JSONException e) {
					logger.error(e.getMessage(), e);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}

			}
		}

	}

	public void catcheProgrammeName(AliasInfo aliasInfo, int category) {
		Set<String> seriesNameSet = aliasInfo.getSeries_programme().keySet();
		Set<String> programmeNameSet = aliasInfo.getName_programme().keySet();

		Set<String> namesSet = new HashSet<String>();

		logger.info("seriesNameSet size = " + seriesNameSet.size());
		logger.info("programeNameSet size = " + programmeNameSet.size());
		try {
			if (seriesNameSet != null) {
				namesSet.addAll(seriesNameSet);
			}

			if (programmeNameSet != null) {
				namesSet.addAll(programmeNameSet);
			}
		} catch (Exception e1) {
			logger.info("error on cate: " + aliasInfo.getCate_id());
			e1.printStackTrace();
		}

		for (String name : namesSet) {
			cacheProgrammeJson(name, category);
		}
	}

	public void cacheProgrammeJson(String name, int category) {
		try {
			JSONObject proJson = new JSONObject();

			proJson.put("nameTypes", category);
			proJson.put("searchKey", StringUtils.trimToEmpty(name));

			// for (int siteId : SiteUtil.getUsedSiteIds()) {}

			JSONArray searchResult = null;

			if (ChannelType.ANIME.getValue() == category) {
				searchResult = AnimeSearcher.searchByName(name, "");
			} else if (ChannelType.MOVIE.getValue() == category) {
				searchResult = MovieSearcher.searchByName(name, "");
			} else if (ChannelType.TELEPLAY.getValue() == category) {
				searchResult = TeleplaySearcher.searchByName(name, "");
			} else if (ChannelType.VARIETY.getValue() == category) {
				searchResult = VarietySearcher.searchByName(name, "");
			} else if (ChannelType.DOCUMENTARY.getValue() == category) {
				searchResult = DocumentarySearcher.searchByName(name, "");
				logger.info("name:" + name);
			}
			if (searchResult != null) {
				proJson.put(getCategoryStr(category), searchResult);
			}

			if(proJson != null) {
				jedis.set(RedisCacheUtil.getStoreKey(name, getCategoryStr(category)), proJson.toString());

				if (logger.isDebugEnabled()) {
					logger.debug("catch key: " + RedisCacheUtil.getStoreKey(name, getCategoryStr(category)));
				}
			}

		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
		}

	}

	private String getCategoryStr(int cateId) {
		String cateStr;
		switch (cateId) {
		case 1:
			cateStr = ChannelType.TELEPLAY.name();
			break;
		case 2:
			cateStr = ChannelType.MOVIE.name();
			break;
		case 3:
			cateStr = ChannelType.VARIETY.name();
			break;
		case 5:
			cateStr = ChannelType.ANIME.name();
			break;
		case 9:
			cateStr = ChannelType.DOCUMENTARY.name();
			break;
		default:
			cateStr = null;
		}
		
		return cateStr;
	}

	public void catchePerson(PersonInfo personInfo) {
		

		Set<String> personNameSet = personInfo.nameIdsMap.keySet();
		//List<String> personNameSet = Arrays.asList("小S", "小s", "Dee", "张嘉译");
		
		for (String personName : personNameSet) {
			JSONObject personJson = new JSONObject();
			try {
				personJson.put("nameTypes", ChannelType.PERSON.name());
				personJson.put("searchKey", StringUtils.trimToEmpty(personName));

				// for (int siteId : SiteUtil.getUsedSiteIds()) {}

				JSONObject personResult = PeopleSearcher.searchByName(personName, "");

				personJson.put(ChannelType.PERSON.name(), personResult);
				personJson.put("name", personName);

				jedis.set(RedisCacheUtil.getStoreKey(personName, ChannelType.PERSON.name()), personJson.toString());

				if (logger.isDebugEnabled()) {
					logger.debug("catch key: " + RedisCacheUtil.getStoreKey(personName, ChannelType.PERSON.name()));
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public void catchToRedis() {
		logger.info("start catch programe to redis");

		long start = System.currentTimeMillis();
		
		catchProgrammeId(ExtInfoHolder.getCurrentThreadLocal().aliasInfo);
		
		catcheProgrammeName(ExtInfoHolder.getCurrentThreadLocal().animeInfo, ChannelType.ANIME.getValue());

		catcheProgrammeName(ExtInfoHolder.getCurrentThreadLocal().movieInfo, ChannelType.MOVIE.getValue());
		catcheProgrammeName(ExtInfoHolder.getCurrentThreadLocal().teleplayInfo, ChannelType.TELEPLAY.getValue());
		catcheProgrammeName(ExtInfoHolder.getCurrentThreadLocal().varietyInfo, ChannelType.VARIETY.getValue());
		

		catcheProgrammeName(ExtInfoHolder.getCurrentThreadLocal().documentaryInfo, ChannelType.DOCUMENTARY.getValue());

	
		
		
		catchePerson(ExtInfoHolder.getCurrentThreadLocal().personInfo);
		long end = System.currentTimeMillis();

		logger.info("catch to redis end, cost: " + (end - start));
	}

	public static void main(String[] args) {
		// 初始化logger
		String log4j = args[0];
		DOMConfigurator.configure(log4j);

		FileLoaderAndSaver loaderAndSaver = new FileLoaderAndSaver();
		loaderAndSaver.load();
		
		new UpdateRecommendation().doUpdate();

		new ProgrammeCache().catchToRedis();
	}

}
