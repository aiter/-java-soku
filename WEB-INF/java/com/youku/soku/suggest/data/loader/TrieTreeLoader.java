package com.youku.soku.suggest.data.loader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.youku.search.pool.net.util.Cost;
import com.youku.search.recomend.Constance;
import com.youku.search.util.DataFormat;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.shield.ShieldInfo;
import com.youku.soku.shield.DataLoader;
import com.youku.soku.shield.Filter;
import com.youku.soku.shield.ShieldWordsInfo;
import com.youku.soku.suggest.data.serialize.ObjectSaverAndLoader;
import com.youku.soku.suggest.entity.NamesEntity;
import com.youku.soku.suggest.entity.VideoCheckResult;
import com.youku.soku.suggest.parser.KeywordsParser;
import com.youku.soku.suggest.parser.WordProcessor;
import com.youku.soku.suggest.trie.Entity;
import com.youku.soku.suggest.trie.EntityFlagUtil;
import com.youku.soku.suggest.trie.TrieTree;
import com.youku.soku.suggest.util.WordUtil;
import com.youku.soku.util.DataBase;

public class TrieTreeLoader {

	private final Logger log = Logger.getLogger(this.getClass());

	private static final int TOTAL_LOAD_NUMBER = 500000;

	private static final int ONCE_LOAD_NUMBER = 500000;

	private TrieTree tree;

	public TrieTreeLoader(TrieTree tree) {
		this.tree = tree;
	}

	public void init() {
		try {

			System.out.format("total memeor: %1d, maxMemeory: %2d, freeMemeor: %3d\n", Runtime.getRuntime().totalMemory(), Runtime.getRuntime().maxMemory(),
					Runtime.getRuntime().freeMemory());
			long memStart = Runtime.getRuntime().freeMemory();
			long startTime = System.currentTimeMillis();
			log.info("creating recomend tree create ");
			createTreeByDB(tree);
			System.out.format("total memeor: %1d, maxMemeory: %2d, freeMemeor: %3d\n", Runtime.getRuntime().totalMemory(), Runtime.getRuntime().maxMemory(),
					Runtime.getRuntime().freeMemory());
			long memEnd = Runtime.getRuntime().freeMemory();
			log.info("recomend tree create success,use_memory:" + (memStart - memEnd));
			log.info("Nodes count: " + tree.getCount());
			// log.info("Nodes count: " + tree.getAllKeys());
			// ObjectSaverAndLoader saver = new ObjectSaverAndLoader("/opt/trietree/", "trietree");
			// saver.save(tree.getRoot());
			long endTime = System.currentTimeMillis();
			log.info("Build Tree cost: " + (endTime - startTime));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createTreeByDB(TrieTree namesTree) {
		Cost cost = new Cost();
		cost.updateStart();
		WordUtil.init();
		ShieldWordsInfo wordsInfo = new ShieldWordsInfo();
		new DataLoader(wordsInfo).init();

		Map<String, Entity> keywordsMap = new HashMap<String, Entity>();

		int startIndex = 0;
		for (int i = 0; i < TOTAL_LOAD_NUMBER / ONCE_LOAD_NUMBER; i++) {
			loadDataFromDb(wordsInfo, keywordsMap, startIndex);
			startIndex += ONCE_LOAD_NUMBER;
		}
		log.info("finished read data from db");
		wordsInfo.clear();
		Set<String> personSet = new HashSet<String>();
		Set<String> teleplaySet = new HashSet<String>();
		Set<String> movieSet = new HashSet<String>();
		Set<String> animSet = new HashSet<String>();
		Set<String> varietyList = new HashSet<String>();
		
		
		for (String keyword : keywordsMap.keySet()) {

			Entity et = keywordsMap.get(keyword);
			VideoCheckResult csv;
			try {
				if (WordUtil.isPerson(keyword)) {
					personSet.add(keyword); 
				}
				csv = WordUtil.isVideo(keyword);
			
				if (csv.isVideo()) {
					if (!StringUtils.isBlank(csv.getViewUrl())) {
						namesTree.addUrlToMap(et.getWord(), csv.getViewUrl());
					}
					if (csv.isTeleplay()) {
						et.setWord(csv.getTeleplayName());
						et.setFlag(EntityFlagUtil.TELEPLAYFLAG);
						teleplaySet.add(csv.getTeleplayName());
					} 
					if (csv.isMovie()) {
						movieSet.add(csv.getMovieName());						
						et.setFlag(EntityFlagUtil.MOVIEODERFLAG);
					} 
					if(csv.isAnime()) {
						animSet.add(csv.getAnimeName());
					} 
					
					if(csv.isVariety()) {
						varietyList.add(csv.getVarietyName());
					}

				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			namesTree.insert(et);
		}
		cost.updateEnd();
		log.info("build normal trie tree cost :" + cost.getCost());

		log.info("MovieList size" + movieSet.size());
		log.info("teleplayList size" + teleplaySet.size());
		log.info("personList size" + personSet.size());
		log.info("animeList size" + animSet.size());
		log.info("varietyList size" + varietyList.size());
		

		namesTree.handleTeleplayName(teleplaySet);
		namesTree.handleMovieName(movieSet);
		namesTree.handleNewestVariety();
		namesTree.handleAnime(animSet);		

		cost.updateStart();
		namesTree.handlePersonName(personSet);
		cost.updateEnd();
		log.info("Add person info trie tree cost " + cost.getCost());


		WordUtil.dispose();
		personSet.clear();
		teleplaySet.clear();
		movieSet.clear();
		keywordsMap.clear();
		cost.updateEnd();
		log.info("add other info cost :" + cost.getCost());

		log.info("begin to clear useless data");
		cost.updateStart();
		namesTree.clearUselessData();
		cost.updateEnd();
		log.info("begin to clear useless data :" + cost.getCost());
		log.info("clear Useless Data " + namesTree.getClearUselessDataTime() + "times");
		
	
	}

	private void loadDataFromDb(ShieldWordsInfo wordsInfo, Map<String, Entity> keywordsMap, int start) {

		String date = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -2), DataFormat.FMT_DATE_YYYY_MM_DD);
		String table = new StringBuffer("query_").append(date).toString();
		log.info("Load data from " + start + "  --  " + (start + ONCE_LOAD_NUMBER));

		// String sql="select * from " + table + " where keyword like '中国式离婚%' and query_count > 5 order by query_count";
		Connection conn = null;
		PreparedStatement pt = null;
		ResultSet rs = null;

		long startTime = System.currentTimeMillis();
		String sql = "select id, keyword,keyword_py,query_type,query_count,result from " + table
				+ " where source = 'youku' and query_count > 3 and result > 1 order by query_count desc limit " + start + ", " + ONCE_LOAD_NUMBER;

		try {
			conn = DataBase.getSearchStatConn();
			pt = conn.prepareStatement(sql);
			rs = pt.executeQuery();
			long endTime = System.currentTimeMillis();
			log.info(" ================= " + (endTime - startTime));
			while (rs.next()) {
				Entity et = new Entity();
				String orginalKeyword = rs.getString("keyword");
				ShieldInfo shieldInfo = Filter.getInstance().isShieldWord(orginalKeyword, wordsInfo);
				if (shieldInfo != null && shieldInfo.isMatched()) {
					continue;
				}


				/*
				 * boolean flag=FilterUtils.isFilter(keyword); if(flag) continue;
				 */
				// keyword_py=rs.getString("keyword_py");
				String query_type = rs.getString("query_type");
				int query_count = rs.getInt("query_count");
				// result=rs.getInt("result");
				String keyword = "";
				if (WordUtil.isSpecialVideo(orginalKeyword) || WordUtil.isAnime(orginalKeyword)) {
					keyword = orginalKeyword;
				} else {
					keyword = WordProcessor.processWord(orginalKeyword);
				}

				shieldInfo = Filter.getInstance().isShieldWord(keyword, wordsInfo);
				if (shieldInfo != null && shieldInfo.isMatched()) {
					continue;
				}

				if (WordUtil.isInterventionBlockWord(keyword)) {
					continue;
				}
				
				// 搜索词长度大于25的不提示
				if (WordProcessor.getStringLength(keyword) > 12) {
					continue;
				}
				et.setId(rs.getInt("id"));
				et.setQueryCount(query_count);
				
				et.setWord(keyword);
				Entity entity = keywordsMap.get(keyword);

				if (query_type.equalsIgnoreCase(Constance.VIDEO)) {
					if (entity != null) {
						entity.setQueryCount(entity.getQueryCount() + query_count);
						keywordsMap.put(keyword, entity);
					} else {
						keywordsMap.put(keyword, et);
					}
				}

			}

		} catch (Exception e) {
			log.info("===Date read from database error!");
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (null != rs)
					rs.close();
				if (null != pt)
					pt.close();
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				log.info("db close error!");
				log.error(e.getMessage(), e);
			}

		}

	}
}
