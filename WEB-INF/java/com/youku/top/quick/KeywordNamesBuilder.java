//package com.youku.top.quick;
//
//import java.sql.Types;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.Map.Entry;
//
//import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
//import com.youku.soku.library.Utils;
//import com.youku.soku.library.orm.AnimeVersion;
//import com.youku.soku.library.orm.Movie;
//import com.youku.soku.library.orm.Music;
//import com.youku.soku.library.orm.Names;
//import com.youku.soku.library.orm.Person;
//import com.youku.soku.library.orm.TeleplayVersion;
//import com.youku.soku.library.orm.Variety;
//import com.youku.top.JdbcTemplateFactoray;
//import com.youku.top.dick.KeywordBuilder;
//import com.youku.top.directory_top.DirectoryInfoMgt;
//import com.youku.top.topn.util.KeywordUtil;
//import com.youku.top.util.VideoTypeUtil.VideoType;
//
//public class KeywordNamesBuilder {
//	
//	static Logger logger = Logger.getLogger(KeywordNamesBuilder.class);
//	
//	public static Map<String,Set<String>> teleplayNamesBuild(boolean hasEpisode){
//		Map<String,Set<String>> map = new HashMap<String, Set<String>>();
//		List<Names> names = KeywordBuilder.getNamesByCate(VideoType.teleplay.getValue());
//		List<TeleplayVersion> tvs = null;
//		Set<String> set = null;
//		List<String> vn = null;
//		List<String> tn = null;
//		StringBuilder name = null;
//		for(Names n:names){
//			tvs = KeywordBuilder.getTVByFkNamesId(n.getId());
//			if(null==tvs) continue;
//			set = new HashSet<String>();
//			tn = new ArrayList<String>();
//			tn.add(n.getName());
//			if (!StringUtils.isBlank(n.getAlias()))
//				tn.addAll(Utils.parseStr2List(n.getAlias(), "\\|"));
//			for(TeleplayVersion tv:tvs){
//				if(hasEpisode){
//				if(!KeywordBuilder.hasEpisodeCollectedFromTeleplay(tv.getId()))
//					continue;
//				}
//				vn = new ArrayList<String>();
//				if (!StringUtils.isBlank(tv.getName())) {
//					vn.add(tv.getName());
//					if (!StringUtils.isBlank(tv.getAlias()))
//						vn.addAll(Utils.parseStr2List(tv.getAlias(), "\\|"));
//				}
//				if (!StringUtils.isBlank(tv.getSearchkeys())) {
//					set.addAll(Utils.parseStr2List(tv.getSearchkeys(), "\\|"));
//				}
//				for (String t : tn) {
//					if(!StringUtils.isBlank(t)){
//						set.add(t);
//						if (null != vn && vn.size() > 0) {
//							for (String v : vn) {
//								name = new StringBuilder(t);
//								name.append(v);
//								set.add(name.toString());
//							}
//						}
//					}
//				}
//			}
//			
//			map.put(n.getName(), set);
//		}
//		
//		logger.info("电视剧系列:"+map.size());
//		
//		return map;
//	}
//	
//	public static Map<String,Set<String>> animeNamesBuild(boolean hasEpisode){
//		Map<String,Set<String>> map = new HashMap<String, Set<String>>();
//		List<Names> names = KeywordBuilder.getNamesByCate(VideoType.anime.getValue());
//		List<AnimeVersion> tvs = null;
//		Set<String> set = null;
//		List<String> vn = null;
//		List<String> tn = null;
//		StringBuilder name = null;
//		for(Names n:names){
//			tvs = KeywordBuilder.getAVByFkNamesId(n.getId());
//			if(null==tvs) continue;
//			set = new HashSet<String>();
//			tn = new ArrayList<String>();
//			tn.add(n.getName());
//			if (!StringUtils.isBlank(n.getAlias()))
//				tn.addAll(Utils.parseStr2List(n.getAlias(), "\\|"));
//			for(AnimeVersion tv:tvs){
//				if(hasEpisode){
//				if(!KeywordBuilder.hasEpisodeCollectedFromAnime(tv.getId()))
//					continue;
//				}
//				vn = new ArrayList<String>();
//				if (!StringUtils.isBlank(tv.getName())) {
//					vn.add(tv.getName());
//					if (!StringUtils.isBlank(tv.getAlias()))
//						vn.addAll(Utils.parseStr2List(tv.getAlias(), "\\|"));
//				}
//				if (!StringUtils.isBlank(tv.getSearchkeys())) {
//					set.addAll(Utils.parseStr2List(tv.getSearchkeys(), "\\|"));
//				}
//				for (String t : tn) {
//					if(!StringUtils.isBlank(t)){
//						set.add(t);
//						if (null != vn && vn.size() > 0) {
//							for (String v : vn) {
//								name = new StringBuilder(t);
//								name.append(v);
//								set.add(name.toString());
//							}
//						}
//					}
//				}
//			}
//			map.put(n.getName(), set);
//		}
//		logger.info("动漫系列:"+map.size());
//		return map;
//	}
//	
//	public static Map<String,Set<String>> movieNamesBuild(boolean hasEpisode){
//		Map<String,Set<String>> map = new HashMap<String, Set<String>>();
//		List<Names> names = KeywordBuilder.getNamesByCate(VideoType.movie.getValue());
//		List<Movie> tvs = null;
//		Set<String> set = null;
//		List<String> vn = null;
//		List<String> tn = null;
//		StringBuilder name = null;
//		for(Names n:names){
//			tvs = KeywordBuilder.getMovieByFkNamesId(n.getId());
//			if(null==tvs) continue;
//			set = new HashSet<String>();
//			tn = new ArrayList<String>();
//			tn.add(n.getName());
//			if (!StringUtils.isBlank(n.getAlias()))
//				tn.addAll(Utils.parseStr2List(n.getAlias(), "\\|"));
//			for(Movie tv:tvs){
//				if(hasEpisode){
//				if(!KeywordBuilder.hasEpisodeCollectedFromMovie(tv.getId()))
//					continue;
//				}
//				vn = new ArrayList<String>();
//				if (!StringUtils.isBlank(tv.getName())) {
//					vn.add(tv.getName());
//					if (!StringUtils.isBlank(tv.getAlias()))
//						vn.addAll(Utils.parseStr2List(tv.getAlias(), "\\|"));
//				}
//				if (!StringUtils.isBlank(tv.getSearchkeys())) {
//					set.addAll(Utils.parseStr2List(tv.getSearchkeys(), "\\|"));
//				}
//				for (String t : tn) {
//					if(!StringUtils.isBlank(t)){
//					set.add(t);
//					if (null != vn && vn.size() > 0) {
//						for (String v : vn) {
//							name = new StringBuilder(t);
//							name.append(v);
//							set.add(name.toString());
//							
//							if(!v.endsWith("版")&&!v.startsWith("第"))
//								set.add(v);
//						}
//					}
//					}
//				}
//			}
//			map.put(n.getName(), set);
//		}
//		logger.info("电影系列:"+map.size());
//		return map;
//	}
//	
//	public static Map<String,Set<String>> varietyNamesBuild(boolean hasEpisode){
//		Map<String,Set<String>> map = new HashMap<String, Set<String>>();
//		List<Names> names = KeywordBuilder.getNamesByCate(VideoType.variety.getValue());
//		List<Variety> tvs = null;
//		Set<String> set = null;
//		List<String> vn = null;
//		List<String> tn = null;
//		StringBuilder name = null;
//		for(Names n:names){
//			tvs = KeywordBuilder.getVarietyByFkNamesId(n.getId());
//			if(null==tvs) continue;
//			set = new HashSet<String>();
//			tn = new ArrayList<String>();
//			tn.add(n.getName());
//			if (!StringUtils.isBlank(n.getAlias()))
//				tn.addAll(Utils.parseStr2List(n.getAlias(), "\\|"));
//			for(Variety tv:tvs){
//				if(hasEpisode){
//				if(!KeywordBuilder.hasEpisodeCollectedFromVariety(tv.getId()))
//					continue;
//				}
//				vn = new ArrayList<String>();
//				if (!StringUtils.isBlank(tv.getName())) {
//					vn.add(tv.getName());
//				}
//				if (!StringUtils.isBlank(tv.getSearchkeys())) {
//					set.addAll(Utils.parseStr2List(tv.getSearchkeys(), "\\|"));
//				}
//				for (String t : tn) {
//					if(!StringUtils.isBlank(t)){
//					set.add(t);
//					if (null != vn && vn.size() > 0) {
//						for (String v : vn) {
//							name = new StringBuilder(t);
//							name.append(v);
//							set.add(name.toString());
//						}
//					}
//					}
//				}
//			}
//			
//			map.put(n.getName(), set);
//		}
//		logger.info("综艺系列:"+map.size());
//		return map;
//	}
//	
//	public static Map<String,Set<String>> personNamesBuild(){
//		Map<String,Set<String>> map = new HashMap<String, Set<String>>();
//		Map<Integer,Person> persons = DirectoryInfoMgt.getPersonMap();
//		Set<String> set = null;
//		String alias = null;
//		List<String> alias_list = null;
//		for(Entry<Integer, Person> entry:persons.entrySet()){
//			if(StringUtils.isBlank(entry.getValue().getName()))
//				continue;
//			set = new HashSet<String>();
//			alias_list = null;
//			alias = entry.getValue().getAlias();
//			set.add(entry.getValue().getName());
//			if(!StringUtils.isBlank(alias)){
//				alias_list = Utils.parseStr2List(alias, "\\|");
//				if(null!=alias_list&&alias_list.size()>0)
//					set.addAll(alias_list);
//			}
//			map.put(entry.getValue().getName(), set);
//		}
//		logger.info("人物:"+map.size());
//		return map;
//	}
//	
//	public static Map<String,Set<String>> musicNamesBuild(boolean hasEpisodes,boolean isCheckTop) {
//		Set<String> names = null;
//		Map<String,Set<String>> map = new HashMap<String, Set<String>>();
//		List<String> alias = null;
//		List<Music> tvs = KeywordBuilder.getAllMusics();
//		for (Music tv : tvs) {
//			if(isCheckTop){
//				if(1!=tv.getFlag())
//					continue;
//			}
//			names = new HashSet<String>();
//			alias = null;
//			if (StringUtils.isBlank(tv.getName())) {
//				continue;
//			}
//			if(hasEpisodes){
//				if(!KeywordBuilder.hasEpisodeCollectedFromMusic(tv.getId())){
//					continue;
//				}
//			}
//			names.add(tv.getName());
//			if (!StringUtils.isBlank(tv.getSearchkeys())) {
//				alias = Utils.parseStr2List(tv.getSearchkeys(), "\\|");
//				if(null!=alias)
//					names.addAll(alias);
//			}
//			map.put(tv.getName(), names);
//		}
//		logger.info("音乐系列:" + map.size());
//		return map;
//	}
//	
//	public static int getQueryCont(String keyword, String table) {
//		try {
//			return JdbcTemplateFactoray.searchStatDataSource.queryForInt("select sum(query_count) from "
//					+ table + " where keyword=? and query_type='video' and source='youku'",
//					new Object[] { keyword}, new int[] { Types.VARCHAR });
//		} catch (Exception e) {
//			logger.debug(keyword,e);
//			String word = KeywordUtil.analyzer(keyword);
//			if(keyword.equalsIgnoreCase(word)) return 0;
//			if(!StringUtils.isBlank(word)){
//				try{
//				return JdbcTemplateFactoray.searchStatDataSource.queryForInt("select sum(query_count) from "
//						+ table + " where keyword=? and query_type='video' and source='youku'",
//						new Object[] { word }, new int[] { Types.VARCHAR });
//				}catch(Exception e1){
//					logger.debug(keyword,e1);
//				}
//			}
//		}
//		return 0;
//	}
//	
//	public static int getQueryCont(Collection<String> keywords, String table) {
//		if(null==keywords||keywords.size()<1) return 0;
//		int sum = 0;
//		for(String keyword:keywords){
//			sum = sum+getQueryCont(keyword, table);
//		}
//		return sum;
//	}
//	
//	public static int getQueryContByMusic(String keyword, String table) {
//		try {
//			return JdbcTemplateFactoray.searchStatDataSource.queryForInt("select sum(query_count) from "
//					+ table + " where (keyword = ? or keyword = ? or keyword = ? or keyword= ? )  and query_type='video' and source='youku'",
//					new Object[] { keyword,keyword+" mv",keyword+" mtv","mv "+keyword}, new int[] { Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR });
//		} catch (Exception e) {
//			logger.debug(keyword,e);
//			String word = KeywordUtil.analyzer(keyword);
//			if(keyword.equalsIgnoreCase(word)) return 0;
//			if(!StringUtils.isBlank(word)){
//				try{
//				return JdbcTemplateFactoray.searchStatDataSource.queryForInt("select query_count from "
//						+ table + " where keyword=? and query_type='video' ",
//						new Object[] { word }, new int[] { Types.VARCHAR });
//				}catch(Exception e1){
//					logger.debug(keyword,e1);
//				}
//			}
//		}
//		return 0;
//	}
//}
